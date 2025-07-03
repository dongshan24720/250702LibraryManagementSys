<template>
    <div class="space-y-6">
      <!-- 页面标题 -->
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">用户管理</h1>
        <p class="text-gray-600">管理系统中的所有用户</p>
      </div>

      <!-- 操作栏 -->
      <div class="flex justify-between items-center">
        <div class="flex space-x-3">
          <button @click="showCreateModal = true" class="btn-primary flex items-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
            </svg>
            添加用户
          </button>
          <button @click="showImportModal = true" class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors flex items-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
            </svg>
            批量导入
          </button>
        </div>
        <input
          v-model="searchQuery"
          @input="handleSearch"
          type="text"
          placeholder="搜索用户..."
          class="form-input w-64"
        />
      </div>

      <!-- 用户列表 -->
      <div class="card">
        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>

        <div v-else-if="users.length === 0" class="text-center py-8 text-gray-500">
          暂无用户数据
        </div>

        <div v-else class="overflow-x-auto border border-gray-200 rounded-lg">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="table-header border-r border-gray-200">用户名</th>
                <th class="table-header border-r border-gray-200">真实姓名</th>
                <th class="table-header border-r border-gray-200">性别</th>
                <th class="table-header border-r border-gray-200">年龄</th>
                <th class="table-header border-r border-gray-200">邮箱</th>
                <th class="table-header border-r border-gray-200">电话</th>
                <th class="table-header border-r border-gray-200">类型</th>
                <th class="table-header border-r border-gray-200">余额</th>
                <th class="table-header">操作</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="user in users" :key="user.userId" class="hover:bg-gray-50 transition-colors">
                <td class="table-cell font-medium border-r border-gray-200">{{ user.username }}</td>
                <td class="table-cell border-r border-gray-200">{{ user.realName }}</td>
                <td class="table-cell border-r border-gray-200">{{ user.gender === 1 ? '男' : '女' }}</td>
                <td class="table-cell border-r border-gray-200">{{ user.age }}</td>
                <td class="table-cell border-r border-gray-200">{{ user.email }}</td>
                <td class="table-cell border-r border-gray-200">{{ user.phone }}</td>
                <td class="table-cell border-r border-gray-200">
                  <span :class="user.userType === 'ADMIN' ? 'px-2 py-1 text-xs font-medium bg-purple-100 text-purple-800 rounded-full' : 'px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full'">
                    {{ user.userType === 'ADMIN' ? '管理员' : '客户' }}
                  </span>
                </td>
                <td class="table-cell border-r border-gray-200">
                  <div class="flex items-center space-x-2">
                    <span class="font-medium">¥{{ user.balance || 0 }}</span>
                    <button @click="editBalance(user)" class="text-green-600 hover:text-green-700 text-xs font-medium px-2 py-1 rounded hover:bg-green-50 transition-colors">
                      修改
                    </button>
                  </div>
                </td>
                <td class="table-cell">
                  <div class="flex space-x-2">
                    <button @click="editUser(user)" class="text-blue-600 hover:text-blue-700 text-sm font-medium px-2 py-1 rounded hover:bg-blue-50 transition-colors">
                      编辑
                    </button>
                    <button @click="deleteUser(user)" class="text-red-600 hover:text-red-700 text-sm font-medium px-2 py-1 rounded hover:bg-red-50 transition-colors">
                      删除
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 分页组件 -->
        <div v-if="totalPages > 1" class="mt-6 flex items-center justify-between">
          <div class="text-sm text-gray-700">
            共 {{ total }} 条记录，每页 {{ pageSize }} 条
          </div>
          <nav class="flex items-center space-x-2">
            <button
              @click="goToPage(currentPage - 1)"
              :disabled="currentPage <= 1"
              class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              上一页
            </button>
            
            <div class="flex space-x-1">
              <template v-for="page in getVisiblePages()" :key="page">
                <button
                  v-if="page !== '...'"
                  @click="goToPage(page)"
                  :class="[
                    'px-3 py-2 text-sm font-medium rounded-md transition-colors',
                    page === currentPage 
                      ? 'bg-blue-600 text-white border border-blue-600' 
                      : 'text-gray-700 bg-white border border-gray-300 hover:bg-gray-50'
                  ]"
                >
                  {{ page }}
                </button>
                <span v-else class="px-3 py-2 text-sm text-gray-500">...</span>
              </template>
            </div>
            
            <button
              @click="goToPage(currentPage + 1)"
              :disabled="currentPage >= totalPages"
              class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              下一页
            </button>
          </nav>
        </div>
      </div>
    </div>

    <!-- 创建/编辑用户弹窗 -->
    <div
      v-if="showCreateModal || showEditModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click="closeModal"
    >
      <div
        class="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <!-- 表单头部 -->
        <div class="bg-gradient-to-r from-blue-600 to-blue-700 px-6 py-4 rounded-t-xl">
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-bold text-white flex items-center">
              <svg class="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              {{ showCreateModal ? '添加新用户' : '编辑用户信息' }}
            </h3>
            <button @click="closeModal" class="text-white hover:text-gray-200 transition-colors">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>

        <!-- 表单内容 -->
        <div class="p-6">
          <form @submit.prevent="saveUser" class="space-y-6">
            <!-- 基本信息区域 -->
            <div class="bg-gray-50 rounded-lg p-4">
              <h4 class="text-lg font-semibold text-gray-800 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                基本信息
              </h4>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>用户名
                  </label>
                  <input
                    v-model="userForm.username"
                    type="text"
                    class="form-input border-2 focus:border-blue-500 transition-colors"
                    placeholder="请输入用户名"
                    required
                  />
                </div>
                
                <div>
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>真实姓名
                  </label>
                  <input
                    v-model="userForm.realName"
                    type="text"
                    class="form-input border-2 focus:border-blue-500 transition-colors"
                    placeholder="请输入真实姓名"
                    required
                  />
                </div>
                
                <div v-if="showCreateModal">
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>密码
                  </label>
                  <input
                    v-model="userForm.password"
                    type="password"
                    class="form-input border-2 focus:border-blue-500 transition-colors"
                    placeholder="请输入密码"
                    required
                  />
                </div>
                
                <div>
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>性别
                  </label>
                  <select v-model="userForm.gender" class="form-input border-2 focus:border-blue-500 transition-colors" required>
                    <option value="">请选择性别</option>
                    <option :value="0">女</option>
                    <option :value="1">男</option>
                  </select>
                </div>
                
                <div>
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>年龄
                  </label>
                  <input
                    v-model="userForm.age"
                    type="number"
                    class="form-input border-2 focus:border-blue-500 transition-colors"
                    placeholder="请输入年龄"
                    required
                    min="1"
                    max="150"
                  />
                </div>
              </div>
            </div>

            <!-- 联系信息区域 -->
            <div class="bg-gray-50 rounded-lg p-4">
              <h4 class="text-lg font-semibold text-gray-800 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                </svg>
                联系信息
              </h4>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>邮箱
                  </label>
                  <input
                    v-model="userForm.email"
                    type="email"
                    class="form-input border-2 focus:border-blue-500 transition-colors"
                    placeholder="请输入邮箱地址"
                    required
                  />
                </div>
                
                <div>
                  <label class="form-label flex items-center">
                    <span class="text-red-500 mr-1">*</span>电话
                  </label>
                  <input
                    v-model="userForm.phone"
                    type="tel"
                    class="form-input border-2 focus:border-blue-500 transition-colors"
                    placeholder="请输入手机号码"
                    required
                  />
                </div>
              </div>
            </div>

            <!-- 权限信息区域 -->
            <div class="bg-gray-50 rounded-lg p-4">
              <h4 class="text-lg font-semibold text-gray-800 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                </svg>
                权限设置
              </h4>
              
              <div>
                <label class="form-label flex items-center">
                  <span class="text-red-500 mr-1">*</span>用户类型
                </label>
                <select v-model="userForm.userType" class="form-input border-2 focus:border-blue-500 transition-colors" required>
                  <option value="CUSTOMER">客户</option>
                  <option value="ADMIN">管理员</option>
                </select>
                <p class="text-sm text-gray-500 mt-1">
                  管理员可以管理系统所有功能，客户只能进行借阅操作
                </p>
              </div>
            </div>
            
            <!-- 按钮区域 -->
            <div class="flex space-x-4 pt-6 border-t">
              <button 
                type="submit" 
                class="flex-1 bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white font-medium py-3 px-6 rounded-lg transition-all duration-200 transform hover:scale-105 shadow-lg flex items-center justify-center" 
                :disabled="saving"
              >
                <svg v-if="!saving" class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
                <svg v-else class="animate-spin w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                </svg>
                {{ saving ? '保存中...' : '保存用户' }}
              </button>
              <button 
                type="button" 
                @click="closeModal" 
                class="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-3 px-6 rounded-lg transition-all duration-200 flex items-center justify-center"
              >
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
                取消
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 导入用户弹窗 -->
    <div
      v-if="showImportModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click="closeImportModal"
    >
      <div
        class="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <!-- 导入弹窗头部 -->
        <div class="bg-gradient-to-r from-green-600 to-green-700 px-6 py-4 rounded-t-xl">
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-bold text-white flex items-center">
              <svg class="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
              </svg>
              批量导入用户
            </h3>
            <button @click="closeImportModal" class="text-white hover:text-gray-200 transition-colors">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>

        <!-- 导入弹窗内容 -->
        <div class="p-6">
          <!-- 支持格式说明 -->
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <h4 class="text-lg font-semibold text-blue-800 mb-2">📋 支持的文件格式</h4>
            <div class="text-sm text-blue-700 space-y-1">
              <p>• <strong>Excel格式：</strong> .xlsx 文件</p>
              <p>• <strong>CSV格式：</strong> .csv 文件 (推荐使用UTF-8编码)</p>
            </div>
          </div>

          <!-- 模板下载 -->
          <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
            <h4 class="text-lg font-semibold text-yellow-800 mb-3">📥 模板下载</h4>
            <p class="text-sm text-yellow-700 mb-3">请先下载模板文件，按照模板格式填写用户信息后再上传：</p>
            <div class="flex space-x-3">
              <button @click="downloadTemplate('excel')" class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                📊 下载Excel模板
              </button>
              <button @click="downloadTemplate('csv')" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                📄 下载CSV模板
              </button>
            </div>
          </div>

          <!-- 文件上传区域 -->
          <div class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
            <input
              ref="fileInput"
              type="file"
              accept=".xlsx,.csv"
              @change="handleFileSelect"
              class="hidden"
            />
            
            <div v-if="!selectedFile" class="space-y-4">
              <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <div>
                <button @click="$refs.fileInput.click()" class="text-blue-600 hover:text-blue-700 font-medium">
                  点击选择文件
                </button>
                <span class="text-gray-500"> 或拖拽文件到此处</span>
              </div>
              <p class="text-sm text-gray-500">支持 .xlsx 和 .csv 格式，文件大小不超过10MB</p>
            </div>

            <div v-else class="space-y-4">
              <svg class="mx-auto h-12 w-12 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              <div>
                <p class="text-lg font-medium text-gray-900">{{ selectedFile.name }}</p>
                <p class="text-sm text-gray-500">{{ formatFileSize(selectedFile.size) }}</p>
              </div>
              <button @click="clearFile" class="text-red-600 hover:text-red-700 text-sm font-medium">
                重新选择文件
              </button>
            </div>
          </div>

          <!-- 导入结果 -->
          <div v-if="importResult" class="mt-6 p-4 rounded-lg" :class="importResult.success ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'">
            <h4 class="font-semibold mb-2" :class="importResult.success ? 'text-green-800' : 'text-red-800'">
              {{ importResult.success ? '✅ 导入成功' : '❌ 导入失败' }}
            </h4>
            <pre class="text-sm whitespace-pre-wrap" :class="importResult.success ? 'text-green-700' : 'text-red-700'">{{ importResult.message }}</pre>
          </div>

          <!-- 按钮组 -->
          <div class="flex space-x-3 mt-6">
            <button
              @click="importUsers"
              :disabled="!selectedFile || importing"
              class="flex-1 bg-green-600 hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-medium py-3 px-6 rounded-lg transition-all duration-200 flex items-center justify-center"
            >
              <svg v-if="!importing" class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
              </svg>
              <svg v-else class="animate-spin w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
              </svg>
              {{ importing ? '导入中...' : '开始导入' }}
            </button>
            <button
              @click="closeImportModal"
              class="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-3 px-6 rounded-lg transition-all duration-200 flex items-center justify-center"
            >
              <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 余额编辑弹窗 -->
    <div
      v-if="showBalanceModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click="closeBalanceModal"
    >
      <div
        class="bg-white rounded-xl shadow-2xl max-w-md w-full"
        @click.stop
      >
        <!-- 余额编辑弹窗头部 -->
        <div class="bg-gradient-to-r from-green-600 to-green-700 px-6 py-4 rounded-t-xl">
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-bold text-white flex items-center">
              <svg class="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
              </svg>
              修改用户余额
            </h3>
            <button @click="closeBalanceModal" class="text-white hover:text-gray-200 transition-colors">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>

        <!-- 余额编辑弹窗内容 -->
        <div class="p-6">
          <div class="mb-4">
            <p class="text-sm text-gray-600 mb-2">用户信息</p>
            <div class="bg-gray-50 p-3 rounded-lg">
              <p class="font-medium">{{ editingBalanceUser?.username }}</p>
              <p class="text-sm text-gray-600">{{ editingBalanceUser?.realName }}</p>
            </div>
          </div>

          <form @submit.prevent="saveBalance" class="space-y-4">
            <div>
              <label class="form-label flex items-center">
                <span class="text-red-500 mr-1">*</span>当前余额
              </label>
              <div class="bg-gray-50 p-3 rounded-lg">
                <p class="text-lg font-bold text-blue-600">¥{{ editingBalanceUser?.balance || 0 }}</p>
              </div>
            </div>
            
            <div>
              <label class="form-label flex items-center">
                <span class="text-red-500 mr-1">*</span>新余额
              </label>
              <input
                v-model="newBalance"
                type="number"
                step="0.01"
                min="0"
                class="form-input border-2 focus:border-green-500 transition-colors"
                placeholder="请输入新的余额"
                required
              />
            </div>
            
            <!-- 按钮区域 -->
            <div class="flex space-x-3 pt-4 border-t">
              <button 
                type="submit" 
                class="flex-1 bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 text-white font-medium py-3 px-6 rounded-lg transition-all duration-200 transform hover:scale-105 shadow-lg flex items-center justify-center" 
                :disabled="updatingBalance"
              >
                <svg v-if="!updatingBalance" class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
                <svg v-else class="animate-spin w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                </svg>
                {{ updatingBalance ? '更新中...' : '确认更新' }}
              </button>
              <button 
                type="button" 
                @click="closeBalanceModal" 
                class="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-3 px-6 rounded-lg transition-all duration-200 flex items-center justify-center"
              >
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
                取消
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '@/api'


const users = ref([])
const loading = ref(false)
const saving = ref(false)
const searchQuery = ref('')

// 分页相关变量
const totalPages = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showCreateModal = ref(false)
const showEditModal = ref(false)
const showImportModal = ref(false)
const editingUserId = ref(null)

// 余额编辑相关变量
const showBalanceModal = ref(false)
const editingBalanceUser = ref(null)
const newBalance = ref('')
const updatingBalance = ref(false)

// 导入相关变量
const selectedFile = ref(null)
const importing = ref(false)
const importResult = ref(null)

const userForm = ref({
  username: '',
  realName: '',
  password: '',
  gender: '',
  age: '',
  email: '',
  phone: '',
  userType: 'CUSTOMER'
})

onMounted(async () => {
  await loadUsers()
})

const loadUsers = async (page = 1) => {
  loading.value = true
  try {
    const params = { 
      current: page,
      size: pageSize.value,
      search: searchQuery.value 
    }
    const response = await admin.getUsers(params)
    if (response.data) {
      users.value = response.data.records || []
      total.value = response.data.total || 0
      totalPages.value = Math.ceil(total.value / pageSize.value)
      currentPage.value = page
    } else {
      users.value = []
      total.value = 0
      totalPages.value = 0
    }
  } catch (error) {
    console.error('Failed to load users:', error)
    users.value = []
    total.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

const editUser = (user) => {
  userForm.value = { ...user }
  editingUserId.value = user.userId
  showEditModal.value = true
}

const deleteUser = async (user) => {
  if (confirm(`确定要删除用户 "${user.username}" 吗？`)) {
    try {
      await admin.deleteUser(user.userId)
      await loadUsers(currentPage.value)
      alert('用户删除成功！')
    } catch (error) {
      alert('删除失败：' + (error.response?.data?.message || '请重试'))
    }
  }
}

const saveUser = async () => {
  saving.value = true
  try {
    const data = { 
      ...userForm.value, 
      age: parseInt(userForm.value.age),
      gender: parseInt(userForm.value.gender)
    }
    
    if (showCreateModal.value) {
      await admin.createUser(data)
      alert('用户创建成功！')
    } else {
      await admin.updateUser(editingUserId.value, data)
      alert('用户更新成功！')
    }
    
    closeModal()
    await loadUsers(currentPage.value)
  } catch (error) {
    alert('保存失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    saving.value = false
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  userForm.value = {
    username: '',
    realName: '',
    password: '',
    gender: '',
    age: '',
    email: '',
    phone: '',
    userType: 'CUSTOMER'
  }
  editingUserId.value = null
}

// 分页函数
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadUsers(page)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers(1)
}

// 获取可见的页码
const getVisiblePages = () => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    pages.push(1)
    
    if (current <= 4) {
      for (let i = 2; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }
  
  return pages
}

// 导入相关方法
const closeImportModal = () => {
  showImportModal.value = false
  selectedFile.value = null
  importResult.value = null
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    const maxSize = 10 * 1024 * 1024 // 10MB
    if (file.size > maxSize) {
      alert('文件大小不能超过10MB')
      return
    }
    selectedFile.value = file
    importResult.value = null
  }
}

const clearFile = () => {
  selectedFile.value = null
  importResult.value = null
  if (document.querySelector('input[type="file"]')) {
    document.querySelector('input[type="file"]').value = ''
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const downloadTemplate = (type) => {
  if (type === 'excel') {
    // 下载Excel模板
    const link = document.createElement('a')
    link.href = '/用户导入模板.xlsx'
    link.download = '用户导入模板.xlsx'
    link.click()
  } else if (type === 'csv') {
    // 创建CSV模板内容
    const csvContent = '用户名,真实姓名,性别,年龄,手机号,邮箱\nuser001,张三,男,25,13800138001,zhang@test.com\nuser002,李四,女,23,13800138002,li@test.com'
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', '用户导入模板.csv')
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

const importUsers = async () => {
  if (!selectedFile.value) {
    alert('请先选择要导入的文件')
    return
  }

  importing.value = true
  importResult.value = null

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    
    const response = await admin.importUsers(formData)
    
    if (response.code === 200) {
      importResult.value = {
        success: true,
        message: response.data
      }
      // 导入成功后刷新用户列表
      await loadUsers(currentPage.value)
    } else {
      importResult.value = {
        success: false,
        message: response.message || '导入失败'
      }
    }
  } catch (error) {
    console.error('Import error:', error)
    importResult.value = {
      success: false,
      message: error.response?.data?.message || '导入失败，请检查文件格式是否正确'
    }
  } finally {
    importing.value = false
  }
}

// 余额编辑相关方法
const editBalance = (user) => {
  editingBalanceUser.value = { ...user }
  newBalance.value = user.balance || 0
  showBalanceModal.value = true
}

const closeBalanceModal = () => {
  showBalanceModal.value = false
  editingBalanceUser.value = null
  newBalance.value = ''
}

const saveBalance = async () => {
  if (!editingBalanceUser.value) return
  
  const balance = parseFloat(newBalance.value)
  if (isNaN(balance) || balance < 0) {
    alert('请输入有效的余额金额')
    return
  }

  updatingBalance.value = true
  try {
    await admin.updateUserBalance(editingBalanceUser.value.userId, balance)
    alert('余额更新成功！')
    closeBalanceModal()
    await loadUsers(currentPage.value)
  } catch (error) {
    alert('更新失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    updatingBalance.value = false
  }
}
</script> 