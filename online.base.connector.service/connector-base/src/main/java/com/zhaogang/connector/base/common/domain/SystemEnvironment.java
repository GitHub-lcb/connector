package com.zhaogang.connector.base.common.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import com.zhaogang.connector.base.common.enumeration.SystemEnvironmentEnum;

/**
 * 系统环境
 *
 * @Author 连接器-主任: 卓大
 * @Date 2021/8/13 21:06:11
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@AllArgsConstructor
@Getter
public class SystemEnvironment {

    /**
     * 是否位生产环境
     */
    private boolean isProd;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 当前环境
     */
    private SystemEnvironmentEnum currentEnvironment;
}
