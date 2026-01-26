package com.zhaogang.connector.admin.module.business.connector.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "连接器密钥VO")
public class ConnectorSecretKeyVO {
    @Schema(description = "主键")
    private Long keyId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "Access Key")
    private String accessKey;
    
    @Schema(description = "Secret Key")
    private String secretKey;

    @Schema(description = "状态 1:启用 0:禁用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
