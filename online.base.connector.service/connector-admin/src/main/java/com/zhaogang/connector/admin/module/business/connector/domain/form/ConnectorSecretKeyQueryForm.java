package com.zhaogang.connector.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

@Data
@Schema(description = "连接器密钥查询表单")
public class ConnectorSecretKeyQueryForm extends PageParam {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "Access Key")
    private String accessKey;

    @Schema(description = "状态")
    private Integer status;
}
