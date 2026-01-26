package com.zhaogang.connector.base.module.support.helpdoc.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 帮助文档 目录
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class HelpDocCatalogUpdateForm extends HelpDocCatalogAddForm {

    @Schema(description = "id")
    @NotNull(message = "id")
    private Long helpDocCatalogId;
}
