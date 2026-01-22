package net.lab1024.sa.admin.module.business.connector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorRouteLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteLogQueryForm;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorRouteLogVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 路由日志 DAO
 */
@Mapper
@Component
public interface ConnectorRouteLogDao extends BaseMapper<ConnectorRouteLogEntity> {

    /**
     * 分页查询日志（关联路由名称）
     *
     * @param page 分页对象
     * @param queryForm 查询表单
     * @return 日志列表
     */
    List<ConnectorRouteLogVO> queryLogPage(Page<?> page, @Param("queryForm") ConnectorRouteLogQueryForm queryForm);
}
