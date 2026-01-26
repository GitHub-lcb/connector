package com.zhaogang.connector.admin.module.system.tenant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.admin.module.system.tenant.domain.entity.TenantEntity;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantQueryForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.vo.TenantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TenantDao extends BaseMapper<TenantEntity> {
    List<TenantVO> queryPage(Page page, @Param("query") TenantQueryForm query);

    void batchUpdateStatus(@Param("tenantIdList") List<Long> tenantIdList, @Param("status") Integer status);
}
