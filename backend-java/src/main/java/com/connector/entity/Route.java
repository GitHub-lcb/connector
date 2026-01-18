package com.connector.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "routes", autoResultMap = true)
public class Route {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String sourcePath;

    private String targetUrl;

    private String method;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> mappingConfig;

    private String status;

    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
