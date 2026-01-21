相关配置页面截图：
<img width="1902" height="957" alt="image" src="https://github.com/user-attachments/assets/8f2708d6-d24c-4109-82ae-b6820187e76c" />
<img width="1902" height="519" alt="image" src="https://github.com/user-attachments/assets/63cf86d9-c481-4403-8632-967c62fcd918" />
<img width="1383" height="912" alt="image" src="https://github.com/user-attachments/assets/d426126d-ec3c-47d1-b384-6c4a86d53b7d" />
<img width="1422" height="781" alt="image" src="https://github.com/user-attachments/assets/f72ae386-86dc-4845-b723-743b527ecf8c" />
<img width="1376" height="534" alt="image" src="https://github.com/user-attachments/assets/1aabcfa1-f7fc-4ea2-adf3-55932e603f67" />
<img width="1882" height="939" alt="image" src="https://github.com/user-attachments/assets/dc213398-cb63-4024-a1fa-4b53fe985c58" />
<img width="1846" height="879" alt="image" src="https://github.com/user-attachments/assets/b4b788bc-503b-4cf9-8326-e9b9b7ad7d5d" />

# Connector Project

这是一个基于 React (前端) 和 Spring Boot (后端) 的全栈应用，用于路由转发和数据映射。

## 项目架构

- **前端**: React + TypeScript + Vite + Tailwind CSS
- **后端**: Java 8 + Spring Boot 2.7 + MyBatis-Plus
- **数据库**: MySQL 5.7+

## 前置要求

在开始之前，请确保您的开发环境已安装以下工具：

1.  **Node.js**: v16+ (推荐使用 LTS 版本)
2.  **JDK**: JDK 1.8
3.  **Maven**: 3.6+
4.  **MySQL**: 5.7 或 8.0

## 快速开始

### 1. 数据库配置

1.  登录 MySQL 数据库。
2.  创建数据库 `connector`。
3.  执行初始化脚本 `backend-java/schema.sql` 建表。

```sql
CREATE DATABASE IF NOT EXISTS connector;
USE connector;
-- 复制并执行 backend-java/schema.sql 中的内容
```

4.  修改后端配置文件 `backend-java/src/main/resources/application.yml` (如有必要)，配置您的数据库用户名和密码。

### 2. 启动后端 (Java)

进入后端目录并运行 Spring Boot 应用：

```bash
cd backend-java

# 方式一：使用 Maven 运行
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

后端服务默认启动在 **8081** 端口。

*   **API 文档/测试接口**: 
    *   Mock 回显: `http://localhost:8081/api/mock/echo`
    *   Mock 延迟: `http://localhost:8081/api/mock/delay?ms=2000`

### 3. 启动前端 (React)

进入项目根目录：

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run client:dev
```

前端服务默认启动在 **5173** 端口。打开浏览器访问 `http://localhost:5173`。

> **注意**: 前端配置了代理 (`vite.config.ts`)，会将 `/api` 开头的请求转发到 `http://localhost:8081`。请确保后端服务已启动。

## 开发指南

### 目录结构

```
connector/
├── backend-java/           # Java 后端项目源码
│   ├── src/main/java       # Java 代码
│   └── src/main/resources  # 配置文件和 Mapper XML
├── src/                    # React 前端项目源码
│   ├── pages/              # 页面组件
│   ├── lib/                # API 封装和工具函数
│   └── ...
├── vite.config.ts          # Vite 配置 (包含代理设置)
└── README.md               # 项目文档
```

### 测试流程

1.  **创建路由**: 在前端页面 "路由管理" -> "新建路由"。
2.  **配置 Mock 目标**: 
    *   源路径: `/test`
    *   目标地址: `http://localhost:8081/api/mock/echo`
3.  **在线测试**: 进入 "路由测试台"，选择刚才创建的路由，发送请求。
4.  **查看结果**: 响应结果区域将显示后端 Mock 接口返回的数据；可以在 "请求日志" 页面查看转发记录。

## 常见问题

*   **端口冲突**: 如果 8081 端口被占用，请修改 `backend-java/src/main/resources/application.yml` 中的 `server.port`，并同步修改 `vite.config.ts` 中的 `target`。
*   **数据库连接失败**: 请检查 `application.yml` 中的数据库 URL、用户名和密码是否正确。
