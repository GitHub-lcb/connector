package com.zhaogang.connector.base.common.util;


import java.time.format.DateTimeFormatter;

/**
 * @Author 连接器: 胡克
 * @Date 2023/12/5 21:26:25
 * @Wechat zhuoda1024

 * 连接器 （ https://www.zhaogang.com ），2012-2023
 */
public enum SmartDateFormatterEnum {
    /**
     * 日期格式 ：年月日 yyyy-MM-dd
     * 例：2021-10-15
     */
    YMD(DateTimeFormatter.ofPattern("yyyy-MM-dd")),

    /**
     * 日期格式 ：年月日 时分 yyyy-MM-dd HH:mm
     * 例：2021-10-15 10:15
     */
    YMD_HM(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),

    /**
     * 日期格式 ：年月日 时分秒 yyyy-MM-dd HH:mm:ss
     * 例：2021-10-15 10:15:00
     */
    YMD_HMS(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),

    /**
     * 日期格式 ：年月 yyyy-MM
     * 例：2021-10
     */
    YM(DateTimeFormatter.ofPattern("yyyy-MM")),

    /**
     * 日期格式：年月 MM-dd
     * 例：10-15
     */
    MD(DateTimeFormatter.ofPattern("MM-dd")),

    /**
     * 日期格式：年月 MM月dd日
     * 例：10月15日
     */
    CHINESE_MD(DateTimeFormatter.ofPattern("MM月dd日")),

    /**
     * 日期格式 ： yyyyMMddHHmmss
     * 例：20091225091010
     */
    YMDHMS(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),

    /**
     * 日期格式 ：时分秒 HH:mm:ss
     * 例：10:15:00
     */
    HMS(DateTimeFormatter.ofPattern("HH:mm:ss")),

    /**
     * 日期格式 ：时分 HH:mm
     * 例：10:15
     */
    HM(DateTimeFormatter.ofPattern("HH:mm"))

    ;

    private final DateTimeFormatter formatter;

    SmartDateFormatterEnum(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
