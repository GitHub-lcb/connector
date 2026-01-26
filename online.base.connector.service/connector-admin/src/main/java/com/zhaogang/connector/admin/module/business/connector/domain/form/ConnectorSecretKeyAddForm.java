package com.zhaogang.connector.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "连接器密钥添加表单")
public class ConnectorSecretKeyAddForm {

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
