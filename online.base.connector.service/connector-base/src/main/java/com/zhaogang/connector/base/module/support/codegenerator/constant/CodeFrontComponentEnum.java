package com.zhaogang.connector.base.module.support.codegenerator.constant;

import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 前端组件类型
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-06-30 20:15:38
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public enum CodeFrontComponentEnum implements BaseEnum {

    INPUT("Input", "输入框"),

    INPUT_NUMBER("InputNumber", "数字输入框"),

    TEXTAREA("Textarea", " 文本"),

    BOOLEAN_SELECT("BooleanSelect", "布尔下拉框"),

    ENUM_SELECT("SmartEnumSelect", "枚举下拉"),

    DICT_SELECT("DictSelect", "字典下拉"),

    DATE("Date", "日期选择"),

    DATE_TIME("DateTime", "时间选择"),

    FILE_UPLOAD("FileUpload", "文件上传");

    private String value;

    private String desc;

    CodeFrontComponentEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
