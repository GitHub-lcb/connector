package com.zhaogang.connector.base.module.support.serialnumber.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.zhaogang.connector.base.common.constant.StringConst;
import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 单据序列号 周期
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@AllArgsConstructor
@Getter
public enum SerialNumberRuleTypeEnum implements BaseEnum {
    /**
     * 没有周期
     */
    NONE(StringConst.EMPTY, "", "没有周期"),
    /**
     * 年周期
     */
    YEAR("[yyyy]", "\\[yyyy\\]", "年"),
    /**
     * 月周期
     */
    MONTH("[mm]", "\\[mm\\]", "年月"),
    /**
     * 日周期
     */
    DAY("[dd]", "\\[dd\\]", "年月日");

    private final String value;

    private final String regex;

    private final String desc;


}
