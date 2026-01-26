package com.zhaogang.connector.base.module.support.heartbeat.core;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 心跳记录日志
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class HeartBeatRecord {

    /**
     * 项目名字
     */
    private String projectPath;
    /**
     * 服务器ip
     */
    private String serverIp;
    /**
     * 进程号
     */
    private Integer processNo;
    /**
     * 进程开启时间
     */
    private LocalDateTime processStartTime;
    /**
     * 心跳当前时间
     */
    private LocalDateTime heartBeatTime;


}
