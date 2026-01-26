package com.zhaogang.connector.base.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.zhaogang.connector.base.common.context.TenantContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * mp 插件
 *
 * @Author 连接器-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusConfig {

    /**
     * 不需要多租户隔离的表
     */
    private static final Set<String> IGNORE_TABLE_NAMES = new HashSet<>(Arrays.asList(
            "t_code_generator_config",
            "information_schema.tables",
            "information_schema.columns",
            "t_connector_job",
            "t_connector_job_log",
            "t_tenant"
    ));

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                Long tenantId = TenantContext.getTenantId();
                if (tenantId == null) {
                    // 如果没有租户ID，默认返回0或者报错，这里为了兼容旧数据暂且返回1（假设1是默认租户）
                    // 实际生产环境应该抛出异常或处理
                    return new LongValue(1L);
                }
                return new LongValue(tenantId);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 手动忽略
                if (TenantContext.getIgnore()) {
                    return true;
                }
                // 表白名单/黑名单
                return IGNORE_TABLE_NAMES.contains(tableName.toLowerCase());
            }
        }));

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
