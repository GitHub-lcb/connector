package com.zhaogang.connector.admin.util;

import com.zhaogang.connector.admin.module.system.login.domain.RequestEmployee;
import com.zhaogang.connector.base.common.domain.RequestUser;
import com.zhaogang.connector.base.common.util.SmartRequestUtil;

/**
 * admin 端的请求工具类
 *
 * @Author 连接器-主任:卓大
 * @Date 2023/7/28 19:39:21
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>，Since 2012
 */
public final class AdminRequestUtil {


    public static RequestEmployee getRequestUser() {
        return (RequestEmployee) SmartRequestUtil.getRequestUser();
    }

    public static Long getRequestUserId() {
        RequestUser requestUser = getRequestUser();
        return null == requestUser ? null : requestUser.getUserId();
    }


}
