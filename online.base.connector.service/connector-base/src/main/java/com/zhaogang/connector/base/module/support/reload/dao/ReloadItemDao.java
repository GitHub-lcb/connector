package com.zhaogang.connector.base.module.support.reload.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhaogang.connector.base.module.support.reload.domain.ReloadItemEntity;
import com.zhaogang.connector.base.module.support.reload.domain.ReloadItemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * t_reload_item 数据表dao
 *
 * @Author 连接器-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Mapper
public interface ReloadItemDao extends BaseMapper<ReloadItemEntity> {

    List<ReloadItemVO> query();
}
