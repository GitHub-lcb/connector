package com.zhaogang.connector.base.module.support.mail.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 邮件模板类型
 *
 * @Author 连接器-创始人兼主任:卓大
 * @Date 2024/8/5
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a> ，Since 2012
 */

@Getter
@AllArgsConstructor
public enum MailTemplateTypeEnum implements BaseEnum {

    STRING("string", "字符串替代器"),

    FREEMARKER("freemarker", "freemarker模板引擎");

    private String value;

    private String desc;


}