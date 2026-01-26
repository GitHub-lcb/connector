package com.zhaogang.connector.admin.module.business.oa.bank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.admin.module.business.oa.bank.domain.*;
import lombok.extern.slf4j.Slf4j;
import com.zhaogang.connector.admin.module.business.oa.bank.dao.BankDao;
import com.zhaogang.connector.admin.module.business.oa.enterprise.dao.EnterpriseDao;
import com.zhaogang.connector.admin.module.business.oa.enterprise.domain.entity.EnterpriseEntity;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import com.zhaogang.connector.base.common.util.SmartPageUtil;
import com.zhaogang.connector.base.module.support.datatracer.constant.DataTracerConst;
import com.zhaogang.connector.base.module.support.datatracer.constant.DataTracerTypeEnum;
import com.zhaogang.connector.base.module.support.datatracer.service.DataTracerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * OA办公-OA银行信息
 *
 * @Author 连接器:善逸
 * @Date 2022/6/23 21:59:22
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
@Service
@Slf4j
public class BankService {

    @Resource
    private BankDao bankDao;

    @Resource
    private EnterpriseDao enterpriseDao;

    @Resource
    private DataTracerService dataTracerService;

    /**
     * 分页查询银行信息
     */
    public ResponseDTO<PageResult<BankVO>> queryByPage(BankQueryForm queryForm) {
        queryForm.setDeletedFlag(Boolean.FALSE);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<BankVO> bankList = bankDao.queryPage(page, queryForm);
        PageResult<BankVO> pageResult = SmartPageUtil.convert2PageResult(page, bankList);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 根据企业ID查询不分页的银行列表
     */
    public ResponseDTO<List<BankVO>> queryList(Long enterpriseId) {
        BankQueryForm queryForm = new BankQueryForm();
        queryForm.setEnterpriseId(enterpriseId);
        queryForm.setDeletedFlag(Boolean.FALSE);
        List<BankVO> bankList = bankDao.queryPage(null, queryForm);
        return ResponseDTO.ok(bankList);
    }

    /**
     * 查询银行信息详情
     */
    public ResponseDTO<BankVO> getDetail(Long bankId) {
        // 校验银行信息是否存在
        BankVO bankVO = bankDao.getDetail(bankId, Boolean.FALSE);
        if (Objects.isNull(bankVO)) {
            return ResponseDTO.userErrorParam("银行信息不存在");
        }
        return ResponseDTO.ok(bankVO);
    }

    /**
     * 新建银行信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> createBank(BankCreateForm createVO) {
        Long enterpriseId = createVO.getEnterpriseId();
        // 校验企业是否存在
        EnterpriseEntity enterpriseDetail = enterpriseDao.selectById(enterpriseId);
        if (Objects.isNull(enterpriseDetail) || enterpriseDetail.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("企业不存在");
        }
        // 验证银行信息账号是否重复
        BankEntity validateBank = bankDao.queryByAccountNumber(enterpriseId, createVO.getAccountNumber(), null, Boolean.FALSE);
        if (Objects.nonNull(validateBank)) {
            return ResponseDTO.userErrorParam("银行信息账号重复");
        }
        // 数据插入
        BankEntity insertBank = SmartBeanUtil.copy(createVO, BankEntity.class);
        bankDao.insert(insertBank);
        dataTracerService.addTrace(enterpriseId, DataTracerTypeEnum.OA_ENTERPRISE, "新增银行:" + DataTracerConst.HTML_BR + dataTracerService.getChangeContent(insertBank));
        return ResponseDTO.ok();
    }

    /**
     * 编辑银行信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> updateBank(BankUpdateForm updateVO) {
        Long enterpriseId = updateVO.getEnterpriseId();
        // 校验企业是否存在
        EnterpriseEntity enterpriseDetail = enterpriseDao.selectById(enterpriseId);
        if (Objects.isNull(enterpriseDetail) || enterpriseDetail.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("企业不存在");
        }
        Long bankId = updateVO.getBankId();
        // 校验银行信息是否存在
        BankEntity bankDetail = bankDao.selectById(bankId);
        if (Objects.isNull(bankDetail) || bankDetail.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("银行信息不存在");
        }
        // 验证银行信息账号是否重复
        BankEntity validateBank = bankDao.queryByAccountNumber(updateVO.getEnterpriseId(), updateVO.getAccountNumber(), bankId, Boolean.FALSE);
        if (Objects.nonNull(validateBank)) {
            return ResponseDTO.userErrorParam("银行信息账号重复");
        }
        // 数据编辑
        BankEntity updateBank = SmartBeanUtil.copy(updateVO, BankEntity.class);
        bankDao.updateById(updateBank);
        dataTracerService.addTrace(enterpriseId, DataTracerTypeEnum.OA_ENTERPRISE, "更新银行:" + DataTracerConst.HTML_BR + dataTracerService.getChangeContent(bankDetail, updateBank));
        return ResponseDTO.ok();
    }


    /**
     * 删除银行信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> deleteBank(Long bankId) {
        // 校验银行信息是否存在
        BankEntity bankDetail = bankDao.selectById(bankId);
        if (Objects.isNull(bankDetail) || bankDetail.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("银行信息不存在");
        }
        bankDao.deleteBank(bankId, Boolean.TRUE);
        dataTracerService.addTrace(bankDetail.getEnterpriseId(), DataTracerTypeEnum.OA_ENTERPRISE, "删除银行:" + DataTracerConst.HTML_BR + dataTracerService.getChangeContent(bankDetail));
        return ResponseDTO.ok();
    }
}
