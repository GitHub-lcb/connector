package com.zhaogang.connector.admin.module.business.goods.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 商品状态
 *
 * @Author 连接器: 胡克
 * @Date 2021-10-25 20:26:54
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@AllArgsConstructor
@Getter
public enum GoodsStatusEnum implements BaseEnum {

    /**
     * 1 预约中
     */
    APPOINTMENT(1, "预约中"),

    /**
     * 2 售卖
     */
    SELL(2, "售卖中"),

    /**
     * 3 售罄
     */
    SELL_OUT(3, "售罄"),


    ;

    private final Integer value;

    private final String desc;
}
