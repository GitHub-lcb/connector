package com.zhaogang.connector.admin.module.business.goods.domain.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.admin.module.business.goods.constant.GoodsStatusEnum;
import com.zhaogang.connector.base.common.domain.PageParam;
import com.zhaogang.connector.base.common.json.deserializer.DictDataDeserializer;
import com.zhaogang.connector.base.common.swagger.SchemaEnum;
import com.zhaogang.connector.base.common.validator.enumeration.CheckEnum;
import org.hibernate.validator.constraints.Length;

/**
 * 商品 分页查询
 *
 * @Author 连接器: 胡克
 * @Date 2021-10-25 20:26:54
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class GoodsQueryForm extends PageParam {

    @Schema(description = "商品分类")
    private Integer categoryId;

    @Schema(description = "搜索词")
    @Length(max = 30, message = "搜索词最多30字符")
    private String searchWord;

    @SchemaEnum(GoodsStatusEnum.class)
    @CheckEnum(message = "商品状态错误", value = GoodsStatusEnum.class, required = false)
    private Integer goodsStatus;

    @Schema(description = "产地")
    @JsonDeserialize(using = DictDataDeserializer.class)
    private String place;

    @Schema(description = "上架状态")
    private Boolean shelvesFlag;

    @Schema(hidden = true)
    private Boolean deletedFlag;
}
