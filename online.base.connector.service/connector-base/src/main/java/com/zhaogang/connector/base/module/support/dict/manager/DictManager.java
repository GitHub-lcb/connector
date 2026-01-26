package com.zhaogang.connector.base.module.support.dict.manager;

import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import com.zhaogang.connector.base.constant.CacheKeyConst;
import com.zhaogang.connector.base.module.support.dict.dao.DictDao;
import com.zhaogang.connector.base.module.support.dict.dao.DictDataDao;
import com.zhaogang.connector.base.module.support.dict.domain.entity.DictDataEntity;
import com.zhaogang.connector.base.module.support.dict.domain.entity.DictEntity;
import com.zhaogang.connector.base.module.support.dict.domain.vo.DictDataVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 数据字典 缓存
 *
 * @Author 连接器-主任-卓大
 * @Date 2025-03-25 22:25:04
 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */

@Service
public class DictManager {

    @Resource
    private DictDao dictDao;

    @Resource
    private DictDataDao dictDataDao;


    /**
     * 获取字典
     */
    @Cacheable(value = CacheKeyConst.Dict.DICT_DATA, key = "#dictCode + '_' + #dataValue")
    public DictDataVO getDictData(String dictCode, String dataValue) {
        DictEntity dictEntity = dictDao.selectByCode(dictCode);
        if (dictEntity == null) {
            return null;
        }

        DictDataEntity dictDataEntity = dictDataDao.selectByDictIdAndValue(dictEntity.getDictId(), dataValue);
        return SmartBeanUtil.copy(dictDataEntity, DictDataVO.class);
    }

}
