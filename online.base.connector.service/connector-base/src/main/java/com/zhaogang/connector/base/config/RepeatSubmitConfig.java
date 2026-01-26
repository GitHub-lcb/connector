package com.zhaogang.connector.base.config;

import com.zhaogang.connector.base.common.constant.StringConst;
import com.zhaogang.connector.base.common.util.SmartRequestUtil;
import com.zhaogang.connector.base.module.support.repeatsubmit.RepeatSubmitAspect;
import com.zhaogang.connector.base.module.support.repeatsubmit.ticket.RepeatSubmitRedisTicket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 重复提交配置
 *
 * @Author 连接器: 罗伊
 * @Date 2021/10/9 18:47
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
@Configuration
public class RepeatSubmitConfig {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public RepeatSubmitAspect repeatSubmitAspect() {
        RepeatSubmitRedisTicket ticket = new RepeatSubmitRedisTicket(redisTemplate, this::ticket);
        return new RepeatSubmitAspect(ticket);
    }

    /**
     * 获取指明某个用户的凭证
     */
    private String ticket(HttpServletRequest request) {
        Long userId = SmartRequestUtil.getRequestUserId();
        if (null == userId) {
            return StringConst.EMPTY;
        }
        return request.getServletPath() + "_" + userId;
    }
}
