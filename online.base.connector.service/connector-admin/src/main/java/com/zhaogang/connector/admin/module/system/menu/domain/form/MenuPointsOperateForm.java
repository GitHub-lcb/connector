package com.zhaogang.connector.admin.module.system.menu.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 菜单功能点操作Form
 *
 * @Author 连接器: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class MenuPointsOperateForm {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "功能点名称")
    @NotBlank(message = "功能点不能为空")
    @Length(max = 30, message = "功能点最多30个字符")
    private String menuName;

    @Schema(description = "禁用状态")
    @NotNull(message = "禁用状态不能为空")
    private Boolean disabledFlag;

    @Schema(description = "后端接口权限集合")
    private List<String> apiPermsList;

    @Schema(description = "权限字符串")
    private String webPerms;

    @Schema(description = "功能点关联菜单ID")
    private Long contextMenuId;
}
