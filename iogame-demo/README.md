# ioGame 玩家数据持久化 Demo

基于 [ioGame](https://github.com/iohao/ioGame) 框架实现的玩家数据持久化演示项目。

## 技术选型

| 技术 | 说明 |
|------|------|
| ioGame 21.34 | 网络通信与路由分发框架 |
| MySQL | 数据持久化存储 |
| HikariCP 5.1.0 | 数据库连接池 |
| JDBC | 数据库访问（轻量，无需额外 ORM） |
| Maven | 项目构建 |
| Java 21 | 运行环境 |

## 项目结构

```
src/main/java/com/demo/game/
├── DemoApplication.java     # 服务端启动入口
├── DemoLogicServer.java     # 游戏逻辑服（注册 Action）
├── PlayerAction.java        # 玩家业务 Action（路由处理）
├── GameCmd.java             # 路由常量定义
├── GameCode.java            # 业务错误码
├── proto/                   # 协议对象（Protobuf DTO）
│   ├── LoginRequest.java    #   登录请求
│   ├── NicknameRequest.java #   设置昵称请求
│   ├── GoldRequest.java     #   增加金币请求
│   └── PlayerInfo.java      #   玩家信息响应
├── db/                      # 数据库层
│   ├── DbConfig.java        #   数据库连接池配置（HikariCP）
│   └── PlayerDao.java       #   玩家 CRUD 操作
└── client/                  # 模拟客户端
    ├── DemoClient.java      #   客户端启动入口
    └── PlayerRegion.java    #   客户端命令定义
```

## 路由设计

采用 ioGame 的 `@ActionController(cmd)` + `@ActionMethod(subCmd)` 二级路由机制：

| 路由 | 说明 | 请求参数 | 返回值 |
|------|------|----------|--------|
| 1-0 | 登录/注册 | LoginRequest { username } | PlayerInfo |
| 1-1 | 设置昵称 | NicknameRequest { username, nickname } | PlayerInfo |
| 1-2 | 增加金币 | GoldRequest { username, gold } | PlayerInfo |
| 1-3 | 查询玩家信息 | LoginRequest { username } | PlayerInfo |

## 启动步骤

### 1. 环境准备

- JDK 21+
- Maven 3.6+
- MySQL 5.7+ / 8.0+

### 2. 创建数据库

连接 MySQL 后执行 `sql/init.sql`：

```sql
CREATE DATABASE IF NOT EXISTS iogame_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE iogame_demo;

CREATE TABLE IF NOT EXISTS t_player (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '玩家ID',
    username   VARCHAR(64) NOT NULL UNIQUE       COMMENT '登录账号（唯一标识）',
    nickname   VARCHAR(64) NOT NULL DEFAULT ''   COMMENT '玩家昵称',
    gold       BIGINT      NOT NULL DEFAULT 0    COMMENT '游戏金币',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP               COMMENT '创建时间',
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='玩家表';
```

### 3. 配置数据库连接

编辑 `src/main/resources/db.properties`，修改为你的 MySQL 连接信息：

```properties
db.url=jdbc:mysql://localhost:3306/iogame_demo?sslMode=DISABLED&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
db.username=root
db.password=root
```

### 4. 编译项目

```bash
mvn compile
```

### 5. 启动服务端

运行 `com.demo.game.DemoApplication` 的 main 方法。

看到类似以下输出表示启动成功：
```
数据库连接池初始化成功: jdbc:mysql://localhost:3306/iogame_demo...
```

### 6. 启动客户端

运行 `com.demo.game.client.DemoClient` 的 main 方法。

启动后控制台会显示可用命令列表，输入命令编号即可交互：

```
1-0    :    登录（输入用户名，不存在自动注册）
1-1    :    设置昵称
1-2    :    增加金币
1-3    :    查询玩家信息
```

## 验证持久化流程

### 第一次启动客户端

1. 输入 `1-0`，用户名输入 `player1` → 自动注册，返回 `gold=0, nickname=""`
2. 输入 `1-1`，用户名 `player1`，昵称 `大侠` → 返回更新后的昵称
3. 输入 `1-2`，用户名 `player1`，金币 `500` → 返回 `gold=500`
4. 输入 `1-3`，用户名 `player1` → 确认数据：`nickname=大侠, gold=500`

### 关闭客户端后重新启动

5. 关闭客户端（Ctrl+C）
6. 重新运行 `DemoClient`
7. 输入 `1-3`，用户名 `player1` → 数据依然是 `nickname=大侠, gold=500`，证明持久化成功

## 设计思路

1. **数据结构设计**：`t_player` 表使用 `username` 作为唯一业务标识（UNIQUE 约束），`id` 为自增主键。将登录账号与显示昵称分离，便于昵称修改而不影响标识。

2. **登录机制**：采用 `INSERT ... ON DUPLICATE KEY UPDATE` 实现原子性的"查找或创建"模式，首次使用某用户名即自动注册，简化了登录流程，同时避免了并发场景下的竞态条件。

3. **路由设计**：遵循 ioGame 的 `cmd + subCmd` 二级路由机制，将玩家相关操作统一归入 `cmd=1` 下，每个操作对应一个子路由。使用 `@ActionController` 和 `@ActionMethod` 注解实现路由映射。

4. **异常处理**：使用 ioGame 提供的 `MsgExceptionInfo` 错误码机制（`GameCode` 枚举），通过 `assertTrue` 进行参数校验，异常时自动向客户端返回错误码和错误信息。同时对输入长度做了限制（username/nickname 最大 32 字符），防止非法输入。

5. **持久化方案**：选择 MySQL + 原生 JDBC，使用 HikariCP 连接池管理数据库连接，避免每次请求创建新连接的性能开销。`PlayerDao` 采用单例模式集中管理数据访问逻辑，UPDATE + SELECT 操作在同一事务中执行，保证数据一致性。

6. **错误码设计**：定义了 7 个业务错误码（100-106），覆盖空值校验、长度校验、业务逻辑校验和数据库异常等场景。

## 错误码表

| 错误码 | 含义 |
|:------:|------|
| 0 | 成功 |
| 100 | 用户名不能为空 |
| 101 | 昵称不能为空 |
| 102 | 金币数量必须大于0 |
| 103 | 玩家不存在，请先登录 |
| 104 | 数据库操作失败 |
| 105 | 用户名长度不能超过32个字符 |
| 106 | 昵称长度不能超过32个字符 |
