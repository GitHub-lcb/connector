package com.zhaogang.connector.base.config;

import cn.dev33.satoken.config.SaTokenConfig;
import com.zhaogang.connector.base.module.support.securityprotect.service.Level3ProtectConfigService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 *
 * 三级等保配置初始化后最低活跃频率全局配置
 *
 * @Author 连接器-创始人兼主任:卓大
 * @Date 2024/11/24
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a> ，Since 2012
 */

@Configuration
public class TokenConfig {

    @Resource
    private Level3ProtectConfigService level3ProtectConfigService;

    // 此配置会覆盖 sa-base.yaml 中的配置
    @Resource
    public void configSaToken(SaTokenConfig config) {

        config.setActiveTimeout(level3ProtectConfigService.getLoginActiveTimeoutSeconds());
    }


}
