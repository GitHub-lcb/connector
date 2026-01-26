package com.zhaogang.connector.admin.module.system.tenant.domain.form;

import com.zhaogang.connector.base.common.domain.PageParam;
import lombok.Data;

@Data
public class TenantQueryForm extends PageParam {
    private String tenantName;
    private String contactPerson;
    private String contactPhone;
}
