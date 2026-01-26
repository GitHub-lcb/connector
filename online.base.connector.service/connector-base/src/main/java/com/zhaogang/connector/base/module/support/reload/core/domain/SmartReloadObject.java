package com.zhaogang.connector.base.module.support.reload.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * Reload 处理程序的实现方法，用于包装以注解 SmartReload 实现的处理类
 *
 * @Author 连接器-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
@AllArgsConstructor
public class SmartReloadObject {

    /**
     * 方法对应的实例化对象
     */
    private Object reloadObject;

    /**
     * 重新加载执行的方法
     */
    private Method method;


}
