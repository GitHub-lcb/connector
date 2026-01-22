package net.lab1024.sa.admin.module.business.connector.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 路由日志实体
 */
@Data
@TableName("route_logs")
public class ConnectorRouteLogEntity {

    @TableId(type = IdType.ASSIGN_UUID)
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

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
