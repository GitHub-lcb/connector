package com.zhaogang.connector.admin.module.system.tenant.domain.form;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class TenantUpdateForm extends TenantAddForm {
    @NotNull(message = "租户ID不能为空")
    private Long tenantId;
}
