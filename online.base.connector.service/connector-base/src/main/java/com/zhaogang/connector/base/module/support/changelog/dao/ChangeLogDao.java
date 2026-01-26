package com.zhaogang.connector.base.module.support.changelog.dao;

import java.util.List;

import com.zhaogang.connector.base.module.support.changelog.domain.form.ChangeLogQueryForm;
import com.zhaogang.connector.base.module.support.changelog.domain.vo.ChangeLogVO;
import com.zhaogang.connector.base.module.support.changelog.domain.entity.ChangeLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统更新日志 Dao
 *
 * @Author 卓大
 * @Date 2022-09-26 14:53:50
 * @Copyright 连接器
 */

@Mapper
public interface ChangeLogDao extends BaseMapper<ChangeLogEntity> {

    /**
     * 分页 查询
     *
     */
    List<ChangeLogVO> queryPage(Page page, @Param("queryForm") ChangeLogQueryForm queryForm);

    /**
     * 根据版本查询 ChangeLog
     *
     */
    ChangeLogEntity selectByVersion(@Param("version") String version);


}
