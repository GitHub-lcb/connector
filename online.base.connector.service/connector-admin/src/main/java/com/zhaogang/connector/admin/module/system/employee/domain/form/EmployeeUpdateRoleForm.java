package com.zhaogang.connector.admin.module.system.employee.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 员工更新角色
 *
 * @Author 连接器: 罗伊
 * @Date 2021-12-20 20:55:13
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class EmployeeUpdateRoleForm {

    @Schema(description = "员工id")
    @NotNull(message = "员工id不能为空")
    private Long employeeId;

    @Schema(description = "角色ids")
    @Size(max = 99, message = "角色最多99")
    private List<Long> roleIdList;

}
