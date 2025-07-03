# 书店租书管理系统 - 前端

这是一个基于 Vue 3 + Vite + Tailwind CSS 的现代化前端应用，为书店租书管理系统提供用户界面。

## 功能特性

### 客户功能
- 用户注册和登录
- 图书搜索和浏览
- 在线借阅图书
- 借阅历史查看
- 图书归还
- 账户充值
- 个人信息管理

### 管理员功能
- 管理员仪表板
- 用户管理（增删改查）
- 图书管理（增删改查）
- 借阅记录管理
- 收费标准设置
- 统计分析报表

### 技术特性
- 响应式设计，支持移动端和桌面端
- JWT 身份认证
- 角色权限控制
- 实时数据更新
- 美观的 UI 界面
- 完善的错误处理

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 现代化构建工具
- **Vue Router** - 官方路由管理器
- **Pinia** - 状态管理
- **Axios** - HTTP 客户端
- **Tailwind CSS** - 实用性优先的 CSS 框架
- **Heroicons** - 精美的 SVG 图标库

## 项目结构

```
src/
├── api/                    # API 请求配置
│   └── index.js           # API 接口定义
├── components/            # 公共组件
│   └── Layout.vue         # 布局组件
├── stores/                # 状态管理
│   ├── auth.js           # 认证状态
│   └── index.js          # Store 入口
├── styles/                # 样式文件
│   └── style.css         # 主样式文件
├── views/                 # 页面组件
│   ├── auth/             # 认证相关页面
│   │   ├── Login.vue     # 登录页面
│   │   └── Register.vue  # 注册页面
│   ├── customer/         # 客户页面
│   │   ├── Dashboard.vue     # 客户仪表板
│   │   ├── BookSearch.vue    # 图书搜索
│   │   ├── BorrowHistory.vue # 借阅历史
│   │   └── Profile.vue       # 个人信息
│   └── admin/            # 管理员页面
│       ├── Dashboard.vue        # 管理员仪表板
│       ├── UserManagement.vue   # 用户管理
│       ├── BookManagement.vue   # 图书管理
│       ├── BorrowRecords.vue    # 借阅记录
│       ├── FeeStandards.vue     # 收费标准
│       └── Statistics.vue      # 统计分析
├── App.vue               # 根组件
├── main.js              # 应用入口
└── router.js            # 路由配置
```

## 安装和运行

### 环境要求
- Node.js 16+ 
- npm 或 yarn

### 安装依赖
```bash
npm install
```

### 开发模式运行
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## 后端接口配置

前端默认连接到 `http://localhost:8080/api`，如需修改请编辑 `src/api/index.js` 文件中的 `baseURL`。

## 默认账户

系统包含以下测试账户：

**管理员账户：**
- 用户名：admin
- 密码：admin123

**客户账户：**
- 用户名：customer1
- 密码：123456

## 页面路由

### 公共路由
- `/login` - 登录页面
- `/register` - 注册页面

### 客户路由（需要客户权限）
- `/customer/dashboard` - 客户仪表板
- `/customer/books` - 图书搜索
- `/customer/history` - 借阅历史
- `/customer/profile` - 个人信息

### 管理员路由（需要管理员权限）
- `/admin/dashboard` - 管理员仪表板
- `/admin/users` - 用户管理
- `/admin/books` - 图书管理
- `/admin/records` - 借阅记录
- `/admin/fees` - 收费标准
- `/admin/statistics` - 统计分析

## 功能说明

### 客户端功能

#### 图书搜索
- 支持按书名、作者、类别搜索
- 分页显示搜索结果
- 实时显示库存状态
- 一键借阅功能

#### 借阅管理
- 设置借阅天数（1-30天）
- 自动计算费用
- 查看借阅历史
- 在线归还图书

#### 账户管理
- 个人信息修改
- 账户余额充值
- 借阅统计查看

### 管理员功能

#### 用户管理
- 用户列表查看
- 添加新用户
- 编辑用户信息
- 删除用户
- 搜索和筛选

#### 图书管理
- 图书库存管理
- 添加新图书
- 编辑图书信息
- 删除图书
- 库存预警

#### 统计分析
- 用户年龄分布
- 图书类别统计
- 借阅情况分析
- 损坏丢失统计
- 收入统计

## 样式定制

项目使用 Tailwind CSS，主要的自定义样式类：

- `.btn-primary` - 主要按钮样式
- `.btn-secondary` - 次要按钮样式
- `.btn-danger` - 危险操作按钮样式
- `.card` - 卡片容器样式
- `.form-input` - 表单输入框样式
- `.form-label` - 表单标签样式
- `.table-header` - 表格头部样式
- `.table-cell` - 表格单元格样式

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 注意事项

1. 确保后端服务已启动并运行在正确的端口
2. 首次运行前需要安装所有依赖
3. 开发模式下支持热重载
4. 生产环境请使用 HTTPS
5. 建议使用现代浏览器以获得最佳体验

## 故障排除

### 常见问题

1. **页面无法加载**
   - 检查后端服务是否启动
   - 确认 API 基础 URL 配置正确

2. **登录失败**
   - 验证用户名和密码
   - 检查网络连接
   - 查看浏览器控制台错误信息

3. **数据不显示**
   - 确认已登录
   - 检查用户权限
   - 查看网络请求是否成功

4. **样式错误**
   - 清除浏览器缓存
   - 重新构建项目
   - 检查 CSS 文件是否正确加载

如有其他问题，请查看浏览器控制台的错误信息进行调试。 