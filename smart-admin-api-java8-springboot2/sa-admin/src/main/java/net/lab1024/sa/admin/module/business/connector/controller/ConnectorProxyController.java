package net.lab1024.sa.admin.module.business.connector.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.connector.dao.ConnectorRouteDao;
import net.lab1024.sa.admin.module.business.connector.dao.ConnectorRouteLogDao;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorRouteEntity;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorRouteLogEntity;
import net.lab1024.sa.admin.module.business.connector.service.ConnectorTransformService;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * Connector 代理控制器
 * <p>
 * 负责处理路由转发、数据转换和请求日志记录
 * 
 * @author connector
 * @date 2026-01-22
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Hidden // 在 Swagger 中隐藏此代理接口
public class ConnectorProxyController {

    /** 代理路径前缀 */
    private static final String PROXY_PREFIX = "/connector/proxy";
    
    /** 测试请求标识请求头 */
    private static final String TEST_HEADER_NAME = "X-Connector-Test";
    
    /** 测试请求标识值 */
    private static final String TEST_HEADER_VALUE = "true";
    
    /** 路由状态：激活 */
    private static final String ROUTE_STATUS_ACTIVE = "active";
    
    /** 不需要转发的请求头：Host */
    private static final String HEADER_HOST = "host";
    
    /** 不需要转发的请求头：Content-Length */
    private static final String HEADER_CONTENT_LENGTH = "content-length";
    
    /** 错误响应字段名 */
    private static final String ERROR_FIELD = "error";

    private final ConnectorRouteDao connectorRouteDao;
    private final ConnectorRouteLogDao connectorRouteLogDao;
    private final ConnectorTransformService connectorTransformService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理代理请求
     * <p>
     * 1. 解析请求路径，匹配路由规则
     * 2. 根据路由配置进行数据转换
     * 3. 转发请求到目标服务
     * 4. 记录请求日志
     * 
     * @param request HTTP 请求对象
     * @param body 请求体（可选）
     * @return 目标服务的响应
     */
    @SaIgnore
    @RequestMapping("/connector/proxy/**")
    public ResponseEntity<Object> handleRequest(HttpServletRequest request, @RequestBody(required = false) Object body) {
        // 获取完整请求路径
        String fullPath = request.getRequestURI();
        
        // 移除代理前缀，获取实际的源路径
        // 例如：/connector/proxy/api/order -> /api/order
        String path = normalizeRequestPath(fullPath);
        
        // 获取 HTTP 请求方法
        String method = request.getMethod();

        // 记录请求开始时间，用于计算延迟
        long startTime = System.currentTimeMillis();
        
        // 步骤 1：根据路径和方法查找匹配的路由规则
        ConnectorRouteEntity route = findRoute(path, method, request);
        
        if (route == null) {
            log.warn("未找到匹配的路由规则, path: {}, method: {}", path, method);
            return ResponseEntity.notFound().build();
        }

        log.info("处理代理请求, routeName: {}, sourcePath: {}, targetUrl: {}", 
                 route.getName(), path, route.getTargetUrl());

        try {
            // 步骤 2：根据映射配置转换请求数据
            Object transformedBody = connectorTransformService.transform(body, route.getMappingConfig());

            // 步骤 3：转发请求到目标服务
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // 构建目标请求
                HttpUriRequestBase proxyRequest = new HttpUriRequestBase(route.getMethod(), URI.create(route.getTargetUrl()));
                
                // 复制原始请求头（排除 Host 和 Content-Length）
                copyRequestHeaders(request, proxyRequest);
                
                // 添加路由配置中的自定义请求头
                addCustomHeaders(proxyRequest, route.getMappingConfig());

                // 设置请求体
                if (transformedBody != null) {
                    String jsonBody = objectMapper.writeValueAsString(transformedBody);
                    proxyRequest.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
                }

                // 执行请求并处理响应
                try (CloseableHttpResponse response = httpClient.execute(proxyRequest)) {
                    int statusCode = response.getCode();
                    String respBody = EntityUtils.toString(response.getEntity());
                    
                    // 步骤 4：记录请求日志（成功）
                    long latency = System.currentTimeMillis() - startTime;
                    
                    // 检查业务错误 (code != 0)
                    String errorMsg = null;
                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            JsonNode jsonNode = objectMapper.readTree(respBody);
                            if (jsonNode.has("code")) {
                                int code = jsonNode.get("code").asInt();
                                if (code != 0) {
                                    // 提取错误信息
                                    if (jsonNode.has("message")) {
                                        errorMsg = jsonNode.get("message").asText();
                                    } else if (jsonNode.has("msg")) {
                                        errorMsg = jsonNode.get("msg").asText();
                                    } else {
                                        errorMsg = "Business error (code=" + code + ")";
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // 解析JSON失败，忽略业务错误检查
                        }
                    }

                    String originalParams = body != null ? objectMapper.writeValueAsString(body) : null;
                    String transformedParams = transformedBody != null ? objectMapper.writeValueAsString(transformedBody) : null;
                    logRequest(route.getId(), route.getChannel(), path, statusCode, latency, errorMsg, originalParams, transformedParams, respBody);

                    log.info("代理请求完成, statusCode: {}, latency: {}ms", statusCode, latency);

                    // 尝试解析响应为 JSON 对象
                    Object responseData = parseResponseBody(respBody);

                    return ResponseEntity.status(statusCode).body(responseData);
                }
            }

        } catch (Exception e) {
            // 记录请求日志（失败）
            long latency = System.currentTimeMillis() - startTime;
            String originalParams = null;
            try {
                originalParams = body != null ? objectMapper.writeValueAsString(body) : null;
            } catch (Exception ignored) {}
            
            String channel = route != null ? route.getChannel() : null;
            String routeId = route != null ? route.getId() : null;
            
            logRequest(routeId, channel, path, 500, latency, e.getMessage(), originalParams, null, null);
            
            log.error("代理请求失败, path: {}, error: {}", path, e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.singletonMap(ERROR_FIELD, e.getMessage()));
        }
    }
    
    /**
     * 标准化请求路径
     * <p>
     * 移除代理前缀，确保路径以 / 开头
     * 
     * @param fullPath 完整请求路径
     * @return 标准化后的路径
     */
    private String normalizeRequestPath(String fullPath) {
        String path = fullPath.replaceFirst("^" + PROXY_PREFIX, "");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
    
    /**
     * 复制请求头
     * <p>
     * 将原始请求的请求头复制到代理请求，排除 Host 和 Content-Length
     * 
     * @param request 原始请求
     * @param proxyRequest 代理请求
     */
    private void copyRequestHeaders(HttpServletRequest request, HttpUriRequestBase proxyRequest) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // 跳过 Host 和 Content-Length 请求头
            if (!name.equalsIgnoreCase(HEADER_HOST) && !name.equalsIgnoreCase(HEADER_CONTENT_LENGTH)) {
                proxyRequest.setHeader(name, request.getHeader(name));
            }
        }
    }
    
    /**
     * 添加自定义请求头
     * <p>
     * 从路由配置中获取自定义请求头并添加到代理请求
     * 
     * @param proxyRequest 代理请求
     * @param mappingConfig 映射配置
     */
    private void addCustomHeaders(HttpUriRequestBase proxyRequest, Map<String, Object> mappingConfig) {
        Map<String, String> customHeaders = connectorTransformService.getCustomHeaders(mappingConfig);
        for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
            proxyRequest.setHeader(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 解析响应体
     * <p>
     * 尝试将响应体解析为 JSON 对象，解析失败则返回原始字符串
     * 
     * @param respBody 响应体字符串
     * @return 解析后的对象
     */
    private Object parseResponseBody(String respBody) {
        try {
            return objectMapper.readValue(respBody, Object.class);
        } catch (Exception e) {
            // 解析失败，返回原始字符串
            return respBody;
        }
    }

    /**
     * 记录请求日志
     * <p>
     * 将代理请求的执行结果保存到数据库
     * 
     * @param routeId 路由 ID
     * @param channel 渠道
     * @param path 请求路径
     * @param statusCode HTTP 状态码
     * @param latency 请求延迟（毫秒）
     * @param error 错误信息（成功时为 null）
     * @param originalParams 原始参数
     * @param transformedParams 转换后参数
     * @param responseData 响应数据
     */
    private void logRequest(String routeId, String channel, String path, int statusCode, long latency, String error, 
                            String originalParams, String transformedParams, String responseData) {
        try {
            ConnectorRouteLogEntity log = new ConnectorRouteLogEntity();
            log.setRouteId(routeId);
            log.setChannel(channel);
            log.setRequestPath(path);
            log.setStatusCode(statusCode);
            log.setLatencyMs(latency);
            log.setErrorMsg(error);
            log.setOriginalParams(originalParams);
            log.setTransformedParams(transformedParams);
            log.setResponseData(responseData);
            log.setCreateTime(LocalDateTime.now());
            connectorRouteLogDao.insert(log);
        } catch (Exception e) {
            // 日志记录失败不影响主流程
            log.error("记录请求日志失败, routeId: {}, error: {}", routeId, e.getMessage());
        }
    }

    /**
     * 查找匹配的路由规则
     * <p>
     * 根据请求路径和方法查找对应的路由配置
     * 测试模式下不检查路由状态，允许访问非激活状态的路由
     * 
     * @param path 请求路径
     * @param method HTTP 方法
     * @param request 原始请求对象
     * @return 匹配的路由实体，未找到返回 null
     */
    private ConnectorRouteEntity findRoute(String path, String method, HttpServletRequest request) {
        QueryWrapper<ConnectorRouteEntity> queryWrapper = new QueryWrapper<ConnectorRouteEntity>()
                .eq("source_path", path)
                .eq("method", method);
        
        // 检查是否为测试请求
        String testHeader = request.getHeader(TEST_HEADER_NAME);
        boolean isTestMode = testHeader != null && testHeader.equalsIgnoreCase(TEST_HEADER_VALUE);
        
        if (!isTestMode) {
            // 非测试模式下，仅查询激活状态的路由
            queryWrapper.eq("status", ROUTE_STATUS_ACTIVE);
        }
        
        return connectorRouteDao.selectOne(queryWrapper);
    }
}
