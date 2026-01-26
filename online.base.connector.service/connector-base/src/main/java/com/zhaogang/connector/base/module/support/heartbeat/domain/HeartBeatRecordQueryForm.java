package com.zhaogang.connector.base.module.support.heartbeat.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 心跳记录 查询
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class HeartBeatRecordQueryForm extends PageParam {

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

}
