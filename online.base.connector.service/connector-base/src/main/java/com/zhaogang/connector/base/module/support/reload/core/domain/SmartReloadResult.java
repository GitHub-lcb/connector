package com.zhaogang.connector.base.module.support.reload.core.domain;

import lombok.Data;

/**
 * t_reload_result 表 实体类
 *
 * @Author 连接器-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class SmartReloadResult {

    /**
     * 项名称
     */
    private String tag;

    /**
     * 参数
     */
    private String args;

    /**
     * 标识
     */
    private String identification;

    /**
     * 处理结果
     */
    private boolean result;

    /**
     * 异常说明
     */
    private String exception;


}
