package com.zhaogang.connector.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

@Data
public class ConnectorRouteQueryForm extends PageParam {
    @Schema(description = "路由名称")
    private String name;
    
    @Schema(description = "状态")
    private String status;
}
