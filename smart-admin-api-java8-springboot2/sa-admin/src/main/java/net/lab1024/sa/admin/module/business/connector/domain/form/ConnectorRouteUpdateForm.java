package net.lab1024.sa.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class ConnectorRouteUpdateForm {

    @Schema(description = "ID", required = true)
    @NotBlank(message = "ID不能为空")
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

    @Schema(description = "映射配置")
    private Map<String, Object> mappingConfig;

    @Schema(description = "状态")
    private String status;
}
