package net.lab1024.sa.admin.module.business.connector.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ConnectorConfigHistoryVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "路由ID")
    private String routeId;

    @Schema(description = "旧配置")
    private Map<String, Object> oldConfig;

    @Schema(description = "新配置")
    private Map<String, Object> newConfig;

    @Schema(description = "修改人")
    private String changedBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
