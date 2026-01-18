package com.connector.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.connector.entity.Route;
import com.connector.entity.RouteLog;
import com.connector.mapper.RouteLogMapper;
import com.connector.mapper.RouteMapper;
import com.connector.service.TransformService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final RouteMapper routeMapper;
    private final RouteLogMapper routeLogMapper;
    private final TransformService transformService;

    @RequestMapping("/**")
    public ResponseEntity<Object> handleRequest(HttpServletRequest request, @RequestBody(required = false) Object body) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip api/admin requests (handled by AdminController)
        if (path.startsWith("/api/admin")) {
            return null; // Spring will try next handler or 404 if not found
        }

        long startTime = System.currentTimeMillis();
        
        // 1. Find Route
        Route route = routeMapper.selectOne(new QueryWrapper<Route>()
                .eq("source_path", path)
                .eq("method", method)
                .eq("status", "active"));

        if (route == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 2. Transform
            Object transformedBody = transformService.transform(body, route.getMappingConfig());

            // 3. Forward
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpUriRequestBase proxyRequest = new HttpUriRequestBase(route.getMethod(), URI.create(route.getTargetUrl()));
                
                // Copy headers
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("content-length")) {
                        proxyRequest.setHeader(name, request.getHeader(name));
                    }
                }
                
                // Add Custom Headers
                Map<String, String> customHeaders = transformService.getCustomHeaders(route.getMappingConfig());
                for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                    proxyRequest.setHeader(entry.getKey(), entry.getValue());
                }

                if (transformedBody != null) {
                    String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(transformedBody);
                    proxyRequest.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
                }

                try (CloseableHttpResponse response = httpClient.execute(proxyRequest)) {
                    int statusCode = response.getCode();
                    String respBody = EntityUtils.toString(response.getEntity());
                    
                    // 4. Log
                    logRequest(route.getId(), path, statusCode, System.currentTimeMillis() - startTime, null);

                    // Parse response if JSON
                    Object responseData = respBody;
                    try {
                        responseData = new com.fasterxml.jackson.databind.ObjectMapper().readValue(respBody, Object.class);
                    } catch (Exception ignored) {}

                    return ResponseEntity.status(statusCode).body(responseData);
                }
            }

        } catch (Exception e) {
            logRequest(route.getId(), path, 500, System.currentTimeMillis() - startTime, e.getMessage());
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    private void logRequest(String routeId, String path, int statusCode, long latency, String error) {
        RouteLog log = new RouteLog();
        log.setRouteId(routeId);
        log.setRequestPath(path);
        log.setStatusCode(statusCode);
        log.setLatencyMs(latency);
        log.setErrorMsg(error);
        routeLogMapper.insert(log);
    }
}
