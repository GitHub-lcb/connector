package com.zhaogang.connector.admin.module.business.oa.notice.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import com.zhaogang.connector.base.common.enumeration.BaseEnum;

/**
 * 公告、通知 可见范围类型
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Getter
@AllArgsConstructor
public enum NoticeVisibleRangeDataTypeEnum implements BaseEnum {

    /**
     * 员工
     */
    EMPLOYEE(1, "员工"),

    /**
     * 部门
     */
    DEPARTMENT(2, "部门"),

    ;

    private final Integer value;

    private final String desc;
}
