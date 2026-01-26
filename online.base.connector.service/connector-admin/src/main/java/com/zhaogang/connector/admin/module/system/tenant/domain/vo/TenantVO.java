package com.zhaogang.connector.admin.module.system.tenant.domain.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TenantVO {
    private Long tenantId;
    private String tenantName;
    private String contactPerson;
    private String contactPhone;
    private Integer status;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
