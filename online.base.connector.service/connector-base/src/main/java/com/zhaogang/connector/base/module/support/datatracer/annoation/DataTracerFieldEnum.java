package com.zhaogang.connector.base.module.support.datatracer.annoation;

import com.zhaogang.connector.base.common.enumeration.BaseEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段枚举
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataTracerFieldEnum {

    Class<? extends BaseEnum> enumClass() default BaseEnum.class;

}
