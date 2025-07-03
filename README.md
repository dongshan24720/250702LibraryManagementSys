# 图书租赁管理系统 - 完整项目文档

## 📖 项目概述

这是一个基于 **Spring Boot 3 + Vue 3** 的现代化书店租书管理系统，采用前后端分离架构，实现了完整的图书租借业务流程。系统支持客户端和管理员端两个子系统，涵盖用户管理、图书管理、借阅管理、收费管理和统计分析等核心功能。

### 🎯 系统特点

- **🏗️ 现代化技术栈**：Spring Boot 3 + Vue 3 最新技术
- **🔐 完善安全机制**：JWT认证、角色权限、数据安全防护
- **📊 智能业务处理**：自动库存管理、费用计算、触发器支持
- **📱 响应式设计**：支持移动端和桌面端访问
- **📥 批量数据导入**：支持Excel格式的用户和图书数据导入
- **📈 可视化统计**：多维度数据统计和图表展示

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.3.6 | 主框架 |
| Spring Security | 6.x | 安全认证 |
| MyBatis Plus | 3.5.7 | ORM框架 |
| MySQL | 5.7+ | 数据库 |
| JWT | 0.11.5 | 身份认证 |
| EasyExcel | 3.3.2 | Excel处理 |
| Maven | - | 构建工具 |
| JDK | 17 | Java版本 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.3.8 | 前端框架 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Axios | 1.6.0 | HTTP客户端 |
| Tailwind CSS | 3.3.3 | CSS框架 |
| Vite | 4.4.10 | 构建工具 |
| Chart.js | 4.4.0 | 图表组件 |

### 系统架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用      │    │   后端API       │    │   数据库        │
│   Vue 3        │◄──►│   Spring Boot   │◄──►│   MySQL 5.7    │
│   - 客户端      │    │   - REST API    │    │   - 用户表      │
│   - 管理员端    │    │   - JWT认证     │    │   - 图书表      │
│   - 响应式UI    │    │   - 业务逻辑    │    │   - 借阅表      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📁 项目结构

```
LibraryManagementSys250702/
├── src/main/java/homework/librarymanagementsys250702/
│   ├── config/                    # 配置类
│   │   ├── MyBatisPlusConfig.java # MyBatis Plus配置
│   │   └── SecurityConfig.java    # Spring Security配置
│   ├── controller/                # 控制器层
│   │   ├── AdminController.java   # 管理员控制器
│   │   ├── AuthController.java    # 认证控制器
│   │   ├── CustomerController.java # 客户控制器
│   │   ├── ExcelImportController.java # Excel导入控制器
│   │   └── FeeStandardController.java # 收费标准控制器
│   ├── dto/                       # 数据传输对象
│   ├── entity/                    # 实体类
│   ├── repository/                # 数据访问层
│   ├── service/                   # 业务逻辑层
│   ├── security/                  # 安全认证
│   ├── utils/                     # 工具类
│   └── exception/                 # 异常处理
├── src/main/resources/
│   ├── application.properties     # 配置文件
│   └── complete_database_init.sql # 完整数据库初始化脚本
├── LibraryManagementSys250702_frontend/ # 前端项目
│   ├── src/
│   │   ├── api/                  # API接口配置
│   │   ├── components/           # 公共组件
│   │   ├── stores/               # 状态管理
│   │   ├── views/                # 页面组件
│   │   │   ├── auth/            # 认证页面
│   │   │   ├── customer/        # 客户页面
│   │   │   └── admin/           # 管理员页面
│   │   ├── router.js            # 路由配置
│   │   └── main.js              # 应用入口
│   ├── package.json             # 前端依赖
│   └── tailwind.config.js       # Tailwind配置
├── 书籍导入示例.csv              # 书籍导入模板
├── 用户导入示例.csv              # 用户导入模板
└── pom.xml                      # Maven配置
```

## 🗄️ 数据库设计

### 主要数据表

#### 1. 用户表 (users)

存储系统用户信息，包括客户和管理员

```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-女，1-男',
    age INT COMMENT '年龄',
    phone VARCHAR(11) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    user_type TINYINT DEFAULT 0 COMMENT '用户类型：0-客户，1-管理员',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    status TINYINT DEFAULT 0 COMMENT '用户状态：0-正常，1-禁用'
);
```

#### 2. 图书表 (books)

存储图书信息和库存

```sql
CREATE TABLE books (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '书籍ID',
    isbn VARCHAR(20) UNIQUE COMMENT 'ISBN',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '书籍价值',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    available_quantity INT DEFAULT 0 COMMENT '可借数量'
);
```

#### 3. 借阅记录表 (borrowing_records)

记录所有借阅和归还信息

```sql
CREATE TABLE borrowing_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '借阅记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    borrow_time DATETIME NOT NULL COMMENT '借阅时间',
    due_time DATETIME NOT NULL COMMENT '应还时间',
    return_time DATETIME COMMENT '实际归还时间',
    status TINYINT DEFAULT 0 COMMENT '借阅状态：0-借阅中，1-已归还，2-超期，3-丢失，4-损坏',
    borrowing_fee DECIMAL(8,2) DEFAULT 0.00 COMMENT '借阅费用',
    overdue_days INT DEFAULT 0 COMMENT '超期天数'
);
```

#### 4. 收费标准表 (fee_standards)

定义各种收费标准

```sql
CREATE TABLE fee_standards (
    fee_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收费标准ID',
    fee_type TINYINT NOT NULL COMMENT '收费类型：1-借阅费，2-超期罚款，3-损坏赔偿，4-丢失赔偿',
    book_category VARCHAR(50) COMMENT '书籍分类',
    fee_amount DECIMAL(8,2) NOT NULL COMMENT '费用金额',
    billing_unit TINYINT DEFAULT 1 COMMENT '计费单位：1-按天，2-按本，3-按次，4-固定金额',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用'
);
```

#### 5. 罚款记录表 (fine_records)

记录各种罚款信息

```sql
CREATE TABLE fine_records (
    fine_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '罚款记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_id BIGINT COMMENT '借阅记录ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    fine_type TINYINT NOT NULL COMMENT '罚款类型：1-超期罚款，2-损坏赔偿，3-丢失赔偿',
    fine_amount DECIMAL(8,2) NOT NULL COMMENT '罚款金额',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付'
);
```

### 🔄 数据库触发器

系统使用触发器实现自动化业务处理：

#### 借阅时自动减少库存

```sql
CREATE TRIGGER tr_borrowing_insert
AFTER INSERT ON borrowing_records
FOR EACH ROW
BEGIN
    IF NEW.status = 0 THEN
        UPDATE books SET available_quantity = available_quantity - 1 
        WHERE book_id = NEW.book_id AND available_quantity > 0;
    END IF;
END;
```

#### 归还时自动恢复库存

```sql
CREATE TRIGGER tr_borrowing_update
AFTER UPDATE ON borrowing_records
FOR EACH ROW
BEGIN
    IF OLD.status = 0 AND NEW.status = 1 THEN
        UPDATE books SET available_quantity = available_quantity + 1 
        WHERE book_id = NEW.book_id;
    END IF;
END;
```

## 🚀 快速启动

### 1. 环境准备

#### 必需环境

- **JDK 17+**
- **MySQL 5.7+**
- **Node.js 16+**
- **Maven 3.6+**

### 2. 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_management;

# 2. 导入数据库脚本
SOURCE src/main/resources/complete_database_init.sql;
```

### 3. 后端启动

```bash
# 方法1：使用Maven
mvn clean install
mvn spring-boot:run

# 方法2：使用IDE
# 运行 LibraryManagementSys250702Application.java

# 方法3：使用jar包
mvn clean package -Dmaven.test.skip=true
java -jar target/LibraryManagementSys250702-0.0.1-SNAPSHOT.jar
```

### 4. 前端启动

```bash
# 进入前端目录
cd LibraryManagementSys250702_frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 生产环境构建
npm run build
```

### 5. 访问系统

- **前端地址**: <http://localhost:5173>
- **后端API**: <http://localhost:8080/api>
- **API文档**: <http://localhost:8080/api/swagger-ui.html>

## 👤 测试账户

| 用户名 | 密码 | 角色 | 余额 | 描述 |
|--------|------|------|------|------|
| admin | admin123 | 管理员 | - | 系统管理员，拥有所有权限 |
| customer1 | 123456 | 客户 | 100.00元 | 普通客户，可借阅图书 |
| customer2 | 123456 | 客户 | 50.00元 | 普通客户，可借阅图书 |

## 🎯 核心功能

### 1. 用户管理模块

#### 👥 客户功能

- **✅ 用户注册**：支持客户自主注册账号
- **✅ 安全登录**：基于JWT的无状态登录
- **✅ 个人信息管理**：查询和修改个人基本信息
- **✅ 密码管理**：安全的密码修改功能
- **✅ 账户管理**：查看余额、充值记录

#### 🛠️ 管理员功能

- **✅ 用户列表管理**：分页查询所有用户信息
- **✅ 用户信息CRUD**：完整的用户增删改查
- **✅ 账户充值**：为用户账户充值
- **✅ 用户状态管理**：启用/禁用用户账号
- **✅ Excel批量导入**：支持用户数据批量导入

### 2. 图书管理模块

#### 📖 会员功能

- **✅ 智能搜索**：支持按书名、作者、分类等多条件搜索
- **✅ 图书详情**：查看图书的详细信息和库存状态
- **✅ 实时库存**：实时显示图书可借数量

#### 📚 管理员功能

- **✅ 图书信息管理**：完整的图书CRUD操作
- **✅ 库存管理**：管理图书库存和可借数量
- **✅ 分类管理**：管理图书分类体系
- **✅ Excel批量导入**：支持图书数据批量导入
- **✅ 状态管理**：上架/下架图书

### 3. 借阅管理模块

#### 📝 完整借阅流程

1. **图书搜索** → 客户搜索要借的图书
2. **借阅申请** → 提交借阅申请，指定借阅天数
3. **费用计算** → 系统自动计算借阅费用
4. **余额验证** → 检查账户余额是否充足
5. **借阅确认** → 扣除费用，生成借阅记录
6. **库存更新** → 触发器自动减少可借数量

#### 🔄 归还处理流程

1. **归还申请** → 客户提交归还申请
2. **超期检查** → 系统自动检查是否超期
3. **费用计算** → 计算超期罚款和其他费用
4. **费用扣除** → 从账户余额扣除相关费用
5. **状态更新** → 更新借阅记录状态
6. **库存恢复** → 触发器自动增加可借数量

#### 🛡️ 管理员功能

- **✅ 记录查询**：查询所有用户的借阅记录
- **✅ 超期管理**：查看和处理超期记录
- **✅ 异常处理**：处理图书损坏、丢失等异常情况
- **✅ 费用管理**：管理各种费用和罚款

### 4. 收费管理模块

#### 💰 收费标准

- **借阅费**：固定 2元/次
- **超期罚款**：1元/天
- **损坏赔偿**：按图书价值比例
- **丢失赔偿**：按图书原价赔偿

#### 🧮 智能费用计算

```java
// 借阅费用计算示例
public BigDecimal calculateBorrowingFee(Long bookId, Integer days) {
    return new BigDecimal("2.00"); // 固定2元
}

// 超期罚款计算示例
public BigDecimal calculateOverdueFine(Integer overdueDays) {
    return new BigDecimal(overdueDays).multiply(new BigDecimal("1.00"));
}
```

### 5. 统计分析模块

#### 📊 多维度统计

- **👥 用户统计**：用户数量、年龄分布、活跃用户
- **📚 图书统计**：图书数量、分类分布、热门图书
- **📖 借阅统计**：借阅次数、借阅趋势、超期率
- **💰 财务统计**：收费统计、罚款统计、收入趋势

#### 📈 可视化图表

- **🥧 饼状图**：图书分类分布、用户年龄分布
- **📊 柱状图**：月度借阅量、热门图书排行
- **📈 折线图**：借阅趋势、收入趋势

### 6. Excel批量导入功能

#### 📥 用户批量导入

**支持字段**：用户名、真实姓名、性别、年龄、手机号、邮箱

**Excel模板格式**：

| 用户名 | 真实姓名 | 性别 | 年龄 | 手机号 | 邮箱 |
|--------|----------|------|------|--------|------|
| user001 | 张三 | 男 | 25 | 13800138001 | <zhang@test.com> |

#### 📥 图书批量导入

**支持字段**：书名、作者、ISBN、分类、出版社、出版年份、库存、价格、描述

**Excel模板格式**：

| 书名 | 作者 | ISBN | 分类 | 出版社 | 库存 | 价格 |
|------|------|------|------|--------|------|------|
| Java编程思想 | Bruce Eckel | 9787111213826 | 计算机 | 机械工业 | 15 | 89.00 |

## 🔗 API接口文档

### 基础配置

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: Bearer Token (JWT)
- **内容类型**: `application/json`

### 1. 认证接口

#### 🔑 用户登录

```http
POST /auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "userId": 1,
            "username": "admin",
            "realName": "系统管理员",
            "userType": "ADMIN",
            "balance": 0.00
        }
    }
}
```

#### 📝 用户注册

```http
POST /auth/register
Content-Type: application/json

{
    "username": "customer1",
    "password": "123456",
    "realName": "张三",
    "gender": 1,
    "age": 25,
    "phone": "13800138000",
    "email": "customer1@example.com"
}
```

### 2. 客户接口

#### 🔍 搜索图书

```http
GET /customer/books/search?keyword=小说&page=1&size=12
Authorization: Bearer {token}
```

#### 📖 借阅图书

```http
POST /customer/borrow
Authorization: Bearer {token}
Content-Type: application/json

{
    "bookId": 1,
    "borrowDays": 7
}
```

#### 🔄 归还图书

```http
POST /customer/return/{recordId}
Authorization: Bearer {token}
```

#### 📋 获取借阅记录

```http
GET /customer/borrowing-records
Authorization: Bearer {token}
```

### 3. 管理员接口

#### 👥 用户管理

```http
# 获取用户列表
GET /admin/users?current=1&size=10&search=张三

# 添加用户
POST /admin/users
{
    "username": "newuser",
    "password": "123456",
    "realName": "新用户",
    "userType": "CUSTOMER"
}

# 用户充值
POST /admin/users/{userId}/recharge?amount=100.00
```

#### 📚 图书管理

```http
# 获取图书列表
GET /admin/books?current=1&size=10&search=小说

# 添加图书
POST /admin/books
{
    "title": "红楼梦",
    "author": "曹雪芹",
    "category": "文学",
    "stockQuantity": 10,
    "price": 45.00
}
```

#### 📊 统计接口

```http
# 获取总体统计
GET /admin/statistics

# 获取图书分类统计
GET /admin/statistics/books
```

#### 📥 批量导入

```http
# 用户批量导入
POST /excel/import/users
Content-Type: multipart/form-data
file: [Excel文件]

# 图书批量导入
POST /excel/import/books
Content-Type: multipart/form-data
file: [Excel文件]
```

## 💻 前端实现详解

### 🗂️ 项目结构

```
src/
├── api/                    # API接口定义
│   └── index.js           # 统一API配置和axios拦截器
├── components/            # 公共组件
│   └── Layout.vue         # 主布局组件（侧边栏+头部）
├── stores/                # 状态管理
│   ├── auth.js           # 认证状态（用户信息、token）
│   └── index.js          # Store入口
├── views/                 # 页面组件
│   ├── auth/             # 认证页面
│   │   ├── Login.vue     # 登录页面
│   │   └── Register.vue  # 注册页面
│   ├── customer/         # 客户页面
│   │   ├── Dashboard.vue # 客户仪表板
│   │   ├── BookSearch.vue # 图书搜索
│   │   ├── BorrowHistory.vue # 借阅历史
│   │   └── Profile.vue   # 个人信息
│   └── admin/            # 管理员页面
│       ├── Dashboard.vue # 管理员仪表板
│       ├── UserManagement.vue # 用户管理
│       ├── BookManagement.vue # 图书管理
│       ├── BorrowRecords.vue # 借阅记录
│       ├── FeeStandards.vue # 收费标准
│       └── Statistics.vue # 统计分析
├── router.js             # 路由配置
└── main.js               # 应用入口
```

### 🔐 状态管理（Pinia）

#### 认证状态管理

```javascript
export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    isLoading: false
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.userType === 'ADMIN',
    isCustomer: (state) => state.user?.userType === 'CUSTOMER'
  },

  actions: {
    async login(credentials) {
      const response = await auth.login(credentials)
      this.token = response.data.token
      this.user = response.data.user
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
```

### 🛡️ 路由守卫

```javascript
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 需要认证的页面
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } 
  // 需要特定角色的页面
  else if (to.meta.role && authStore.user?.userType !== to.meta.role) {
    next('/login')
  } 
  else {
    next()
  }
})
```

### 🎨 核心页面特色

#### 📖 图书搜索页面

- **响应式布局**：自适应不同屏幕尺寸
- **实时搜索**：输入即搜索，防抖处理
- **多条件筛选**：支持书名、作者、分类筛选
- **分页加载**：优化大数据量显示性能
- **在线借阅**：直接在搜索页面完成借阅操作

#### 👥 用户管理页面

- **表格展示**：清晰的用户信息表格
- **模态框编辑**：弹窗式编辑用户信息
- **批量操作**：支持批量启用/禁用用户
- **搜索过滤**：快速查找特定用户
- **Excel导入**：拖拽上传Excel文件

#### 📊 统计分析页面

- **可视化图表**：使用Chart.js展示数据
- **实时更新**：数据实时刷新
- **多维度统计**：支持不同维度的数据分析
- **导出功能**：支持数据导出

## 🔐 安全机制详解

### 1. 🔑 认证安全

#### JWT Token机制

- **生成策略**：登录成功后生成包含用户信息的JWT
- **存储方式**：前端存储在localStorage中
- **传输方式**：通过Authorization Header传输
- **验证流程**：每次请求自动验证Token有效性
- **过期处理**：Token有效期24小时，过期自动跳转登录

#### 密码安全处理

```java
@Service
public class PasswordUtil {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 密码加密
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    // 密码验证
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

### 2. 🛡️ 权限控制

#### 角色定义与权限

- **CUSTOMER**：普通客户
  - 图书搜索和借阅
  - 个人信息管理
  - 借阅历史查看
  
- **ADMIN**：系统管理员
  - 所有客户权限
  - 用户管理
  - 图书管理
  - 系统统计
  - 收费标准管理

#### 接口权限控制

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public Result<IPage<UserDTO>> getUserList() {
    // 仅管理员可访问
}

@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
@GetMapping("/customer/profile")
public Result<UserDTO> getProfile() {
    // 客户和管理员都可访问
}
```

### 3. 🔒 数据安全

#### SQL注入防护

- **参数化查询**：使用MyBatis Plus的参数绑定
- **输入验证**：严格验证所有用户输入
- **禁用动态SQL**：避免SQL拼接攻击

#### XSS防护

- **前端转义**：Vue自动转义用户输入
- **后端编码**：输出数据HTML编码
- **CSP策略**：内容安全策略防护

#### CSRF防护

- **无状态设计**：使用JWT替代传统Session
- **Token验证**：每次请求携带和验证Token
- **SameSite Cookie**：防止跨站请求伪造

## ⚡ 特殊技术实现

### 1. 🔄 数据库触发器应用

#### 智能库存管理

系统使用MySQL触发器实现自动化库存管理，无需手动更新：

```sql
-- 借阅时自动减少库存
DELIMITER $$
CREATE TRIGGER tr_borrow_book 
AFTER INSERT ON borrowing_records
FOR EACH ROW
BEGIN
    IF NEW.status = 0 THEN -- 借阅状态
        UPDATE books 
        SET available_quantity = available_quantity - 1 
        WHERE book_id = NEW.book_id AND available_quantity > 0;
    END IF;
END$$

-- 归还时自动增加库存
CREATE TRIGGER tr_return_book
AFTER UPDATE ON borrowing_records
FOR EACH ROW
BEGIN
    IF OLD.status = 0 AND NEW.status = 1 THEN -- 从借阅变为归还
        UPDATE books 
        SET available_quantity = available_quantity + 1 
        WHERE book_id = NEW.book_id;
    END IF;
END$$
DELIMITER ;
```

### 2. 💰 智能费用计算

#### 多层次费用计算逻辑

```java
@Service
public class FeeCalculationService {
    
    // 基础借阅费计算
    public BigDecimal calculateBaseBorrowingFee(String category) {
        return feeStandardRepository.findByCategoryAndType(category, FeeType.BORROWING)
                .map(FeeStandard::getFeeAmount)
                .orElse(new BigDecimal("2.00")); // 默认2元
    }
    
    // 超期费用计算
    public BigDecimal calculateOverdueFee(int overdueDays, String category) {
        BigDecimal dailyFine = feeStandardRepository
                .findByCategoryAndType(category, FeeType.OVERDUE)
                .map(FeeStandard::getFeeAmount)
                .orElse(new BigDecimal("1.00")); // 默认1元/天
        
        return dailyFine.multiply(new BigDecimal(overdueDays));
    }
    
    // 损坏赔偿计算
    public BigDecimal calculateDamageFee(BigDecimal bookPrice) {
        BigDecimal damageRate = new BigDecimal("0.3"); // 30%赔偿率
        return bookPrice.multiply(damageRate);
    }
}
```

### 3. 📥 Excel批量处理

#### 高性能Excel导入

```java
@Service
public class ExcelImportService {
    
    // 用户批量导入
    @Transactional
    public ImportResult importUsers(MultipartFile file) {
        List<UserImportDTO> users = EasyExcel.read(file.getInputStream())
                .head(UserImportDTO.class)
                .sheet()
                .doReadSync();
        
        // 数据验证
        List<String> errors = validateUsers(users);
        if (!errors.isEmpty()) {
            return ImportResult.fail(errors);
        }
        
        // 批量插入
        List<User> entities = users.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        userService.saveBatch(entities);
        return ImportResult.success(entities.size());
    }
    
    // 数据验证逻辑
    private List<String> validateUsers(List<UserImportDTO> users) {
        List<String> errors = new ArrayList<>();
        Set<String> existingUsernames = new HashSet<>();
        
        for (int i = 0; i < users.size(); i++) {
            UserImportDTO user = users.get(i);
            String rowInfo = "第" + (i + 2) + "行"; // Excel行号从2开始
            
            // 验证必填字段
            if (StringUtils.isBlank(user.getUsername())) {
                errors.add(rowInfo + ": 用户名不能为空");
            }
            
            // 验证唯一性
            if (existingUsernames.contains(user.getUsername())) {
                errors.add(rowInfo + ": 用户名重复");
            }
            existingUsernames.add(user.getUsername());
            
            // 验证数据格式
            if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
                errors.add(rowInfo + ": 手机号格式不正确");
            }
        }
        
        return errors;
    }
}
```

### 4. 📊 实时统计查询优化

#### 高效的统计SQL

```sql
-- 用户年龄分布统计
SELECT 
    CASE 
        WHEN age BETWEEN 0 AND 18 THEN '18岁以下'
        WHEN age BETWEEN 19 AND 30 THEN '19-30岁'
        WHEN age BETWEEN 31 AND 50 THEN '31-50岁'
        ELSE '50岁以上'
    END as age_group,
    COUNT(*) as user_count
FROM users 
WHERE deleted = 0
GROUP BY age_group;

-- 热门图书排行
SELECT 
    b.title,
    b.author,
    b.category,
    COUNT(br.record_id) as borrow_count
FROM books b
LEFT JOIN borrowing_records br ON b.book_id = br.book_id
GROUP BY b.book_id
ORDER BY borrow_count DESC
LIMIT 10;

-- 借阅趋势分析
SELECT 
    DATE_FORMAT(borrow_time, '%Y-%m') as month,
    COUNT(*) as borrow_count,
    COUNT(DISTINCT user_id) as active_users
FROM borrowing_records
WHERE borrow_time >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
GROUP BY month
ORDER BY month;
```

### 5. 🔄 前端状态同步

#### 实时数据更新机制

```javascript
// 全局事件总线
export const eventBus = {
  events: {},
  
  on(event, callback) {
    if (!this.events[event]) {
      this.events[event] = [];
    }
    this.events[event].push(callback);
  },
  
  emit(event, data) {
    if (this.events[event]) {
      this.events[event].forEach(callback => callback(data));
    }
  }
};

// 借阅成功后通知相关组件更新
const borrowBook = async (bookId, days) => {
  try {
    await api.borrowBook({ bookId, borrowDays: days });
    
    // 通知图书列表更新库存
    eventBus.emit('book-borrowed', { bookId });
    
    // 通知用户余额更新
    eventBus.emit('balance-changed');
    
    // 通知借阅历史更新
    eventBus.emit('borrow-history-changed');
    
  } catch (error) {
    console.error('借阅失败:', error);
  }
};
```

## 🧪 完整功能测试

### 🔍 测试环境配置

#### 1. 环境启动检查清单

- [ ] MySQL服务运行正常（端口3306）
- [ ] 数据库初始化完成
- [ ] 后端服务启动成功（端口8080）
- [ ] 前端服务启动成功（端口5173）
- [ ] 网络连接正常

#### 2. 数据准备

- [ ] 测试用户账户存在
- [ ] 基础图书数据存在
- [ ] 收费标准配置正确
- [ ] 权限配置验证

### 📋 系统功能测试清单

#### 🔐 认证授权测试

**登录功能测试**

- [ ] 管理员登录（admin/admin123）
- [ ] 客户登录（customer1/123456）
- [ ] 错误密码登录（预期失败）
- [ ] 不存在用户登录（预期失败）

**权限控制测试**

- [ ] 客户访问管理员页面（预期被拦截）
- [ ] 未登录访问受保护页面（预期跳转登录）
- [ ] Token过期处理（预期自动跳转登录）

#### 👥 用户管理测试

**基础CRUD操作**

- [ ] 查看用户列表（分页、搜索）
- [ ] 添加新用户
- [ ] 编辑用户信息
- [ ] 删除用户
- [ ] 用户状态切换（启用/禁用）

**高级功能测试**

- [ ] 用户充值功能
- [ ] 用户搜索过滤
- [ ] Excel批量导入用户

**测试数据示例**

```
用户名: testuser001
真实姓名: 测试用户
邮箱: test@example.com
手机: 13800138000
年龄: 25
性别: 男
```

#### 📚 图书管理测试

**图书信息管理**

- [ ] 查看图书列表
- [ ] 添加新图书
- [ ] 编辑图书信息
- [ ] 删除图书
- [ ] 图书搜索功能

**库存管理测试**

- [ ] 库存数量显示
- [ ] 可借数量实时更新
- [ ] 库存不足时的处理

**测试数据示例**

```
书名: 测试图书
作者: 测试作者
ISBN: 9787000000000
分类: 测试分类
价格: 25.00
库存: 10
```

#### 📖 借阅流程测试

**完整借阅流程**

1. [ ] 客户搜索图书
2. [ ] 选择图书借阅
3. [ ] 设置借阅天数
4. [ ] 查看费用预估
5. [ ] 确认借阅（验证余额扣除）
6. [ ] 验证库存减少
7. [ ] 生成借阅记录

**归还流程测试**

1. [ ] 查看借阅记录
2. [ ] 选择归还图书
3. [ ] 验证超期计算
4. [ ] 确认归还（验证费用扣除）
5. [ ] 验证库存恢复
6. [ ] 更新记录状态

**异常情况测试**

- [ ] 余额不足借阅（预期失败）
- [ ] 库存为0借阅（预期失败）
- [ ] 重复借阅同一本书（预期失败）
- [ ] 超期归还费用计算

#### 💰 收费管理测试

**收费标准管理**

- [ ] 查看收费标准列表
- [ ] 添加新收费标准
- [ ] 编辑收费标准
- [ ] 删除收费标准
- [ ] 启用/禁用收费标准

**费用计算验证**

- [ ] 借阅费用计算正确性
- [ ] 超期费用计算正确性
- [ ] 损坏费用计算正确性
- [ ] 不同分类费用差异化

#### 📊 统计分析测试

**基础统计数据**

- [ ] 用户总数统计
- [ ] 图书总数统计
- [ ] 当前借阅数统计
- [ ] 超期图书统计

**详细分析验证**

- [ ] 用户年龄分布图
- [ ] 图书分类分布图
- [ ] 热门图书排行榜
- [ ] 借阅趋势分析
- [ ] 收入统计报表

#### 📥 批量导入测试

**用户批量导入**

1. [ ] 下载用户导入模板
2. [ ] 填写测试数据（20个用户）
3. [ ] 上传Excel文件
4. [ ] 验证导入结果
5. [ ] 检查数据完整性

**图书批量导入**

1. [ ] 下载图书导入模板
2. [ ] 填写测试数据（50本图书）
3. [ ] 上传Excel文件
4. [ ] 验证导入结果
5. [ ] 检查库存数据

#### 🌐 界面交互测试

**响应式设计**

- [ ] 手机端适配测试
- [ ] 平板端适配测试
- [ ] 桌面端显示测试

**用户体验**

- [ ] 页面加载速度
- [ ] 操作响应速度
- [ ] 错误提示友好性
- [ ] 成功提示准确性

### 🚀 性能压力测试

#### 并发测试场景

**用户登录并发**

- 100个用户同时登录
- 验证系统稳定性
- 检查数据库连接池

**图书搜索并发**

- 50个用户同时搜索
- 验证查询性能
- 检查数据库索引效果

**借阅操作并发**

- 20个用户同时借阅同一本书
- 验证库存一致性
- 检查事务处理正确性

#### 大数据量测试

**数据规模**

- 用户数据：1000+ 条记录
- 图书数据：5000+ 条记录
- 借阅记录：10000+ 条记录

**性能指标**

- [ ] 列表查询响应时间 < 2秒
- [ ] 复杂统计查询 < 5秒
- [ ] 批量导入处理 < 10秒

### 🛡️ 安全测试

#### 认证安全测试

- [ ] JWT Token过期处理
- [ ] 无效Token访问拦截
- [ ] 权限越权尝试
- [ ] 密码强度验证

#### 数据安全测试

- [ ] SQL注入攻击防护
- [ ] XSS攻击防护
- [ ] 文件上传安全验证
- [ ] 敏感信息保护

### ✅ 测试完成标准

系统测试合格需满足以下条件：

- [ ] 所有核心功能正常运行
- [ ] 用户角色权限控制正确
- [ ] 数据库触发器工作正常
- [ ] Excel导入功能稳定
- [ ] 统计分析数据准确
- [ ] 前后端交互无异常
- [ ] 界面友好操作流畅
- [ ] 性能指标满足要求
- [ ] 安全防护机制有效

## 🚀 部署指南

### 🖥️ 开发环境部署

#### 1. 环境要求

- **JDK**: 17或更高版本
- **Node.js**: 16或更高版本
- **MySQL**: 5.7或更高版本
- **Maven**: 3.6或更高版本

#### 2. 开发环境配置

```properties
# application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_management
spring.datasource.username=root
spring.datasource.password=your_password
spring.profiles.active=dev
logging.level.homework.librarymanagementsys250702=DEBUG
```

#### 3. 前端开发配置

```javascript
// vite.config.js
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 🏭 生产环境部署

#### 1. 服务器要求

- **CPU**: 2核心以上
- **内存**: 4GB以上
- **磁盘**: 50GB以上
- **操作系统**: CentOS 7+ / Ubuntu 18.04+

#### 2. 数据库部署

```bash
# 安装MySQL
sudo apt-get install mysql-server

# 创建数据库
mysql -u root -p
CREATE DATABASE library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p library_management < complete_database_init.sql
```

#### 3. 后端部署

```bash
# 编译打包
mvn clean package -Dmaven.test.skip=true

# 运行应用
nohup java -jar -Dspring.profiles.active=prod \
  target/LibraryManagementSys250702-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

#### 4. 前端部署

```bash
# 构建生产版本
npm run build

# 使用Nginx部署
sudo cp -r dist/* /var/www/html/

# Nginx配置
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 🔧 系统配置

#### JWT配置

```properties
# JWT密钥配置（生产环境请使用复杂密钥）
jwt.secret=your-256-bit-secret-key-here-should-be-very-long-and-complex
jwt.expiration=86400000
```

#### 文件上传配置

```properties
# 文件上传限制
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload.path=/var/uploads/
```

#### 数据库连接池配置

```properties
# HikariCP连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
```

### 📊 监控和维护

#### 应用监控

```properties
# Actuator监控
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

#### 日志配置

```properties
# 日志配置
logging.level.root=INFO
logging.level.homework.librarymanagementsys250702=DEBUG
logging.file.name=logs/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

#### 备份策略

```bash
# 数据库定时备份
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p library_management > /backup/db_backup_$DATE.sql

# 定时任务
0 2 * * * /path/to/backup.sh
```

## ❓ 常见问题解决

### 🔧 技术问题

#### 数据库连接问题

**问题现象**：应用启动时报数据库连接错误
**解决方案**：

1. 检查MySQL服务状态：`sudo systemctl status mysql`
2. 验证数据库配置：用户名、密码、端口
3. 检查防火墙设置：确保3306端口开放
4. 验证数据库存在：`SHOW DATABASES;`

#### 前后端通信问题

**问题现象**：前端无法获取后端数据
**解决方案**：

1. 检查后端服务状态：访问 `http://localhost:8080/api/health`
2. 验证CORS配置：检查跨域设置
3. 查看浏览器控制台：检查网络请求错误
4. 验证API路径：确认接口地址正确

#### JWT Token问题

**问题现象**：登录后仍然跳转到登录页
**解决方案**：

1. 检查Token存储：浏览器localStorage中是否有token
2. 验证Token格式：JWT格式是否正确
3. 检查Token过期：是否超过有效期
4. 验证Secret配置：前后端Secret是否一致

#### Excel导入问题

**问题现象**：Excel文件上传失败或数据导入错误
**解决方案**：

1. 检查文件格式：确保是.xlsx格式
2. 验证文件大小：不超过10MB限制
3. 检查数据格式：按照模板格式填写
4. 查看错误日志：获取具体错误信息

### 💡 业务问题

#### 库存不一致问题

**问题现象**：图书显示有库存但无法借阅
**解决方案**：

1. 检查触发器：验证数据库触发器是否正常工作
2. 手动同步库存：

```sql
UPDATE books b SET available_quantity = (
    SELECT stock_quantity - COALESCE(borrowed_count, 0)
    FROM (
        SELECT book_id, COUNT(*) as borrowed_count
        FROM borrowing_records 
        WHERE status = 0 
        GROUP BY book_id
    ) br WHERE br.book_id = b.book_id
);
```

#### 费用计算错误

**问题现象**：系统计算的费用不正确
**解决方案**：

1. 检查收费标准：验证fee_standards表配置
2. 重新计算费用：

```sql
-- 更新超期费用
UPDATE borrowing_records br 
SET overdue_days = DATEDIFF(COALESCE(return_time, NOW()), due_time)
WHERE overdue_days < DATEDIFF(COALESCE(return_time, NOW()), due_time);
```

#### 用户权限问题

**问题现象**：用户无法访问应有的功能
**解决方案**：

1. 检查用户类型：确认user_type字段正确
2. 验证角色配置：检查Spring Security配置
3. 清除缓存：重新登录获取最新权限

### 📱 界面问题

#### 页面显示异常

**问题现象**：页面布局混乱或组件显示不正常
**解决方案**：

1. 清除浏览器缓存：强制刷新页面
2. 检查CSS加载：确认Tailwind CSS正常加载
3. 验证JavaScript：查看控制台是否有JS错误
4. 更新浏览器：使用最新版本浏览器

#### 移动端适配问题

**问题现象**：手机端显示效果不佳
**解决方案**：

1. 检查响应式类：确认Tailwind响应式类使用正确
2. 测试不同设备：在多种设备上测试
3. 调整断点：修改响应式断点设置

### 🚀 性能优化

#### 查询性能优化

**数据库索引优化**：

```sql
-- 创建常用查询索引
CREATE INDEX idx_borrowing_user_status ON borrowing_records(user_id, status);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_title_author ON books(title, author);
CREATE INDEX idx_users_username ON users(username);
```

#### 前端性能优化

**路由懒加载**：

```javascript
const routes = [
  {
    path: '/admin/users',
    component: () => import('@/views/admin/UserManagement.vue')
  }
]
```

**图片优化**：

```javascript
// 使用WebP格式图片
const imageUrl = `${baseUrl}/images/book.webp`
```

## 🏆 项目特色与亮点

### 💡 技术创新

#### 1. 现代化技术栈

- **前后端分离**：Vue 3 + Spring Boot 3 最新技术组合
- **响应式设计**：Tailwind CSS 实现现代化 UI
- **组合式API**：Vue 3 Composition API 提升代码组织性
- **函数式编程**：Java 8+ Stream API 优化数据处理

#### 2. 智能化业务处理

- **触发器自动化**：数据库触发器实现库存自动管理
- **费用智能计算**：多层次费用计算策略
- **实时数据同步**：前端状态实时更新机制
- **批量处理优化**：Excel 数据批量导入优化

#### 3. 安全机制完善

- **JWT 无状态认证**：提升系统扩展性
- **角色权限控制**：细粒度权限管理
- **数据加密存储**：BCrypt 密码加密
- **安全防护机制**：SQL注入、XSS攻击防护

### 🎯 业务价值

#### 1. 完整业务闭环

- **用户全生命周期管理**：从注册到账户管理
- **图书完整流程**：从入库到借还全流程
- **财务清晰管理**：费用计算到收支统计
- **数据可视化分析**：多维度统计报表

#### 2. 用户体验优化

- **操作流程简化**：一键借阅、快速归还
- **界面友好直观**：现代化设计语言
- **响应迅速稳定**：优化查询性能
- **错误提示清晰**：友好的错误处理

#### 3. 管理效率提升

- **批量操作支持**：Excel 批量导入功能
- **自动化处理**：减少人工干预
- **实时监控分析**：数据实时统计
- **异常处理机制**：完善的异常处理流程

### 🔧 技术实现亮点

#### 1. 数据库设计优秀

- **3NF 规范化设计**：数据结构清晰合理
- **触发器业务自动化**：库存管理自动化
- **外键约束保证**：数据完整性保障
- **索引优化查询**：查询性能优化

#### 2. 后端架构清晰

- **分层架构设计**：Controller-Service-Repository
- **统一异常处理**：GlobalExceptionHandler
- **统一响应格式**：Result 统一返回
- **AOP 切面编程**：日志记录、权限控制

#### 3. 前端工程化

- **组件化开发**：可复用组件设计
- **状态管理**：Pinia 状态集中管理
- **路由守卫**：权限控制自动化
- **API 统一管理**：axios 拦截器处理

### 📈 扩展性设计

#### 1. 系统架构扩展

- **微服务改造**：模块独立部署
- **分布式存储**：数据库分库分表
- **缓存机制**：Redis 缓存优化
- **消息队列**：异步处理优化

#### 2. 功能模块扩展

- **支付集成**：支付宝、微信支付
- **通知系统**：短信、邮件通知
- **移动端APP**：原生或混合开发
- **第三方集成**：图书信息API

#### 3. 数据分析扩展

- **BI 报表系统**：更丰富的数据分析
- **机器学习**：推荐算法应用
- **实时监控**：系统性能监控
- **数据大屏**：可视化展示

## 📞 技术支持与联系

### 🛠️ 问题反馈

如遇到技术问题，请按以下步骤排查：

1. **查看日志文件**：检查 `logs/application.log`
2. **检查配置文件**：验证 `application.properties` 配置
3. **验证数据库**：确认数据库连接和数据完整性
4. **测试API接口**：使用 Postman 测试后端接口
5. **查看浏览器控制台**：检查前端错误信息


**🎉 感谢使用图书租赁管理系统！**

*这是一个完整、现代化的企业级应用，展示了 Spring Boot + Vue 3 的最佳实践。希望能为您的学习和开发提供帮助！*
