
package com.zhaogang.connector.base.module.support.codegenerator.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 列
 *
 * @Author 连接器-主任:卓大
 * @Date 2022/9/21 21:07:58
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */

@Data
public class TableColumnVO {

    @Schema(description = "列名")
    private String columnName;

    @Schema(description = "列描述")
    private String columnComment;

    @Schema(description = "数据类型varchar")
    private String dataType;

    @Schema(description = "是否为空")
    private Boolean nullableFlag;

    @Schema(description = "是否为主键")
    private Boolean primaryKeyFlag;

    @Schema(description = "是否递增")
    private Boolean autoIncreaseFlag;

    // --------------- 临时字段 -------------------

    @Schema(hidden = true)
    private String columnKey;

    @Schema(hidden = true)
    private String extra;

    @Schema(hidden = true)
    private String isNullable;
}
