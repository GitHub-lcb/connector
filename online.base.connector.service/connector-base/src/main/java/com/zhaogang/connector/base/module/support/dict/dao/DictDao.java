package com.zhaogang.connector.base.module.support.dict.dao;

import java.util.List;

import com.zhaogang.connector.base.module.support.dict.domain.entity.DictEntity;
import com.zhaogang.connector.base.module.support.dict.domain.form.DictQueryForm;
import com.zhaogang.connector.base.module.support.dict.domain.vo.DictVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 数据字典 Dao
 *
 * @Author 连接器-主任-卓大
 * @Date 2025-03-25 22:25:04
 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */

@Mapper
@Component
public interface DictDao extends BaseMapper<DictEntity> {

    /**
     * 分页 查询
     */
    List<DictVO> queryPage(Page page, @Param("queryForm") DictQueryForm queryForm);

    /**
     * 根据 dictCode 去查询
     */
    DictEntity selectByCode(@Param("code") String code);

}
