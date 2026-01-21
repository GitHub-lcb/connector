package com.connector.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.connector.entity.Route;
import com.connector.entity.RouteLog;
import com.connector.mapper.RouteLogMapper;
import com.connector.mapper.RouteMapper;
import com.connector.service.TransformService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final RouteMapper routeMapper;
    private final RouteLogMapper routeLogMapper;
    private final TransformService transformService;

    // Routes CRUD
    @GetMapping("/routes")
    public List<Route> getRoutes() {
        return routeMapper.selectList(new QueryWrapper<Route>().orderByDesc("created_at"));
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<Route> getRoute(@PathVariable String id) {
        Route route = routeMapper.selectById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }

    @PostMapping("/routes")
    public Route createRoute(@RequestBody Route route) {
        routeMapper.insert(route);
        return route;
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable String id, @RequestBody Route route) {
        route.setId(id);
        int rows = routeMapper.updateById(route);
        return rows > 0 ? ResponseEntity.ok(routeMapper.selectById(id)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable String id) {
        routeMapper.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Logs
    @GetMapping("/logs")
    public Map<String, Object> getLogs(
            @RequestParam(required = false) String routeId, 
            @RequestParam(defaultValue = "1") int page, 
            @RequestParam(defaultValue = "20") int size) {
        
        QueryWrapper<RouteLog> query = new QueryWrapper<>();
        if (routeId != null && !routeId.isEmpty()) {
            query.eq("route_id", routeId);
        }
        query.orderByDesc("created_at");
        
        Page<RouteLog> pageParam = new Page<>(page, size);
        Page<RouteLog> resultPage = routeLogMapper.selectPage(pageParam, query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("items", resultPage.getRecords());
        response.put("total", resultPage.getTotal());
        response.put("page", resultPage.getCurrent());
        response.put("size", resultPage.getSize());
        response.put("pages", resultPage.getPages());
        
        return response;
    }

    // Dashboard Stats
    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total Routes
        stats.put("totalRoutes", routeMapper.selectCount(null));
        
        // Active Routes
        stats.put("activeRoutes", routeMapper.selectCount(new QueryWrapper<Route>().eq("status", "active")));
        
        // Total Requests (Today or All time - let's do all time for simplicity first)
        stats.put("totalRequests", routeLogMapper.selectCount(null));
        
        // Error Rate (Status >= 400)
        long total = routeLogMapper.selectCount(null);
        long errors = routeLogMapper.selectCount(new QueryWrapper<RouteLog>().ge("status_code", 400));
        
        stats.put("errorRate", total > 0 ? (double) errors / total : 0);
        
        return stats;
    }

    private void logRequest(String routeId, String path, int statusCode, long latency, String error) {
        RouteLog log = new RouteLog();
        log.setRouteId(routeId);
        log.setRequestPath(path);
        log.setStatusCode(statusCode);
        log.setLatencyMs(latency);
        log.setErrorMsg(error);
        routeLogMapper.insert(log);
    }
}
