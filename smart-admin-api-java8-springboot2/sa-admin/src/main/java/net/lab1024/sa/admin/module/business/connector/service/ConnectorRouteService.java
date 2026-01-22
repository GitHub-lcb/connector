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
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
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

        // Record history
        ConnectorConfigHistoryEntity history = new ConnectorConfigHistoryEntity();
        history.setRouteId(updateForm.getId());
        history.setOldConfig(oldEntity.getMappingConfig());
        history.setNewConfig(updateForm.getMappingConfig());
        history.setChangedBy("admin"); // TODO: Get current user
        history.setCreateTime(LocalDateTime.now());
        connectorConfigHistoryDao.insert(history);

        ConnectorRouteEntity entity = SmartBeanUtil.copy(updateForm, ConnectorRouteEntity.class);
        // Ensure version increment or handle optimistic locking if needed
        entity.setVersion(oldEntity.getVersion() != null ? oldEntity.getVersion() + 1 : 1);
        
        connectorRouteDao.updateById(entity);
        return ResponseDTO.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(String id) {
        connectorRouteDao.deleteById(id);
        return ResponseDTO.ok();
    }
}
