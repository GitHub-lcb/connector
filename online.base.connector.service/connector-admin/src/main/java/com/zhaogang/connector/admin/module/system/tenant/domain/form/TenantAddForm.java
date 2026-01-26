package com.zhaogang.connector.admin.module.system.tenant.domain.form;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class TenantAddForm {
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;
    private String contactPerson;
    private String contactPhone;
    private Integer status;
    private LocalDateTime expireTime;
}
