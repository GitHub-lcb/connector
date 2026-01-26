package com.zhaogang.connector.admin.module.business.connector.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 路由配置实体
 */
@Data
@TableName(value = "t_connector_route", autoResultMap = true)
public class ConnectorRouteEntity {

    @TableId(value = "route_id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String channel;

    @TableField("source_path")
    private String sourcePath;

    @TableField("target_url")
    private String targetUrl;

    private String method;

    @TableField("forward_flag")
    private Boolean forwardFlag;

    @TableField(value = "mapping_config", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> mappingConfig;

    @TableField("script_type")
    private String scriptType;

    @TableField("script_content")
    private String scriptContent;

    private String status;

    private Integer version;

    private Boolean deletedFlag;

    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
