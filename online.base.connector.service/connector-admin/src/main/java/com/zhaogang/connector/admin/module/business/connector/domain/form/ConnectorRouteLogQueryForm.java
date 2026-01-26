package com.zhaogang.connector.admin.module.business.connector.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

@Data
public class ConnectorRouteLogQueryForm extends PageParam {

    @Schema(description = "路由ID")
    private String routeId;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "是否成功")
    private Boolean successFlag;
}
