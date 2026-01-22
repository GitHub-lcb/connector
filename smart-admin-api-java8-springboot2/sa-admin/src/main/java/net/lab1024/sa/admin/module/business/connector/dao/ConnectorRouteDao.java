package net.lab1024.sa.admin.module.business.connector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorRouteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 路由配置 DAO
 */
@Mapper
@Component
public interface ConnectorRouteDao extends BaseMapper<ConnectorRouteEntity> {
}
