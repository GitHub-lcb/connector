package com.zhaogang.connector.admin.module.business.connector.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.admin.module.business.connector.dao.ConnectorSecretKeyDao;
import com.zhaogang.connector.admin.module.business.connector.domain.entity.ConnectorSecretKeyEntity;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyAddForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyQueryForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyUpdateForm;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorSecretKeyVO;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import com.zhaogang.connector.base.common.util.SmartPageUtil;
import com.zhaogang.connector.base.common.util.SmartRequestUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyImportForm;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorSecretKeyExcelVO;
import com.zhaogang.connector.base.common.exception.BusinessException;
import cn.idev.excel.FastExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.collections4.CollectionUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 连接器密钥服务
 */
@Slf4j
@Service
public class ConnectorSecretKeyService {

    @Resource
    private ConnectorSecretKeyDao connectorSecretKeyDao;

    /**
     * 分页查询
     */
    public ResponseDTO<PageResult<ConnectorSecretKeyVO>> queryPage(ConnectorSecretKeyQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        QueryWrapper<ConnectorSecretKeyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted_flag", false);
        if (StringUtils.isNotBlank(queryForm.getTitle())) {
            queryWrapper.like("title", queryForm.getTitle());
        }
        if (StringUtils.isNotBlank(queryForm.getAccessKey())) {
            queryWrapper.like("access_key", queryForm.getAccessKey());
        }
        if (queryForm.getStatus() != null) {
            queryWrapper.eq("status", queryForm.getStatus());
        }
        
        // Dynamic Sorting
        if (CollectionUtils.isNotEmpty(queryForm.getSortItemList())) {
             SmartPageUtil.buildSort(queryWrapper, queryForm.getSortItemList());
        } else {
             queryWrapper.orderByDesc("create_time");
        }

        Page<ConnectorSecretKeyEntity> entityPage = connectorSecretKeyDao.selectPage((Page<ConnectorSecretKeyEntity>) page, queryWrapper);
        PageResult<ConnectorSecretKeyVO> pageResult = SmartPageUtil.convert2PageResult(entityPage, entityPage.getRecords(), ConnectorSecretKeyVO.class);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ConnectorSecretKeyAddForm addForm) {
        ConnectorSecretKeyEntity entity = SmartBeanUtil.copy(addForm, ConnectorSecretKeyEntity.class);
        
        // 生成 AccessKey 和 SecretKey
        entity.setAccessKey("AK" + RandomStringUtils.randomAlphanumeric(16).toUpperCase());
        entity.setSecretKey("SK" + UUID.randomUUID().toString().replace("-", "") + RandomStringUtils.randomAlphanumeric(8));
        
        entity.setDeletedFlag(false);
        entity.setCreateUserId(SmartRequestUtil.getRequestUserId());
        
        connectorSecretKeyDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     */
    public ResponseDTO<String> update(ConnectorSecretKeyUpdateForm updateForm) {
        ConnectorSecretKeyEntity entity = connectorSecretKeyDao.selectById(updateForm.getKeyId());
        if (entity == null || entity.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("Key not found");
        }

        if (updateForm.getTitle() != null) entity.setTitle(updateForm.getTitle());
        if (updateForm.getRemark() != null) entity.setRemark(updateForm.getRemark());
        if (updateForm.getStatus() != null) entity.setStatus(updateForm.getStatus());
        
        if (Boolean.TRUE.equals(updateForm.getResetSecret())) {
             entity.setSecretKey("SK" + UUID.randomUUID().toString().replace("-", "") + RandomStringUtils.randomAlphanumeric(8));
        }

        entity.setUpdateUserId(SmartRequestUtil.getRequestUserId());
        connectorSecretKeyDao.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 删除
     */
    public ResponseDTO<String> delete(Long keyId) {
        ConnectorSecretKeyEntity entity = connectorSecretKeyDao.selectById(keyId);
        if (entity == null) {
            return ResponseDTO.userErrorParam("Key not found");
        }
        
        entity.setDeletedFlag(true);
        entity.setUpdateUserId(SmartRequestUtil.getRequestUserId());
        connectorSecretKeyDao.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        
        connectorSecretKeyDao.selectBatchIds(idList).forEach(e -> {
            e.setDeletedFlag(true);
            e.setUpdateUserId(SmartRequestUtil.getRequestUserId());
            connectorSecretKeyDao.updateById(e);
        });
        
        return ResponseDTO.ok();
    }

    /**
     * 导入
     */
    public ResponseDTO<String> importKey(MultipartFile file) {
        List<ConnectorSecretKeyImportForm> dataList;
        try {
            dataList = FastExcel.read(file.getInputStream()).head(ConnectorSecretKeyImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        for (ConnectorSecretKeyImportForm importForm : dataList) {
            ConnectorSecretKeyAddForm addForm = new ConnectorSecretKeyAddForm();
            addForm.setTitle(importForm.getTitle());
            addForm.setRemark(importForm.getRemark());
            addForm.setStatus(1); // Default active
            add(addForm);
        }
        return ResponseDTO.okMsg("成功导入" + dataList.size() + "条");
    }

    /**
     * 导出
     */
    public List<ConnectorSecretKeyExcelVO> getAllKeys() {
        QueryWrapper<ConnectorSecretKeyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted_flag", false);
        List<ConnectorSecretKeyEntity> entityList = connectorSecretKeyDao.selectList(queryWrapper);
        return entityList.stream().map(e -> ConnectorSecretKeyExcelVO.builder()
                .keyId(e.getKeyId())
                .title(e.getTitle())
                .accessKey(e.getAccessKey())
                .secretKey(e.getSecretKey())
                .status(e.getStatus() == 1 ? "启用" : "禁用")
                .remark(e.getRemark())
                .createTime(e.getCreateTime())
                .build()).collect(Collectors.toList());
    }
}
