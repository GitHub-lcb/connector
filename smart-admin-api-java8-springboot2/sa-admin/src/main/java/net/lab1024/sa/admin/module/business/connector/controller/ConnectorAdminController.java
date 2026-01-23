package net.lab1024.sa.admin.module.business.connector.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteAddForm;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteLogQueryForm;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteQueryForm;
import net.lab1024.sa.admin.module.business.connector.domain.form.ConnectorRouteUpdateForm;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorConfigHistoryVO;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorRouteLogVO;
import net.lab1024.sa.admin.module.business.connector.domain.vo.ConnectorRouteVO;
import net.lab1024.sa.admin.module.business.connector.service.ConnectorRouteService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Connector - 路由管理")
@RequestMapping("/connector/route")
public class ConnectorAdminController {

    @Resource
    private ConnectorRouteService connectorRouteService;

    @Operation(summary = "分页查询路由")
    @PostMapping("/query")
    public ResponseDTO<PageResult<ConnectorRouteVO>> query(@RequestBody @Valid ConnectorRouteQueryForm queryForm) {
        return connectorRouteService.queryPage(queryForm);
    }

    @Operation(summary = "查询路由详情")
    @GetMapping("/detail/{id}")
    public ResponseDTO<ConnectorRouteVO> getDetail(@PathVariable String id) {
        return connectorRouteService.getDetail(id);
    }

    @Operation(summary = "添加路由")
    @PostMapping("/add")
    public ResponseDTO<String> add(@RequestBody @Valid ConnectorRouteAddForm addForm) {
        return connectorRouteService.add(addForm);
    }

    @Operation(summary = "更新路由")
    @PostMapping("/update")
    public ResponseDTO<String> update(@RequestBody @Valid ConnectorRouteUpdateForm updateForm) {
        return connectorRouteService.update(updateForm);
    }

    @Operation(summary = "删除路由")
    @GetMapping("/delete/{id}")
    public ResponseDTO<String> delete(@PathVariable String id) {
        return connectorRouteService.delete(id);
    }

    @Operation(summary = "分页查询日志")
    @PostMapping("/log/query")
    public ResponseDTO<PageResult<ConnectorRouteLogVO>> queryLog(@RequestBody @Valid ConnectorRouteLogQueryForm queryForm) {
        return connectorRouteService.queryLogPage(queryForm);
    }

    @Operation(summary = "查询配置历史")
    @GetMapping("/history/{routeId}")
    public ResponseDTO<List<ConnectorConfigHistoryVO>> queryHistory(@PathVariable String routeId) {
        return connectorRouteService.queryHistory(routeId);
    }
    
    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public ResponseDTO<Map<String, Object>> getStats() {
        return connectorRouteService.getStats();
    }

    @Operation(summary = "配置回滚")
    @GetMapping("/rollback/{historyId}")
    public ResponseDTO<String> rollback(@PathVariable String historyId) {
        return connectorRouteService.rollback(historyId);
    }
}
