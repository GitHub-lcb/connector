package com.zhaogang.connector.admin.module.business.connector.domain.form;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 密钥导入表单
 *
 * @author chenbo.li
 */
@Data
public class ConnectorSecretKeyImportForm {

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("备注")
    private String remark;
}
