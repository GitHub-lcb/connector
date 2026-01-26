package com.zhaogang.connector.admin.module.business.oa.enterprise.constant;


import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 企业类型
 *
 * @Author 连接器: 开云
 * @Date 2022/7/28 20:37:15
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public enum EnterpriseTypeEnum implements BaseEnum {

    /**
     * 有限企业
     */
    NORMAL(1, "有限企业"),

    /**
     * 外资企业
     */
    FOREIGN(2, "外资企业"),
    ;

    private Integer value;
    private String desc;

    EnterpriseTypeEnum(Integer value, String desc) {
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
