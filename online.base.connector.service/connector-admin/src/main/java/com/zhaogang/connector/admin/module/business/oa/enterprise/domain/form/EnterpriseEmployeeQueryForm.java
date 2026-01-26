package com.zhaogang.connector.admin.module.business.oa.enterprise.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 查询企业员工
 *
 * @Author 连接器: 开云
 * @Date 2021-12-20 21:06:49
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class EnterpriseEmployeeQueryForm extends PageParam {

    @Schema(description = "搜索词")
    @Length(max = 20, message = "搜索词最多20字符")
    private String keyword;

    @Schema(description = "公司Id")
    @NotNull(message = "公司id 不能为空")
    private Long enterpriseId;

    @Schema(description = "删除标识", hidden = true)
    private Boolean deletedFlag;

}
