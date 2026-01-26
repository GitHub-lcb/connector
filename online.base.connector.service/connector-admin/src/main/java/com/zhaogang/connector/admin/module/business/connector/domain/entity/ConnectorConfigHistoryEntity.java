package com.zhaogang.connector.admin.module.business.connector.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 配置历史实体
 */
@Data
@TableName(value = "t_connector_config_history", autoResultMap = true)
public class ConnectorConfigHistoryEntity {
    @TableId(value = "history_id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("route_id")
    private String routeId;

    @TableField(value = "old_config", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> oldConfig;

    @TableField(value = "new_config", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> newConfig;

    @TableField("changed_by")
    private String changedBy;

    private Long createUserId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
