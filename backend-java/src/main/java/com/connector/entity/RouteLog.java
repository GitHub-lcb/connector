package com.connector.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("route_logs")
public class RouteLog {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String routeId;

    private String requestPath;

    private Integer statusCode;

    private Long latencyMs;

    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // MyBatis-Plus doesn't support @ManyToOne directly. 
    // We usually fetch related data manually or use XML mapper if needed.
    // For simple CRUD, we can skip the relation object in entity 
    // or keep it as transient/DTO field if we populate it manually.
    // Let's keep it simple for now and rely on routeId.
}
