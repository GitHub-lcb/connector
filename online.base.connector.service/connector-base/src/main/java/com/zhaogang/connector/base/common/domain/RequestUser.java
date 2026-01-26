package com.zhaogang.connector.base.common.domain;

import com.zhaogang.connector.base.common.enumeration.UserTypeEnum;

/**
 * 请求用户
 *
 * @Author 连接器-主任: 卓大
 * @Date 2021-12-21 19:55:07
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public interface RequestUser {

    /**
     * 请求用户id
     *
     * @return
     */
    Long getUserId();

    /**
     * 请求用户名称
     *
     * @return
     */
    String getUserName();

    /**
     * 获取用户类型
     */
    UserTypeEnum getUserType();

    /**
     * 获取请求的IP
     *
     * @return
     */
    String getIp();

    /**
     * 获取请求 user-agent
     *
     * @return
     */
    String getUserAgent();

}
