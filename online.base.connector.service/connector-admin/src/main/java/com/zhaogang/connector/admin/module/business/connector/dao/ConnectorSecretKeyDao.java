package com.zhaogang.connector.admin.module.business.connector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhaogang.connector.admin.module.business.connector.domain.entity.ConnectorSecretKeyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 连接器密钥DAO
 */
@Mapper
@Component
public interface ConnectorSecretKeyDao extends BaseMapper<ConnectorSecretKeyEntity> {
}
