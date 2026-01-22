package net.lab1024.sa.admin.module.business.connector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorConfigHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 配置历史 DAO
 */
@Mapper
@Component
public interface ConnectorConfigHistoryDao extends BaseMapper<ConnectorConfigHistoryEntity> {
}
