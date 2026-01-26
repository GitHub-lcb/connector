package com.zhaogang.connector.base.module.support.dict.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 字典数据表 更新表单
 *
 * @Author 连接器-主任-卓大
 * @Date 2025-03-25 23:12:59
 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */

@Data
public class DictDataUpdateForm extends DictDataAddForm {

    @Schema(description = "字典数据id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "字典数据id 不能为空")
    private Long dictDataId;

    @Schema(description = "字典数据编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "字典数据编码 不能为空")
    private String dictCode;


}