package com.zhaogang.connector.admin.module.business.oa.bank.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * OA办公-银行信息更新
 *
 * @Author 连接器:善逸
 * @Date 2022/6/23 21:59:22
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class BankUpdateForm extends BankCreateForm {

    @Schema(description = "银行信息ID")
    @NotNull(message = "银行信息ID不能为空")
    private Long bankId;
}
