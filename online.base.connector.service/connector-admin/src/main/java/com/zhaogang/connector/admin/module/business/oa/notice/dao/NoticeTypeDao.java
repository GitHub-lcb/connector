package com.zhaogang.connector.admin.module.business.oa.notice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhaogang.connector.admin.module.business.oa.notice.domain.entity.NoticeTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 通知公告类型
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Mapper
public interface NoticeTypeDao extends BaseMapper<NoticeTypeEntity> {

}
