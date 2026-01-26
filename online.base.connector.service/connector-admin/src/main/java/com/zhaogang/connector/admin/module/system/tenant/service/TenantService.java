package com.zhaogang.connector.admin.module.system.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import com.zhaogang.connector.base.common.util.SmartPageUtil;
import com.zhaogang.connector.admin.module.system.tenant.dao.TenantDao;
import com.zhaogang.connector.admin.module.system.tenant.domain.entity.TenantEntity;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantAddForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantQueryForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantUpdateForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.vo.TenantVO;
import com.zhaogang.connector.admin.module.system.tenant.domain.vo.TenantExcelVO;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantImportForm;
import com.zhaogang.connector.base.common.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.idev.excel.FastExcel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TenantService {

    @Resource
    private TenantDao tenantDao;

    public ResponseDTO<PageResult<TenantVO>> queryPage(TenantQueryForm queryForm) {
        Page page = SmartPageUtil.convert2PageQuery(queryForm);
        List<TenantVO> list = tenantDao.queryPage(page, queryForm);
        PageResult<TenantVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }

    public ResponseDTO<String> add(TenantAddForm addForm) {
        TenantEntity entity = SmartBeanUtil.copy(addForm, TenantEntity.class);
        tenantDao.insert(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(TenantUpdateForm updateForm) {
        TenantEntity entity = SmartBeanUtil.copy(updateForm, TenantEntity.class);
        tenantDao.updateById(entity);
        return ResponseDTO.ok();
    }
    
    public ResponseDTO<String> delete(Long tenantId) {
        if (tenantId == 1) {
            return ResponseDTO.userErrorParam("默认租户不能删除");
        }
        tenantDao.deleteById(tenantId);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        if (idList.contains(1L)) {
            return ResponseDTO.userErrorParam("默认租户不能删除");
        }
        tenantDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> importTenant(MultipartFile file) {
        List<TenantImportForm> dataList;
        try {
            dataList = FastExcel.read(file.getInputStream()).head(TenantImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        for (TenantImportForm importForm : dataList) {
            TenantEntity entity = SmartBeanUtil.copy(importForm, TenantEntity.class);
            entity.setStatus(1);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            tenantDao.insert(entity);
        }
        return ResponseDTO.okMsg("成功导入" + dataList.size() + "条");
    }

    public List<TenantExcelVO> getAllTenant() {
        List<TenantEntity> entityList = tenantDao.selectList(null);
        return entityList.stream().map(e -> TenantExcelVO.builder()
                .tenantId(e.getTenantId())
                .tenantName(e.getTenantName())
                .contactPerson(e.getContactPerson())
                .contactPhone(e.getContactPhone())
                .statusName(e.getStatus() == 1 ? "正常" : "禁用")
                .expireTime(e.getExpireTime())
                .createTime(e.getCreateTime())
                .build()).collect(Collectors.toList());
    }
}
