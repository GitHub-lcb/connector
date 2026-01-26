package com.zhaogang.connector.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "连接器密钥更新表单")
public class ConnectorSecretKeyUpdateForm {

    @Schema(description = "主键")
    @NotNull(message = "ID不能为空")
    private Long keyId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "是否重置密钥")
    private Boolean resetSecret;
}
