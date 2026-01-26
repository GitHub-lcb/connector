package com.zhaogang.connector.admin.module.system.datascope.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 数据可见范围
 *
 * @Author 连接器: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
@Builder
public class DataScopeViewTypeVO {

    @Schema(description = "可见范围")
    private Integer viewType;

    @Schema(description = "可见范围名称")
    private String viewTypeName;

    @Schema(description = "级别,用于表示范围大小")
    private Integer viewTypeLevel;
}
