package com.zhaogang.connector.admin.module.business.connector.domain.form;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 路由导入表单
 *
 * @author chenbo.li
 */
@Data
public class ConnectorRouteImportForm {

    @ExcelProperty("路由名称")
    private String name;

    @ExcelProperty("渠道")
    private String channel;

    @ExcelProperty("源路径")
    private String sourcePath;

    @ExcelProperty("目标URL")
    private String targetUrl;

    @ExcelProperty("HTTP方法")
    private String method;
}
