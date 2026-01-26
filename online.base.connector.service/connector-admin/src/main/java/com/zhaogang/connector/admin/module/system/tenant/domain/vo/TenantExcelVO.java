package com.zhaogang.connector.admin.module.system.tenant.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 租户 Excel 导出 VO
 *
 * @author chenbo.li
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantExcelVO {

    @ExcelProperty("租户ID")
    private Long tenantId;

    @ExcelProperty("租户名称")
    private String tenantName;

    @ExcelProperty("联系人")
    private String contactPerson;

    @ExcelProperty("联系电话")
    private String contactPhone;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("过期时间")
    private LocalDateTime expireTime;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
