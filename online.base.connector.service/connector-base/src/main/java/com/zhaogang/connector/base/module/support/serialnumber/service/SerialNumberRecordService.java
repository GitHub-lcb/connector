package com.zhaogang.connector.base.module.support.serialnumber.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.util.SmartPageUtil;
import com.zhaogang.connector.base.module.support.serialnumber.dao.SerialNumberRecordDao;
import com.zhaogang.connector.base.module.support.serialnumber.domain.SerialNumberRecordEntity;
import com.zhaogang.connector.base.module.support.serialnumber.domain.SerialNumberRecordQueryForm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据序列号 记录
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Service
public class SerialNumberRecordService {

    @Resource
    private SerialNumberRecordDao serialNumberRecordDao;

    public PageResult<SerialNumberRecordEntity> query(SerialNumberRecordQueryForm queryForm) {
        Page page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SerialNumberRecordEntity> recordList = serialNumberRecordDao.query(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, recordList);
    }
}
