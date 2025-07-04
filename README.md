# OCG交易系统

## 项目介绍

OCG交易系统是一个用于OCG卡牌交易的平台，提供卡牌上架、购买、用户管理等功能。

## 技术栈

- 后端：Spring Boot 2.7.2
- 数据库：MySQL 8.0
- 权限管理：Sa-Token
- ORM框架：MyBatis-Plus
- API文档：Knife4j

## 使用Docker部署

### 前提条件

- 安装Docker和Docker Compose

### 部署步骤

1. 克隆项目到本地

2. 进入项目目录

```bash
cd OCG-Trading-System
```

3. 使用Docker Compose启动服务

```bash
docker-compose up -d
```

4. 服务启动后，可以通过以下地址访问：

- 应用访问地址：http://localhost:8080
- API文档地址：http://localhost:8080/doc.html

### 默认账号

- 管理员账号：Admin
- 管理员密码：123456

## 服务说明

- **mysql**: MySQL数据库服务，存储系统数据
- **app**: OCG交易系统应用服务

## 数据持久化

- MySQL数据存储在名为`mysql-data`的Docker卷中
- 数据库初始化脚本位于`database`目录

## 停止服务

```bash
docker-compose down
```

如需同时删除数据卷：

```bash
docker-compose down -v
```


















