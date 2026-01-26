package com.zhaogang.connector.admin.module.business.connector.controller;

import com.zhaogang.connector.admin.module.business.connector.domain.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteAddForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteLogQueryForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteQueryForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorRouteUpdateForm;
import com.zhaogang.connector.admin.module.business.connector.service.ConnectorRouteService;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import com.zhaogang.connector.base.common.domain.ValidateList;
import com.zhaogang.connector.base.common.util.SmartExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chenbo.li
 */
@RestController
@Tag(name = "Connector - 路由管理")
@RequestMapping("/connector/route")
public class ConnectorAdminController {

    @Resource
    private ConnectorRouteService connectorRouteService;

    @Operation(summary = "分页查询路由")
    @PostMapping("/query")
    @SaCheckPermission("connector:route:query")
    public ResponseDTO<PageResult<ConnectorRouteVO>> query(@RequestBody @Valid ConnectorRouteQueryForm queryForm) {
        return connectorRouteService.queryPage(queryForm);
    }

    @Operation(summary = "查询路由详情")
    @GetMapping("/detail/{id}")
    @SaCheckPermission("connector:route:query")
    public ResponseDTO<ConnectorRouteVO> getDetail(@PathVariable String id) {
        return connectorRouteService.getDetail(id);
    }

    @Operation(summary = "添加路由")
    @PostMapping("/add")
    @SaCheckPermission("connector:route:add")
    public ResponseDTO<String> add(@RequestBody @Valid ConnectorRouteAddForm addForm) {
        return connectorRouteService.add(addForm);
    }

    @Operation(summary = "更新路由")
    @PostMapping("/update")
    @SaCheckPermission("connector:route:update")
    public ResponseDTO<String> update(@RequestBody @Valid ConnectorRouteUpdateForm updateForm) {
        return connectorRouteService.update(updateForm);
    }

    @Operation(summary = "删除路由")
    @GetMapping("/delete/{id}")
    @SaCheckPermission("connector:route:delete")
    public ResponseDTO<String> delete(@PathVariable String id) {
        return connectorRouteService.delete(id);
    }

    @Operation(summary = "批量删除路由")
    @PostMapping("/batchDelete")
    @SaCheckPermission("connector:route:batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody @Valid ValidateList<String> idList) {
        return connectorRouteService.batchDelete(idList);
    }

    @Operation(summary = "导入路由")
    @PostMapping("/import")
    @SaCheckPermission("connector:route:import")
    public ResponseDTO<String> importRoute(@RequestParam MultipartFile file) {
        return connectorRouteService.importRoute(file);
    }

    @Operation(summary = "导出路由")
    @GetMapping("/export")
    @SaCheckPermission("connector:route:export")
    public void exportRoute(HttpServletResponse response) throws IOException {
        List<ConnectorRouteExcelVO> list = connectorRouteService.getAllRoutes();
        SmartExcelUtil.exportExcel(response, "路由列表.xlsx", "路由", ConnectorRouteExcelVO.class, list);
    }

    @Operation(summary = "分页查询日志")
    @PostMapping("/log/query")
    @SaCheckPermission("connector:routeLog:query")
    public ResponseDTO<PageResult<ConnectorRouteLogVO>> queryLog(@RequestBody @Valid ConnectorRouteLogQueryForm queryForm) {
        return connectorRouteService.queryLogPage(queryForm);
    }

    @Operation(summary = "导出日志")
    @PostMapping("/log/export")
    @SaCheckPermission("connector:routeLog:export")
    public void exportLog(@RequestBody @Valid ConnectorRouteLogQueryForm queryForm, HttpServletResponse response) throws IOException {
        List<ConnectorRouteLogExcelVO> list = connectorRouteService.exportLog(queryForm);
        SmartExcelUtil.exportExcel(response, "请求日志.xlsx", "日志", ConnectorRouteLogExcelVO.class, list);
    }

    @Operation(summary = "查询配置历史")
    @GetMapping("/history/{routeId}")
    @SaCheckPermission("connector:route:query")
    public ResponseDTO<List<ConnectorConfigHistoryVO>> queryHistory(@PathVariable String routeId) {
        return connectorRouteService.queryHistory(routeId);
    }
    
    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    @SaCheckPermission("connector:route:query")
    public ResponseDTO<Map<String, Object>> getStats() {
        return connectorRouteService.getStats();
    }

    @Operation(summary = "配置回滚")
    @GetMapping("/rollback/{historyId}")
    @SaCheckPermission("connector:route:update")
    public ResponseDTO<String> rollback(@PathVariable String historyId) {
        return connectorRouteService.rollback(historyId);
    }
}
