# 🚀 Connector - 下一代企业级路由转发与数据映射平台

> **连接万物，智通未来。**
> 
> Connector 是一个基于 Spring Boot 和 React/Vue 的全栈中间件解决方案，专注于简化微服务间的 API 路由、数据清洗与协议转换。

---

## 📚 项目简介

在微服务架构和遗留系统集成的复杂场景下，Connector 充当了智能网关的角色。它不仅是一个请求转发器，更是一个强大的**数据适配器**。通过可视化配置，用户可以轻松定义请求如何从源端流向目标端，并在途中完成数据的"华丽转身"。

**核心价值：**
*   **解耦**：前端与后端、服务与服务之间的接口契约解耦。
*   **适配**：新老系统对接时的字段映射与格式转换。
*   **安全**：敏感数据的自动脱敏与加密传输。
*   **监控**：全链路的请求日志追踪与回放。

---

## 🏗️ 技术架构

本项目采用前后端分离架构，确保高性能与易维护性。

*   **前端 (Frontend)**:
    *   **核心框架**: Vue 3 + Vite
    *   **UI 组件库**: Ant Design Vue (Smart Admin 架构)
    *   **特性**: 响应式设计，极速构建。
*   **后端 (Backend)**:
    *   **核心框架**: Spring Boot 2.7 + Java 8
    *   **持久层**: MyBatis-Plus
    *   **安全**: Spring Security + JWT
    *   **工具**: HttpClient 5 (高性能转发), Jackson (JSON 处理)
*   **数据存储**: MySQL 5.7+

---

## 📂 目录结构说明

```text
connector/
├── sa-api-service/                 # 后端工程 (Java/Spring Boot)
│   ├── sa-admin/                   # 管理端 API 源码 (启动入口)
│   │   └── src/main/java/net/lab1024/sa/admin/module/business/connector/ # ★ 连接器核心业务代码
│   └── sa-base/                    # 基础框架/通用模块
├── sa-web-ui/                      # 前端工程 (Vue 3)
│   ├── src/api/business/connector/     # 连接器前端 API 定义
│   ├── src/views/business/connector/   # 连接器页面 (路由管理/日志/测试等)
│   └── ...
└── doc/                            # 数据库脚本与文档
```

---

## 🌟 核心功能模块

### 1. 🚦 智能路由引擎 (Intelligent Routing Engine)

Connector 的心脏是一个高性能的动态代理引擎。它接管了 `/connector/proxy/**` 下的所有流量，并根据配置规则精确分发。

*   **动态代理**: 无需重启服务，路由规则即时生效。
*   **多模式匹配**: 支持 GET, POST, PUT, DELETE 等全系 HTTP 方法。
*   **环境隔离**: 支持测试模式 (`X-Connector-Test`)，让您在不影响生产环境的情况下调试新路由。

### 2. ⚗️ 数据炼金术 (Data Transformation)

这不仅仅是转发，这是数据的重塑。通过 `ConnectorTransformService`，我们提供了强大的数据处理管线：

*   **字段映射 (Field Mapping)**: 将上游的 `user_name` 变为下游的 `username`，或者将扁平结构转换为嵌套对象。
*   **数据聚合 (Aggregation)**: 
    *   *场景*: 列表数据字段补全。
    *   *能力*: 自动保留主记录字段（Deep Copy），并对数值型字段进行高精度（BigDecimal）汇总计算。
*   **安全加固 (Security)**: 内置 RSA 加解密支持，确保敏感信息在传输过程中即使被截获也无法破解。

### 3. 🛡️ 全链路可观测性 (Full Observability)

拒绝黑盒！Connector 记录了每一次呼吸。

*   **请求日志 (Request Logging)**: 详细记录请求头、请求体、响应数据、耗时以及 HTTP 状态码。
*   **配置时光机 (Config History)**: 谁在什么时候修改了路由规则？变更历史一目了然，支持配置回溯。
*   **健康大盘**: 实时监控路由的调用频率与成功率（Stats API）。

### 4. 🧩 开发者友好套件 (Developer Kit)

为了让集成测试更简单，我们内置了 Mock 服务中心。

*   **Echo 回显服务**: 发送什么返回什么，用于调试网络连通性和参数传递。
*   **Mock 订单服务**: 模拟真实的业务接口延迟和响应结构，方便前端在后端未就绪时进行开发。

---

## 🚀 快速开始 (Quick Start)

### 1. 环境准备
确保您的电脑已安装：
*   JDK 1.8+
*   Node.js 16+
*   MySQL 5.7/8.0
*   Maven 3.6+

### 2. 数据库初始化
1.  创建数据库 `connector`。
2.  执行 SQL 脚本：
    *   初始化脚本: `doc/connector_base_new.sql`

### 3. 启动后端
```bash
cd sa-api-service/sa-admin
mvn spring-boot:run
```
服务默认运行在端口 `1024`。
*   Swagger 文档: `http://localhost:1024/swagger-ui/index.html`

### 4. 启动前端
```bash
cd sa-web-ui
npm install
npm run dev
```
访问地址: `http://localhost:5173`

---

## ❓ 常见问题

**Q: 代理请求报 404？**
A: 请检查路由配置中的 `Source Path` 是否与请求路径完全匹配，并确认路由状态已设置为 `Active`（开启）。

**Q: 如何配置数据库连接？**
A: 修改 `sa-api-service/sa-admin/src/main/resources/dev/application.yaml` 中的 datasource 配置。
