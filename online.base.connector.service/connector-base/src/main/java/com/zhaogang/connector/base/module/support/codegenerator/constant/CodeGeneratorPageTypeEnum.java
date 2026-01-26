package com.zhaogang.connector.base.module.support.codegenerator.constant;

import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 页面类型
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-06-29 19:11:22
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public enum CodeGeneratorPageTypeEnum implements BaseEnum {

    MODAL("modal", "弹窗"),
    DRAWER("drawer", "抽屉"),
    PAGE("page", "新页面");

    private String value;

    private String desc;

    CodeGeneratorPageTypeEnum(String value, String desc) {
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
