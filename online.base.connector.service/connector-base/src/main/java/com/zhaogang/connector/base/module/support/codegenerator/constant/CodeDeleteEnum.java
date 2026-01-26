package com.zhaogang.connector.base.module.support.codegenerator.constant;

import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 删除类型
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-06-30 22:15:38
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public enum CodeDeleteEnum implements BaseEnum {

    SINGLE("Single", "单个删除"),
    BATCH("Batch", "批量删除"),
    SINGLE_AND_BATCH("SingleAndBatch", "单个和批量删除");

    private String value;

    private String desc;

    CodeDeleteEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
