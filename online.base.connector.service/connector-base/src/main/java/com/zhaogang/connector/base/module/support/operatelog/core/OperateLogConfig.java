package com.zhaogang.connector.base.module.support.operatelog.core;

import lombok.Builder;
import lombok.Data;
import com.zhaogang.connector.base.module.support.operatelog.domain.OperateLogEntity;

import java.util.function.Function;

/**
 * 配置
 *
 * @Author 连接器: 罗伊
 * @Date 2021-12-08 20:48:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
@Builder
public class OperateLogConfig {

    /**
     * 操作日志存储方法
     */
    private Function<OperateLogEntity, Boolean> saveFunction;

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 队列大小
     */
    private Integer queueCapacity;


}
