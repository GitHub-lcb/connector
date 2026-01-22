package net.lab1024.sa.admin.module.business.connector.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectorRouteLogVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "路由ID")
    private String routeId;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "请求路径")
    private String requestPath;

    @Schema(description = "状态码")
    private Integer statusCode;

    @Schema(description = "耗时(ms)")
    private Long latencyMs;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "原始入参")
    private String originalParams;

    @Schema(description = "转换后入参")
    private String transformedParams;

    @Schema(description = "响应数据")
    private String responseData;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
