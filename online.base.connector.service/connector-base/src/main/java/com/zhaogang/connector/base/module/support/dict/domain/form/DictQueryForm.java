package com.zhaogang.connector.base.module.support.dict.domain.form;

import com.zhaogang.connector.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典 分页查询表单
 *
 * @Author 连接器-主任-卓大
 * @Date 2025-03-25 22:25:04
 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class DictQueryForm extends PageParam {

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "禁用状态")
    private Boolean disabledFlag;

}
