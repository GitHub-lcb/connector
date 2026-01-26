package com.zhaogang.connector.admin.module.business.oa.notice.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 通知公告 管理查询表单
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class NoticeQueryForm extends PageParam {

    @Schema(description = "分类")
    private Long noticeTypeId;

    @Schema(description = "标题、作者、来源")
    private String keywords;

    @Schema(description = "文号")
    private String documentNumber;

    @Schema(description = "创建人")
    private String createUserName;

    @Schema(description = "删除标识")
    private Boolean deletedFlag;

    @Schema(description = "创建-开始时间")
    private LocalDate createTimeBegin;

    @Schema(description = "创建-截止时间")
    private LocalDate createTimeEnd;

    @Schema(description = "发布-开始时间")
    private LocalDate publishTimeBegin;

    @Schema(description = "发布-截止时间")
    private LocalDate publishTimeEnd;

}
