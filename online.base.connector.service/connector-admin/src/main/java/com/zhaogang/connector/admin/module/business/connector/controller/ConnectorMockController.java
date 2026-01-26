package com.zhaogang.connector.admin.module.business.connector.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/connector/mock")
@Slf4j
@Tag(name = "Connector - Mock 服务")
public class ConnectorMockController {

    /**
     * 回显接口：返回接收到的所有请求信息
     */
    @Operation(summary = "Echo回显")
    @RequestMapping(value = "/echo", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<Map<String, Object>> echo(HttpServletRequest request, @RequestBody(required = false) Object body) {
        Map<String, Object> response = new HashMap<>();
        
        // 1. Method
        response.put("method", request.getMethod());
        
        // 2. Headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        response.put("headers", headers);
        
        // 3. Query Params
        response.put("queryParams", request.getParameterMap());
        
        // 4. Body
        response.put("body", body);
        
        // 5. Request URL
        response.put("url", request.getRequestURL().toString());
        
        log.info("Mock Echo Request: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 延迟响应接口：模拟耗时操作
     * @param ms 延迟毫秒数
     */
    @Operation(summary = "延迟响应")
    @GetMapping("/delay")
    public Map<String, Object> delay(@RequestParam(defaultValue = "1000") long ms) throws InterruptedException {
        log.info("Mock Delay Request: {}ms", ms);
        TimeUnit.MILLISECONDS.sleep(ms);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("delayed_ms", ms);
        response.put("message", "Operation completed after " + ms + "ms");
        return response;
    }

    /**
     * 状态码接口：模拟不同的HTTP状态码
     * @param code 期望返回的状态码
     */
    @Operation(summary = "模拟状态码")
    @RequestMapping("/status/{code}")
    public ResponseEntity<Map<String, Object>> status(@PathVariable int code) {
        log.info("Mock Status Request: {}", code);
        Map<String, Object> response = new HashMap<>();
        response.put("requested_status", code);
        
        if (code >= 400) {
            response.put("error", "Simulated Error");
            response.put("message", "This is a simulated error response with status " + code);
        } else {
            response.put("status", "success");
            response.put("message", "This is a simulated success response with status " + code);
        }
        
        return ResponseEntity.status(code).body(response);
    }

    /**
     * 模拟订单创建接口
     */
    @Operation(summary = "模拟创建订单")
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData) {
        log.info("Mock Create Order: {}", orderData);
        Map<String, Object> response = new HashMap<>();
        
        // 简单的校验模拟
        if (!orderData.containsKey("orderId") && !orderData.containsKey("id")) {
             response.put("error", "Missing orderId");
             return ResponseEntity.badRequest().body(response);
        }

        response.put("status", "created");
        response.put("orderId", orderData.getOrDefault("orderId", orderData.get("id")));
        response.put("timestamp", System.currentTimeMillis());
        response.put("receivedData", orderData);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 模拟需要认证的接口
     */
    @Operation(summary = "模拟认证接口")
    @GetMapping("/secure")
    public ResponseEntity<Map<String, Object>> secureResource(@RequestHeader(value = "Authorization", required = false) String auth) {
        Map<String, Object> response = new HashMap<>();
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        response.put("status", "authorized");
        response.put("user", "mock-user");
        response.put("token", auth);
        return ResponseEntity.ok(response);
    }
}
