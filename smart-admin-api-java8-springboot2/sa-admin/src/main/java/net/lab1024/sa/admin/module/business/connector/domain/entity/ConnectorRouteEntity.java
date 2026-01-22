package net.lab1024.sa.admin.module.business.connector.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 路由配置实体
 */
@Data
@TableName(value = "routes", autoResultMap = true)
public class ConnectorRouteEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String channel;

    @TableField("source_path")
    private String sourcePath;

    @TableField("target_url")
    private String targetUrl;

    private String method;

    @TableField(value = "mapping_config", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> mappingConfig;

    private String status;

    private Integer version;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
