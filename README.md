# 书店租书管理系统

## 项目简介

这是一个基于Spring Boot 3的书店租书管理系统，实现了完整的图书租借业务流程，包括用户管理、书籍管理、借阅管理、收费管理和统计分析等功能。

## 技术栈

- **后端框架**: Spring Boot 3.5.3
- **数据库**: MySQL 5.7
- **ORM框架**: MyBatis Plus 3.5.5
- **安全框架**: Spring Security 6.x
- **认证方式**: JWT
- **构建工具**: Maven
- **Java版本**: JDK 17

## 主要功能

### 1. 用户管理

- **客户功能**:
  - 用户注册/登录
  - 查询和修改个人信息
  - 修改密码
  - 查看账户余额

- **管理员功能**:
  - 查询所有用户信息
  - 给用户账户充值
  - 注销用户并退款
  - 用户状态管理

### 2. 书籍管理

- **会员功能**:
  - 查询书籍信息
  - 按条件搜索书籍（精确/模糊查询）

- **管理员功能**:
  - 发布与更新书籍信息
  - 删除书籍
  - 书籍库存管理
  - Excel批量导入书籍

### 3. 借阅管理

- **会员功能**:
  - 借阅书籍
  - 归还书籍（含自动扣费）
  - 查看借阅历史

- **管理员功能**:
  - 查询借阅信息（借阅人、借阅日期等）
  - 超期管理
  - 损坏/丢失处理

### 4. 收费管理

- 制定借书收费标准
- 超期还书罚款方案
- 书籍损坏/丢失赔偿方案
- 自动费用计算和扣除

### 5. 查询模块

- 精确条件查询
- 模糊条件查询
- 复合条件查询
- 分页查询

### 6. 统计分析

- 客户年龄分布统计
- 书籍类别分布统计
- 各类书籍借阅情况统计
- 损坏或丢失情况统计

## 数据库设计

系统采用3NF规范化设计，包含以下主要数据表：

1. **users** - 用户表（客户和管理员）
2. **books** - 书籍表
3. **borrowing_records** - 借阅记录表
4. **fee_standards** - 收费标准表
5. **fine_records** - 罚款记录表

### 关系设计

- 用户与借阅记录：一对多关系
- 书籍与借阅记录：一对多关系
- 借阅记录与罚款记录：一对多关系
- 外键约束确保数据完整性
- 触发器自动处理业务逻辑

## 安装和部署

### 1. 环境要求

- JDK 17或更高版本
- MySQL 5.7或更高版本
- Maven 3.6或更高版本

### 2. 数据库初始化

```sql
-- 执行数据库SQL文件
mysql -u root -p < src/main/resources/database.sql
```

### 3. 配置文件修改

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_management
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. 编译和运行

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 5. 访问应用

应用启动后，访问地址：`http://localhost:8080/api`

## API接口

### 认证接口

- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册

### 客户接口

- `GET /customer/books/search` - 搜索书籍
- `POST /customer/borrow` - 借阅书籍
- `POST /customer/return/{recordId}` - 归还书籍
- `GET /customer/borrowing-records` - 查看借阅记录
- `POST /customer/change-password` - 修改密码

### 管理员接口

- `GET /admin/users` - 用户管理
- `POST /admin/users/{userId}/recharge` - 用户充值
- `GET /admin/books` - 书籍列表
- `POST /admin/books` - 添加书籍
- `PUT /admin/books` - 更新书籍
- `DELETE /admin/books/{bookId}` - 删除书籍
- `GET /admin/borrowing/overdue` - 超期记录
- `GET /admin/statistics/books` - 书籍统计
- `GET /admin/statistics/borrowing` - 借阅统计

## 默认账户

系统初始化后包含以下测试账户：

### 管理员账户

- 用户名：`admin`
- 密码：`admin123`

### 客户账户

- 用户名：`customer1`
- 密码：`123456`
- 余额：100元

- 用户名：`customer2`
- 密码：`123456`
- 余额：50元

## 特色功能

### 1. 自动化业务处理

- 触发器自动更新书籍库存
- 自动计算超期天数和罚款
- 定时任务检查超期记录

### 2. 安全认证

- JWT Token认证
- 角色权限控制
- 密码加密存储

### 3. 数据完整性

- 外键约束
- 数据校验
- 事务处理

### 4. 统计分析

- 多维度统计
- 数据可视化支持
- 报表导出

## 系统架构

```
├── controller/          # 控制器层
├── service/            # 业务逻辑层
├── repository/         # 数据访问层
├── entity/             # 实体类
├── dto/                # 数据传输对象
├── config/             # 配置类
├── security/           # 安全认证
├── utils/              # 工具类
└── exception/          # 异常处理
```

## 开发说明

### 代码规范

- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- RESTful API设计

### 数据库规范

- 3NF范式设计
- 统一命名规范
- 合理使用索引
- 外键约束保证数据完整性

## 扩展功能

未来可扩展的功能包括：

- 微信/支付宝支付集成
- 短信通知功能
- 邮件提醒功能
- 移动端APP
- 数据大屏展示
- 更多统计报表

## 联系方式

如有问题，请联系项目维护者。
