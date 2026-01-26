package com.zhaogang.connector.base.module.support.heartbeat.core;

/**
 * 心跳处理接口
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public interface IHeartBeatRecordHandler {

    /**
     * 心跳日志处理方法
     *
     * @param heartBeatRecord
     */
    void handler(HeartBeatRecord heartBeatRecord);
}
