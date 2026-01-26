package com.zhaogang.connector.base.module.support.operatelog.domain;

import com.zhaogang.connector.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *  操作日志查询 表单
 *
 * @Author 连接器: 罗伊
 * @Date 2021-12-08 20:48:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class OperateLogQueryForm extends PageParam {

    @Schema(description = "用户ID")
    private Long operateUserId;

    @Schema(description = "用户类型")
    private Integer operateUserType;

    @Schema(description = "关键字：模块、操作内容")
    private String keywords;

    @Schema(description = "请求关键字：请求地址、请求方法、请求参数")
    private String requestKeywords;

    @Schema(description = "开始日期")
    private String startDate;

    @Schema(description = "结束日期")
    private String endDate;


    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "请求结果 false失败 true成功")
    private Boolean successFlag;

}
