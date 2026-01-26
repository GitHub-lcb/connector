package com.zhaogang.connector.base.module.support.helpdoc.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 帮助文档 关联项目
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class HelpDocRelationForm {

    @Schema(description = "关联名称")
    @NotBlank(message = "关联名称不能为空")
    private String relationName;

    @Schema(description = "关联id")
    @NotNull(message = "关联id不能为空")
    private Long relationId;
}
