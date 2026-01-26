package com.zhaogang.connector.admin.module.system.tenant.domain.form;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 租户导入表单
 *
 * @author chenbo.li
 */
@Data
public class TenantImportForm {

    @ExcelProperty("租户名称")
    private String tenantName;

    @ExcelProperty("联系人")
    private String contactPerson;

    @ExcelProperty("联系电话")
    private String contactPhone;
}
