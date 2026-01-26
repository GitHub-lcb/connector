package com.zhaogang.connector.admin.module.business.category.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.admin.module.business.category.constant.CategoryTypeEnum;
import com.zhaogang.connector.base.common.swagger.SchemaEnum;

/**
 * 类目 层级树查询
 *
 * @Author 连接器: 胡克
 * @Date 2021/08/05 21:26:58
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class CategoryTreeQueryForm {

    @SchemaEnum(desc = "分类类型|可选", value = CategoryTypeEnum.class)
    private Integer categoryType;

    @Schema(description = "父级类目id|可选")
    private Long parentId;
}
