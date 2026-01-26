package com.zhaogang.connector.base.module.support.reload.core.domain;

import lombok.Data;

/**
 * reload项目
 *
 * @Author 连接器-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class SmartReloadItem {

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

}
