package com.zhaogang.connector.admin.module.business.connector.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 路由配置 Excel VO
 *
 * @author chenbo.li
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorRouteExcelVO {

    @ExcelProperty("路由ID")
    private String id;

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

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
