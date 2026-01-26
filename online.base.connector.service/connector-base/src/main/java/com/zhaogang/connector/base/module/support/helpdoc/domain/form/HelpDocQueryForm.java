package com.zhaogang.connector.base.module.support.helpdoc.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 帮助文档 分页查询
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class HelpDocQueryForm extends PageParam {

    @Schema(description = "分类")
    private Long helpDocCatalogId;

    @Schema(description = "标题")
    private String keywords;

    @Schema(description = "创建-开始时间")
    private LocalDate createTimeBegin;

    @Schema(description = "创建-截止时间")
    private LocalDate createTimeEnd;

}
