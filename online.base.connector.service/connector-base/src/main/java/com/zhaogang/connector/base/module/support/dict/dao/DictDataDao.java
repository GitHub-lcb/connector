package com.zhaogang.connector.base.module.support.dict.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhaogang.connector.base.module.support.dict.domain.entity.DictDataEntity;
import com.zhaogang.connector.base.module.support.dict.domain.vo.DictDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 字典数据表 Dao
 *
 * @Author 连接器-主任-卓大
 * @Date 2025-03-25 23:12:59
 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */

@Mapper
@Component
public interface DictDataDao extends BaseMapper<DictDataEntity> {

    List<DictDataVO> queryByDictId(@Param("dictId") Long dictId);

    List<DictDataVO> selectByDictDataIds(@Param("dictDataIdList") Collection<Long> dictDataIds);

    DictDataEntity selectByDictIdAndValue(@Param("dictId") Long dictId, @Param("dataValue") String dataValue);

    List<DictDataVO> getAll();
}
