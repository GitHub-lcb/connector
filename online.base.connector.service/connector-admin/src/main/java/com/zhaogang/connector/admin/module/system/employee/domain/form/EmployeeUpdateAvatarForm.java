package com.zhaogang.connector.admin.module.system.employee.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改登录人头像
 *
 * @Author 连接器: 善逸
 * @Date 2024年6月30日00:26:35
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class EmployeeUpdateAvatarForm {

    @Schema(hidden = true)
    private Long employeeId;

    @Schema(description = "头像")
    @NotBlank(message = "头像不能为空哦")
    private String avatar;
}
