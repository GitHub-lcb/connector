package com.zhaogang.connector.admin.module.system.datascope.constant;


import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 数据范围 sql where
 *
 * @Author 连接器: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public enum DataScopeWhereInTypeEnum implements BaseEnum {

    /**
     * 以员工IN
     */
    EMPLOYEE(0, "以员工IN"),

    /**
     * 以部门IN
     */
    DEPARTMENT(1, "以部门IN"),

    /**
     * 自定义策略
     */
    CUSTOM_STRATEGY(2, "自定义策略");

    private final Integer value;
    private final String desc;

    DataScopeWhereInTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }


}
