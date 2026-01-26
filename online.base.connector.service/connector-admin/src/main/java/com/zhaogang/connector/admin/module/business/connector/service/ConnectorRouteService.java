package com.zhaogang.connector.admin.module.business.connector.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.admin.module.business.connector.dao.ConnectorConfigHistoryDao;
import com.zhaogang.connector.admin.module.business.connector.dao.ConnectorRouteDao;
import com.zhaogang.connector.admin.module.business.connector.dao.ConnectorRouteLogDao;
import com.zhaogang.connector.admin.module.business.connector.domain.entity.ConnectorConfigHistoryEntity;
import com.zhaogang.connector.admin.module.business.connector.domain.entity.ConnectorRouteEntity;
import com.zhaogang.connector.admin.module.business.connector.domain.entity.ConnectorRouteLogEntity;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteAddForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteLogQueryForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteQueryForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteUpdateForm;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorConfigHistoryVO;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorRouteLogVO;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorRouteVO;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.RequestUser;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import com.zhaogang.connector.base.common.util.SmartPageUtil;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteImportForm;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorRouteExcelVO;
import com.zhaogang.connector.base.common.exception.BusinessException;
import cn.idev.excel.FastExcel;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorRouteLogExcelVO;
import com.zhaogang.connector.base.common.util.SmartRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.collections4.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 路由管理服务
 */
@Slf4j
@Service
public class ConnectorRouteService {

    @Resource
    private ConnectorRouteDao connectorRouteDao;
    
    @Resource
    private ConnectorConfigHistoryDao connectorConfigHistoryDao;
    
    @Resource
    private ConnectorRouteLogDao connectorRouteLogDao;

    public ResponseDTO<PageResult<ConnectorRouteVO>> queryPage(ConnectorRouteQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        QueryWrapper<ConnectorRouteEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(queryForm.getName())) {
            queryWrapper.like("name", queryForm.getName());
        }
        if (StringUtils.isNotBlank(queryForm.getStatus())) {
            queryWrapper.eq("status", queryForm.getStatus());
        }
        
        // Dynamic Sorting
        if (CollectionUtils.isNotEmpty(queryForm.getSortItemList())) {
             SmartPageUtil.buildSort(queryWrapper, queryForm.getSortItemList());
        } else {
             queryWrapper.orderByDesc("create_time");
        }
        
        Page<ConnectorRouteEntity> entityPage = connectorRouteDao.selectPage((Page<ConnectorRouteEntity>) page, queryWrapper);
        PageResult<ConnectorRouteVO> pageResult = SmartPageUtil.convert2PageResult(entityPage, entityPage.getRecords(), ConnectorRouteVO.class);
        return ResponseDTO.ok(pageResult);
    }
    
    public ResponseDTO<PageResult<ConnectorRouteLogVO>> queryLogPage(ConnectorRouteLogQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ConnectorRouteLogVO> list = connectorRouteLogDao.queryLogPage(page, queryForm);
        PageResult<ConnectorRouteLogVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }
    
    public List<ConnectorRouteLogExcelVO> exportLog(ConnectorRouteLogQueryForm queryForm) {
        // Query all logs (no pagination)
        queryForm.setPageNum(1L);
        queryForm.setPageSize(10000L); // Limit export size for safety
        
        // Reuse query logic from dao but without pagination if possible, or just use large page
        // Note: queryLogPage in DAO takes Page object.
        Page<?> page = new Page<>(1, 10000);
        List<ConnectorRouteLogVO> list = connectorRouteLogDao.queryLogPage(page, queryForm);
        
        return list.stream().map(e -> ConnectorRouteLogExcelVO.builder()
                .createTime(e.getCreateTime())
                .routeName(e.getRouteName())
                .channel(e.getChannel())
                .requestPath(e.getRequestPath())
                .statusCode(e.getStatusCode())
                .latencyMs(e.getLatencyMs())
                .errorMsg(e.getErrorMsg())
                .build()).collect(Collectors.toList());
    }
    
    public ResponseDTO<List<ConnectorConfigHistoryVO>> queryHistory(String routeId) {
        QueryWrapper<ConnectorConfigHistoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("route_id", routeId);
        queryWrapper.orderByDesc("create_time");
        List<ConnectorConfigHistoryEntity> list = connectorConfigHistoryDao.selectList(queryWrapper);
        return ResponseDTO.ok(SmartBeanUtil.copyList(list, ConnectorConfigHistoryVO.class));
    }
    
    public ResponseDTO<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total Routes
        stats.put("totalRoutes", connectorRouteDao.selectCount(null));
        
        // Active Routes
        stats.put("activeRoutes", connectorRouteDao.selectCount(new QueryWrapper<ConnectorRouteEntity>().eq("status", "active")));
        
        // Total Requests
        stats.put("totalRequests", connectorRouteLogDao.selectCount(null));
        
        // Error Rate (Status >= 400)
        long total = connectorRouteLogDao.selectCount(null);
        long errors = connectorRouteLogDao.selectCount(new QueryWrapper<ConnectorRouteLogEntity>().ge("status_code", 400));
        
        stats.put("errorRate", total > 0 ? (double) errors / total : 0);
        
        return ResponseDTO.ok(stats);
    }

    public ResponseDTO<ConnectorRouteVO> getDetail(String id) {
        ConnectorRouteEntity entity = connectorRouteDao.selectById(id);
        if (entity == null) {
            return ResponseDTO.userErrorParam("Route not found");
        }
        return ResponseDTO.ok(SmartBeanUtil.copy(entity, ConnectorRouteVO.class));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(ConnectorRouteAddForm addForm) {
        // Validate targetUrl if forwardFlag is true (default is true)
        if ((addForm.getForwardFlag() == null || addForm.getForwardFlag()) && StringUtils.isBlank(addForm.getTargetUrl())) {
            return ResponseDTO.userErrorParam("Target URL is required when forwarding is enabled");
        }

        // Check duplicate source path
        QueryWrapper<ConnectorRouteEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("source_path", addForm.getSourcePath());
        queryWrapper.eq("method", addForm.getMethod());
        if (connectorRouteDao.selectCount(queryWrapper) > 0) {
            return ResponseDTO.userErrorParam("Source path and method already exists");
        }

        ConnectorRouteEntity entity = SmartBeanUtil.copy(addForm, ConnectorRouteEntity.class);
        entity.setVersion(1);
        connectorRouteDao.insert(entity);
        return ResponseDTO.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(ConnectorRouteUpdateForm updateForm) {
        ConnectorRouteEntity oldEntity = connectorRouteDao.selectById(updateForm.getId());
        if (oldEntity == null) {
            return ResponseDTO.userErrorParam("Route not found");
        }

        // 1. Snapshot Old Config
        Map<String, Object> oldConfigMap = createConfigSnapshot(oldEntity);
        
        // 2. Prepare New Entity (Merge old + update)
        // Create a copy of old entity first to ensure we have all fields
        ConnectorRouteEntity newEntity = SmartBeanUtil.copy(oldEntity, ConnectorRouteEntity.class);
        
        // Update fields from form if they are present (support partial update)
        if (updateForm.getName() != null) newEntity.setName(updateForm.getName());
        if (updateForm.getChannel() != null) newEntity.setChannel(updateForm.getChannel());
        if (updateForm.getSourcePath() != null) newEntity.setSourcePath(updateForm.getSourcePath());
        if (updateForm.getTargetUrl() != null) newEntity.setTargetUrl(updateForm.getTargetUrl());
        if (updateForm.getMethod() != null) newEntity.setMethod(updateForm.getMethod());
        if (updateForm.getForwardFlag() != null) newEntity.setForwardFlag(updateForm.getForwardFlag());
        if (updateForm.getStatus() != null) newEntity.setStatus(updateForm.getStatus());
        if (updateForm.getMappingConfig() != null) newEntity.setMappingConfig(updateForm.getMappingConfig());
        if (updateForm.getScriptType() != null) newEntity.setScriptType(updateForm.getScriptType());
        if (updateForm.getScriptContent() != null) newEntity.setScriptContent(updateForm.getScriptContent());
        
        // Validate targetUrl if forwardFlag is true
        if ((newEntity.getForwardFlag() == null || newEntity.getForwardFlag()) && StringUtils.isBlank(newEntity.getTargetUrl())) {
             return ResponseDTO.userErrorParam("Target URL is required when forwarding is enabled");
        }

        // 3. Snapshot New Config
        Map<String, Object> newConfigMap = createConfigSnapshot(newEntity);
        
        // 4. Record History
        ConnectorConfigHistoryEntity history = new ConnectorConfigHistoryEntity();
        history.setRouteId(updateForm.getId());
        history.setOldConfig(oldConfigMap);
        history.setNewConfig(newConfigMap);
        
        RequestUser user = SmartRequestUtil.getRequestUser();
        history.setChangedBy(user != null ? user.getUserName() : "unknown");
        
        history.setCreateTime(LocalDateTime.now());
        connectorConfigHistoryDao.insert(history);

        // 5. Update DB
        newEntity.setVersion(oldEntity.getVersion() != null ? oldEntity.getVersion() + 1 : 1);
        newEntity.setUpdateTime(LocalDateTime.now());
        connectorRouteDao.updateById(newEntity);
        
        return ResponseDTO.ok();
    }

    private Map<String, Object> createConfigSnapshot(ConnectorRouteEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", entity.getName());
        map.put("channel", entity.getChannel());
        map.put("sourcePath", entity.getSourcePath());
        map.put("targetUrl", entity.getTargetUrl());
        map.put("method", entity.getMethod());
        map.put("forwardFlag", entity.getForwardFlag());
        map.put("status", entity.getStatus());
        map.put("scriptType", entity.getScriptType());
        map.put("scriptContent", entity.getScriptContent());
        
        if (entity.getMappingConfig() != null) {
            map.put("mappingConfig", new HashMap<>(entity.getMappingConfig()));
        } else {
            map.put("mappingConfig", null);
        }
        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(String id) {
        connectorRouteDao.deleteById(id);
        return ResponseDTO.ok();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> batchDelete(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        connectorRouteDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> importRoute(MultipartFile file) {
        List<ConnectorRouteImportForm> dataList;
        try {
            dataList = FastExcel.read(file.getInputStream()).head(ConnectorRouteImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        for (ConnectorRouteImportForm importForm : dataList) {
            ConnectorRouteEntity entity = SmartBeanUtil.copy(importForm, ConnectorRouteEntity.class);
            // Default values
            entity.setVersion(1);
            entity.setStatus("active");
            entity.setForwardFlag(true); 
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            
            // Check duplicates
            QueryWrapper<ConnectorRouteEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("source_path", entity.getSourcePath());
            queryWrapper.eq("method", entity.getMethod());
            if (connectorRouteDao.selectCount(queryWrapper) == 0) {
                 connectorRouteDao.insert(entity);
            }
        }
        return ResponseDTO.okMsg("成功导入" + dataList.size() + "条");
    }

    public List<ConnectorRouteExcelVO> getAllRoutes() {
        List<ConnectorRouteEntity> entityList = connectorRouteDao.selectList(null);
        return entityList.stream().map(e -> ConnectorRouteExcelVO.builder()
                .id(e.getId())
                .name(e.getName())
                .channel(e.getChannel())
                .sourcePath(e.getSourcePath())
                .targetUrl(e.getTargetUrl())
                .method(e.getMethod())
                .status("active".equals(e.getStatus()) ? "启用" : "停用")
                .createTime(e.getCreateTime())
                .build()).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> rollback(String historyId) {
        ConnectorConfigHistoryEntity history = connectorConfigHistoryDao.selectById(historyId);
        if (history == null) {
            return ResponseDTO.userErrorParam("History record not found");
        }

        ConnectorRouteEntity route = connectorRouteDao.selectById(history.getRouteId());
        if (route == null) {
            return ResponseDTO.userErrorParam("Route not found");
        }

        // 1. Snapshot Current State (Before Rollback)
        Map<String, Object> oldConfigMap = createConfigSnapshot(route);
        
        // Use OldConfig to revert the change represented by this history record
        Map<String, Object> historyConfig = history.getOldConfig();
        if (historyConfig == null || historyConfig.isEmpty()) {
             return ResponseDTO.userErrorParam("Cannot rollback: Previous configuration is empty.");
        }

        // 2. Apply Rollback to Route Object
        if (historyConfig.containsKey("mappingConfig") && historyConfig.containsKey("targetUrl")) {
            // New format: Full snapshot
            if (historyConfig.containsKey("name")) route.setName((String) historyConfig.get("name"));
            if (historyConfig.containsKey("channel")) route.setChannel((String) historyConfig.get("channel"));
            if (historyConfig.containsKey("sourcePath")) route.setSourcePath((String) historyConfig.get("sourcePath"));
            if (historyConfig.containsKey("targetUrl")) route.setTargetUrl((String) historyConfig.get("targetUrl"));
            if (historyConfig.containsKey("method")) route.setMethod((String) historyConfig.get("method"));
            if (historyConfig.containsKey("forwardFlag")) route.setForwardFlag((Boolean) historyConfig.get("forwardFlag"));
            if (historyConfig.containsKey("status")) route.setStatus((String) historyConfig.get("status"));
            if (historyConfig.containsKey("scriptType")) route.setScriptType((String) historyConfig.get("scriptType"));
            if (historyConfig.containsKey("scriptContent")) route.setScriptContent((String) historyConfig.get("scriptContent"));
            
            // Handle mappingConfig safely
            if (historyConfig.containsKey("mappingConfig")) {
                Object mappingConfigObj = historyConfig.get("mappingConfig");
                if (mappingConfigObj instanceof Map) {
                    route.setMappingConfig((Map<String, Object>) mappingConfigObj);
                } else {
                    route.setMappingConfig(null);
                }
            }
        } else {
            // Old format: Only mapping config
            route.setMappingConfig(historyConfig);
        }
        
        // 3. Snapshot New State (After Rollback)
        Map<String, Object> newConfigMap = createConfigSnapshot(route);
        
        // Check if configuration actually changed
        if (oldConfigMap.equals(newConfigMap)) {
            return ResponseDTO.userErrorParam("Current configuration is identical to the rollback target. No changes made.");
        }

        // 4. Record New History
        ConnectorConfigHistoryEntity newHistory = new ConnectorConfigHistoryEntity();
        newHistory.setRouteId(route.getId());
        newHistory.setOldConfig(oldConfigMap);
        newHistory.setNewConfig(newConfigMap);
        
        RequestUser user = SmartRequestUtil.getRequestUser();
        newHistory.setChangedBy(user != null ? user.getUserName() : "unknown");
        
        newHistory.setCreateTime(LocalDateTime.now());
        connectorConfigHistoryDao.insert(newHistory);

        // 5. Update DB
        route.setVersion(route.getVersion() + 1);
        route.setUpdateTime(LocalDateTime.now());
        connectorRouteDao.updateById(route);

        return ResponseDTO.ok();
    }
}
