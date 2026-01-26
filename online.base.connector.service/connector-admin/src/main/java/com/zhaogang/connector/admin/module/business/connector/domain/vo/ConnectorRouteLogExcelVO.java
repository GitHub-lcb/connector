package com.zhaogang.connector.admin.module.business.connector.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 路由日志 Excel VO
 *
 * @author chenbo.li
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorRouteLogExcelVO {

    @ExcelProperty("请求时间")
    private LocalDateTime createTime;

    @ExcelProperty("路由名称")
    private String routeName;

    @ExcelProperty("渠道")
    private String channel;

    @ExcelProperty("请求路径")
    private String requestPath;

    @ExcelProperty("状态码")
    private Integer statusCode;

    @ExcelProperty("耗时(ms)")
    private Long latencyMs;

    @ExcelProperty("错误信息")
    private String errorMsg;
}
