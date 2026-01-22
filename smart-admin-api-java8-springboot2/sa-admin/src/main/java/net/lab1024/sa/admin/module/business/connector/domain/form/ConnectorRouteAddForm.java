package net.lab1024.sa.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class ConnectorRouteAddForm {

    @Schema(description = "路由名称", required = true)
    @NotBlank(message = "路由名称不能为空")
    private String name;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "源路径", required = true)
    @NotBlank(message = "源路径不能为空")
    private String sourcePath;

    @Schema(description = "目标URL", required = true)
    @NotBlank(message = "目标URL不能为空")
    private String targetUrl;

    @Schema(description = "HTTP方法", required = true)
    @NotBlank(message = "HTTP方法不能为空")
    private String method;

    @Schema(description = "映射配置")
    private Map<String, Object> mappingConfig;

    @Schema(description = "状态")
    private String status = "active";
}
