# Connector Service (后端服务)

这是 Connector 平台的后端服务项目，基于 Spring Boot 2.7 和 JDK 1.8 开发。本项目采用分层架构，集成了多租户、动态路由、代码生成、权限管理等核心功能。

## 📚 项目简介

`online.base.connector.service` 是一个企业级的连接器管理与后台服务系统。它提供了强大的数据路由、格式转换（支持 Groovy/Jolt 脚本）、多租户隔离以及完善的后台管理功能（用户、角色、部门、菜单等）。

主要特性包括：
- **多租户支持**：基于 MyBatis-Plus 插件实现的租户数据隔离。
- **动态路由**：支持灵活的连接器路由配置，可自定义数据转换逻辑。
- **脚本处理**：集成 Groovy 脚本引擎，支持复杂的数据清洗和转换需求。
- **权限管理**：集成 Sa-Token 框架，提供细粒度的 RBAC 权限控制。
- **代码生成**：内置代码生成器，支持前后端代码（Java/Vue/SQL）一键生成。
- **API 文档**：集成 Knife4j，提供友好的在线接口文档。

## 🏗 模块结构

项目包含以下两个核心模块：

- **`connector-base`**
  - 公共基础模块，包含核心配置、工具类、通用常量。
  - 数据模型（Entity）、持久层（Mapper）以及 XML 映射文件。
  - 代码生成器模板（Velocity）。
  - 全局异常处理、拦截器、监听器等。

- **`connector-admin`**
  - 应用启动模块，包含 Spring Boot 启动类。
  - 业务逻辑层（Service）和 控制层（Controller）。
  - 不同环境的配置文件（dev, test, pre, prod）。

## 🛠 技术选型

| 技术 | 说明 | 版本 |
| --- | --- | --- |
| **Java** | 开发语言 | 1.8 |
| **Spring Boot** | 核心框架 | 2.7.18 |
| **MyBatis Plus** | ORM 框架 | 3.5.12 |
| **MySQL** | 数据库 | 8.0+ (驱动 9.3.0) |
| **Redis** | 缓存与分布式锁 | Redisson 3.25.0 |
| **Sa-Token** | 权限认证 | 1.44.0 |
| **Knife4j** | API 文档 | 4.5.0 |
| **Druid** | 数据库连接池 | 1.2.25 |
| **FastJson2** | JSON 处理 | 2.0.57 |
| **Hutool** | 工具类库 | 5.8.39 |

## 🚀 快速开始

### 1. 环境准备
- JDK 1.8+
- Maven 3.x
- MySQL 5.7 或 8.0
- Redis

### 2. 数据库初始化
1. 创建数据库 `connector`（或其他自定义名称）。
2. 执行 `doc/connector_base_new.sql`（及 `doc/init_data.sql` 如有）脚本初始化表结构和基础数据。
3. 确保数据库连接信息在配置文件中正确配置。

### 3. 配置修改
修改 `connector-admin/src/main/resources/dev/application.yaml` (或对应环境配置) 中的数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/connector?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
      username: root
      password: your_password
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

### 4. 编译与运行

**使用 Maven 编译：**
```bash
mvn clean package -Dmaven.test.skip=true
```

**运行应用：**
```bash
java -jar connector-admin/target/connector-admin-3.0.0.jar
```

启动成功后：
- 服务端口：`1024`
- 接口文档地址：`http://localhost:1024/doc.html`

## 📂 目录说明

```text
online.base.connector.service/
├── connector-admin/           # [启动模块] Admin后台与API入口
│   ├── src/main/java/         # 业务逻辑与控制器
│   ├── src/main/resources/    # 配置文件与Mapper XML
│   └── pom.xml
├── connector-base/            # [基础模块] 公共组件与底层设施
│   ├── src/main/java/         # 配置类、Entity、Utils
│   ├── src/main/resources/    # 代码生成模板、sa-base.yaml
│   └── pom.xml
├── doc/                       # 数据库脚本与文档 (通常在项目根目录或上级目录)
├── pom.xml                    # 父工程 Maven 配置
└── README.md                  # 项目说明文档
```

## 📝 开发规范
- **包命名**：`com.zhaogang.connector.*`
- **代码风格**：遵循阿里巴巴 Java 开发手册。
- **多租户**：业务表需包含 `tenant_id` 字段；系统表（如 `t_tenant`）需在 `MybatisPlusConfig` 中配置忽略多租户插件。
- **代码生成**：推荐优先使用内置代码生成器生成基础 CRUD 代码，再进行业务扩展。

## 🤝 贡献与维护
- 提交代码前请确保通过所有单元测试。
- 请遵循 Git Flow 工作流进行分支管理。
