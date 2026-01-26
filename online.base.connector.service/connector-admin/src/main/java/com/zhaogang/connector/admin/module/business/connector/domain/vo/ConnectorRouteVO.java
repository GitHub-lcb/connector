package com.zhaogang.connector.admin.module.business.connector.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 路由配置 VO
 */
@Data
public class ConnectorRouteVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "源路径")
    private String sourcePath;

    @Schema(description = "目标URL")
    private String targetUrl;

    @Schema(description = "HTTP方法")
    private String method;

    @Schema(description = "是否转发请求")
    private Boolean forwardFlag;

    @Schema(description = "映射配置")
    private Map<String, Object> mappingConfig;

    @Schema(description = "脚本类型")
    private String scriptType;

    @Schema(description = "脚本内容")
    private String scriptContent;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
