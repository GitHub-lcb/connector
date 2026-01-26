package com.zhaogang.connector.base.module.support.changelog.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;
import com.zhaogang.connector.base.common.swagger.SchemaEnum;
import com.zhaogang.connector.base.common.validator.enumeration.CheckEnum;
import com.zhaogang.connector.base.module.support.changelog.constant.ChangeLogTypeEnum;

import java.time.LocalDate;

/**
 * 系统更新日志 查询
 *
 * @Author 卓大
 * @Date 2022-09-26 14:53:50
 * @Copyright 连接器
 */

@Data
public class ChangeLogQueryForm extends PageParam{

    @SchemaEnum(value = ChangeLogTypeEnum.class, desc = "更新类型:[1:特大版本功能更新;2:功能更新;3:bug修复]")
    @CheckEnum(value = ChangeLogTypeEnum.class, message = "更新类型:[1:特大版本功能更新;2:功能更新;3:bug修复] 错误")
    private Integer type;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "发布日期")
    private LocalDate publicDateBegin;

    @Schema(description = "发布日期")
    private LocalDate publicDateEnd;

    @Schema(description = "创建时间")
    private LocalDate createTime;

    @Schema(description = "跳转链接")
    private String link;

}