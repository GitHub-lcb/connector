package com.zhaogang.connector.admin.module.business.connector.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 路由日志实体
 */
@Data
@TableName("t_connector_route_log")
public class ConnectorRouteLogEntity {

    @TableId(value = "log_id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("route_id")
    private String routeId;

    @TableField("request_path")
    private String requestPath;

    @TableField("status_code")
    private Integer statusCode;

    @TableField("latency_ms")
    private Long latencyMs;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("channel")
    private String channel;

    @TableField("original_params")
    private String originalParams;

    @TableField("transformed_params")
    private String transformedParams;

    @TableField("response_data")
    private String responseData;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
