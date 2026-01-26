package com.zhaogang.connector.base.common.context;

/**
 * 多租户上下文
 *
 * @Author 连接器-主任: 卓大
 * @Date 2023-01-23 20:21:10
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> IGNORE = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setIgnore(Boolean ignore) {
        IGNORE.set(ignore);
    }

    public static Boolean getIgnore() {
        return IGNORE.get() != null && IGNORE.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        IGNORE.remove();
    }
}
