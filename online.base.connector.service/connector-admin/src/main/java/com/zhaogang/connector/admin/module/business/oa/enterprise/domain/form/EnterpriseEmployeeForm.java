package com.zhaogang.connector.admin.module.business.oa.enterprise.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 企业员工
 *
 * @Author 连接器: 罗伊
 * @Date 2022/7/28 20:37:15
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class EnterpriseEmployeeForm {

    @Schema(description = "企业id")
    @NotNull(message = "企业id不能为空")
    private Long enterpriseId;

    @Schema(description = "员工信息id")
    @NotEmpty(message = "员工信息id不能为空")
    private List<Long> employeeIdList;
}