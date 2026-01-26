package com.zhaogang.connector.admin.module.system.menu.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 菜单 更新Form
 *
 * @Author 连接器: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class MenuUpdateForm extends MenuBaseForm {

    @Schema(description = "菜单ID")
    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(hidden = true)
    private Long updateUserId;
}
