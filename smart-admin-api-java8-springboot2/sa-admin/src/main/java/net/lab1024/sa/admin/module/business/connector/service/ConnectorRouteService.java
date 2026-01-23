package net.lab1024.sa.admin.module.business.connector.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.connector.dao.ConnectorConfigHistoryDao;
import net.lab1024.sa.admin.module.business.connector.dao.ConnectorRouteDao;
import net.lab1024.sa.admin.module.business.connector.dao.ConnectorRouteLogDao;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorConfigHistoryEntity;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorRouteEntity;
import net.lab1024.sa.admin.module.business.connector.domain.entity.ConnectorRouteLogEntity;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteAddForm;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteLogQueryForm;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteQueryForm;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteUpdateForm;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorConfigHistoryVO;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorRouteLogVO;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorRouteVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由管理服务
 */
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
        queryWrapper.orderByDesc("created_at");
        
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
    
    public ResponseDTO<List<ConnectorConfigHistoryVO>> queryHistory(String routeId) {
        QueryWrapper<ConnectorConfigHistoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("route_id", routeId);
        queryWrapper.orderByDesc("created_at");
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
        if (updateForm.getStatus() != null) newEntity.setStatus(updateForm.getStatus());
        if (updateForm.getMappingConfig() != null) newEntity.setMappingConfig(updateForm.getMappingConfig());
        
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
        map.put("status", entity.getStatus());
        
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
            if (historyConfig.containsKey("status")) route.setStatus((String) historyConfig.get("status"));
            
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
