package com.zhaogang.connector.admin.module.business.connector.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 连接器密钥实体
 */
@Data
@TableName("t_connector_secret_key")
public class ConnectorSecretKeyEntity {

    @TableId(value = "key_id", type = IdType.AUTO)
    private Long keyId;

    private Long tenantId;

    private String title;

    private String accessKey;

    private String secretKey;

    private Integer status;

    private Boolean deletedFlag;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
