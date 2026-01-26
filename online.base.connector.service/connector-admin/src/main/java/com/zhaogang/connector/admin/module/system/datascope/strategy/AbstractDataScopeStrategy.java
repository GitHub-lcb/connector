package com.zhaogang.connector.admin.module.system.datascope.strategy;

import com.zhaogang.connector.admin.module.system.datascope.constant.DataScopeViewTypeEnum;
import com.zhaogang.connector.admin.module.system.datascope.domain.DataScopeSqlConfig;

import java.util.Map;

/**
 * 数据范围策略 ,使用DataScopeWhereInTypeEnum.CUSTOM_STRATEGY类型，DataScope注解的joinSql属性无用
 *
 * @Author 连接器: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public abstract class AbstractDataScopeStrategy {

    /**
     * 获取joinsql 字符串
     */
    public abstract String getCondition(DataScopeViewTypeEnum viewTypeEnum, Map<String, Object> paramMap, DataScopeSqlConfig sqlConfigDTO);
}
