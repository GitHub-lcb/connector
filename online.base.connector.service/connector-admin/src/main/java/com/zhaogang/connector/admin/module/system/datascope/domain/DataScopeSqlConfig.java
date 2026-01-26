package com.zhaogang.connector.admin.module.system.datascope.domain;

import lombok.Data;
import com.zhaogang.connector.admin.module.system.datascope.constant.DataScopeTypeEnum;
import com.zhaogang.connector.admin.module.system.datascope.constant.DataScopeWhereInTypeEnum;

/**
 * 数据范围
 *
 * @Author 连接器: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class DataScopeSqlConfig {

    /**
     * 数据范围类型
     * {@link DataScopeTypeEnum}
     */
    private DataScopeTypeEnum dataScopeType;

    /**
     * join sql 具体实现类
     */
    private Class<?> joinSqlImplClazz;

    private String joinSql;

    private Integer whereIndex;

    private String paramName;

    /**
     * whereIn类型
     * {@link DataScopeWhereInTypeEnum}
     */
    private DataScopeWhereInTypeEnum dataScopeWhereInType;
}
