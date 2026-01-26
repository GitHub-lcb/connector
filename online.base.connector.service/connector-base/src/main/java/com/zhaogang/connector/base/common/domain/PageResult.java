package com.zhaogang.connector.base.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页返回对象
 *
 * @Author 连接器-主任: 卓大
 * @Date 2020/04/28 16:19
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class PageResult<T> {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Long pageNum;

    /**
     * 每页的数量
     */
    @Schema(description = "每页的数量")
    private Long pageSize;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Long pages;

    /**
     * 结果集
     */
    @Schema(description = "结果集")
    private List<T> list;

    @Schema(description = "是否为空")
    private Boolean emptyFlag;

}
