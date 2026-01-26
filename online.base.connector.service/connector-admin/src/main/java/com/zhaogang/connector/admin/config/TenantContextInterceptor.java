package com.zhaogang.connector.admin.config;

import cn.dev33.satoken.stp.StpUtil;
import com.zhaogang.connector.base.common.context.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 多租户拦截器
 *
 * @Author 连接器-主任: 卓大
 * @Date 2023-01-23 20:21:10
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Configuration
public class TenantContextInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 优先从 Sa-Token 获取（已登录用户）
        // 这里假设 LoginService 登录时已经将 tenantId 放入 Session 中
        // Object tenantIdObj = StpUtil.getSession().get("tenantId");
        
        // 简化版：为了演示，如果 Header 有 x-tenant-id 则使用，否则默认为 1
        String tenantIdStr = request.getHeader("x-tenant-id");
        Long tenantId = null;
        if (StringUtils.isNotBlank(tenantIdStr) && NumberUtils.isDigits(tenantIdStr)) {
            tenantId = Long.valueOf(tenantIdStr);
        } else {
            // 尝试从 Token 获取（需要登录逻辑配合存储）
             if (StpUtil.isLogin()) {
                 // 暂时 Mock，实际应从 StpUtil.getSession().get("tenantId") 获取
                 // tenantId = 1L;
             }
        }
        
        // 如果没有获取到，默认为 1 (主租户)
        if (tenantId == null) {
            tenantId = 1L;
        }

        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.clear();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v3/**", "/swagger-ui.html/**");
    }
}
