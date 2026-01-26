package com.zhaogang.connector.base.module.support.dict.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 数据字典 列表VO
 *
 * @Author 连接器-主任-卓大
 * @Date 2025-03-25 22:25:04
 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */

@Data
public class DictVO {

    @Schema(description = "字典id")
    private Long dictId;

    @Schema(description = "字典名字")
    private String dictName;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典备注")
    private String remark;

    @Schema(description = "禁用状态")
    private Boolean disabledFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
