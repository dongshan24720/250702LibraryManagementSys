<template>
    <div class="space-y-6">
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">图书管理</h1>
        <p class="text-gray-600">管理图书库存和信息</p>
      </div>

      <div class="flex justify-between items-center">
        <div class="flex space-x-3">
          <button @click="showCreateModal = true" class="btn-primary flex items-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
            </svg>
            添加图书
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
          placeholder="搜索图书..."
          class="form-input w-64"
        />
      </div>

      <div class="card">
        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>

        <div v-else-if="books.length === 0" class="text-center py-8 text-gray-500">
          暂无图书数据
        </div>

        <div v-else class="overflow-x-auto border border-gray-200 rounded-lg">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="table-header border-r border-gray-200">ISBN</th>
                <th class="table-header border-r border-gray-200">书名</th>
                <th class="table-header border-r border-gray-200">作者</th>
                <th class="table-header border-r border-gray-200">类别</th>
                <th class="table-header border-r border-gray-200">出版社</th>
                <th class="table-header border-r border-gray-200">库存</th>
                <th class="table-header border-r border-gray-200">价格</th>
                <th class="table-header">操作</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="book in books" :key="book.bookId" class="hover:bg-gray-50 transition-colors">
                <td class="table-cell text-sm text-gray-600 border-r border-gray-200">{{ book.isbn }}</td>
                <td class="table-cell font-medium border-r border-gray-200">{{ book.title }}</td>
                <td class="table-cell border-r border-gray-200">{{ book.author }}</td>
                <td class="table-cell border-r border-gray-200">{{ book.category }}</td>
                <td class="table-cell text-sm text-gray-600 border-r border-gray-200">{{ book.publisher }}</td>
                <td class="table-cell border-r border-gray-200">
                  <span :class="book.stockQuantity > 0 ? 'text-green-600' : 'text-red-600'">
                    {{ book.stockQuantity }}
                  </span>
                </td>
                <td class="table-cell border-r border-gray-200">¥{{ book.price }}</td>
                <td class="table-cell">
                  <div class="flex space-x-2">
                    <button @click="editBook(book)" class="text-blue-600 hover:text-blue-700 text-sm font-medium px-2 py-1 rounded hover:bg-blue-50 transition-colors">
                      编辑
                    </button>
                    <button @click="deleteBook(book)" class="text-red-600 hover:text-red-700 text-sm font-medium px-2 py-1 rounded hover:bg-red-50 transition-colors">
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
            
            <!-- 页码按钮 -->
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

    <!-- 图书表单弹窗 -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click="closeModal">
      <div class="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto" @click.stop>
        <!-- 弹窗头部 -->
        <div class="bg-gradient-to-r from-blue-600 to-blue-700 px-8 py-6 rounded-t-xl">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="w-8 h-8 bg-white bg-opacity-20 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
                </svg>
              </div>
              <div>
                <h3 class="text-xl font-bold text-white">
                  {{ showCreateModal ? '添加图书' : '编辑图书' }}
                </h3>
                <p class="text-blue-100 text-sm mt-1">
                  {{ showCreateModal ? '填写以下信息来添加新图书' : '修改图书信息' }}
                </p>
              </div>
            </div>
            <button @click="closeModal" class="text-white hover:bg-white hover:bg-opacity-20 rounded-lg p-2 transition-colors">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>

        <!-- 表单内容 -->
        <div class="px-8 py-6">
          <form @submit.prevent="saveBook" class="space-y-6">
            <!-- 基本信息 -->
            <div class="bg-gray-50 rounded-lg p-6">
              <h4 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 text-blue-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                基本信息
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    ISBN <span class="text-red-500">*</span>
                  </label>
                                     <input 
                     v-model="bookForm.isbn" 
                     type="text" 
                     class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                     required 
                     placeholder="输入13位数字，系统会自动格式化"
                     maxlength="17"
                     @input="handleISBNInput"
                     title="请输入有效的ISBN-13格式，如：978-7-111-40815-5"
                   />
                                                           <div class="mt-2">
                       <p class="text-xs text-gray-500 mb-1 isbn-helper">13位ISBN号码，输入数字即可自动格式化</p>
                       <div class="flex flex-wrap gap-2 text-xs">
                         <span class="inline-flex items-center px-2 py-1 rounded-full bg-green-100 text-green-800 cursor-pointer hover:bg-green-200 transition-colors" 
                               @click="bookForm.isbn = '9781234567890'; handleISBNInput({value: '9781234567890'})">
                           📚 9781234567890
                         </span>
                         <span class="inline-flex items-center px-2 py-1 rounded-full bg-blue-100 text-blue-800 cursor-pointer hover:bg-blue-200 transition-colors"
                               @click="bookForm.isbn = '9789876543210'; handleISBNInput({value: '9789876543210'})">
                           💻 9789876543210
                         </span>
                         <span class="inline-flex items-center px-2 py-1 rounded-full bg-purple-100 text-purple-800 cursor-pointer hover:bg-purple-200 transition-colors"
                               @click="bookForm.isbn = '9780123456789'; handleISBNInput({value: '9780123456789'})">
                           🔧 9780123456789
                         </span>
                       </div>
                       <div class="flex items-center justify-between mt-1">
                         <p class="text-xs text-gray-400">💡 点击上方示例快速填入，或直接输入13位数字</p>
                         <button 
                           type="button"
                           @click="generateRandomISBN"
                           class="text-xs bg-gray-200 hover:bg-gray-300 text-gray-700 px-2 py-1 rounded transition-colors"
                         >
                           🎲 随机生成
                         </button>
                       </div>
                     </div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    书名 <span class="text-red-500">*</span>
                  </label>
                  <input 
                    v-model="bookForm.title" 
                    type="text" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    required 
                    placeholder="请输入图书名称"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    作者 <span class="text-red-500">*</span>
                  </label>
                  <input 
                    v-model="bookForm.author" 
                    type="text" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    required 
                    placeholder="请输入作者姓名"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    类别 <span class="text-red-500">*</span>
                  </label>
                  <select v-model="bookForm.category" class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors" required>
                    <option value="">请选择类别</option>
                    <option value="计算机">计算机</option>
                    <option value="小说">小说</option>
                    <option value="文学">文学</option>
                    <option value="历史">历史</option>
                    <option value="科学">科学</option>
                    <option value="艺术">艺术</option>
                    <option value="哲学">哲学</option>
                    <option value="教育">教育</option>
                    <option value="其他">其他</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- 出版信息 -->
            <div class="bg-gray-50 rounded-lg p-6">
              <h4 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 text-green-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
                出版信息
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    出版社 <span class="text-red-500">*</span>
                  </label>
                  <input 
                    v-model="bookForm.publisher" 
                    type="text" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    required 
                    placeholder="请输入出版社名称"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">出版年份</label>
                  <input 
                    v-model="bookForm.publishYear" 
                    type="number" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    min="1900" 
                    max="2100" 
                    placeholder="如：2020"
                  />
                </div>
              </div>
            </div>

            <!-- 库存和价格 -->
            <div class="bg-gray-50 rounded-lg p-6">
              <h4 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 text-yellow-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
                </svg>
                库存与价格
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    库存数量 <span class="text-red-500">*</span>
                  </label>
                  <input 
                    v-model="bookForm.stockQuantity" 
                    type="number" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    required 
                    min="0" 
                    placeholder="请输入库存数量"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    价格（元） <span class="text-red-500">*</span>
                  </label>
                  <input 
                    v-model="bookForm.price" 
                    type="number" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    required 
                    step="0.01" 
                    min="0" 
                    placeholder="如：89.00"
                  />
                </div>
              </div>
            </div>

            <!-- 描述 -->
            <div class="bg-gray-50 rounded-lg p-6">
              <h4 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 text-purple-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                图书描述
              </h4>
              <div>
                <textarea 
                  v-model="bookForm.description" 
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors resize-none"
                  rows="4" 
                  placeholder="请输入图书简介、内容概述等（可选）"
                ></textarea>
              </div>
            </div>

            <!-- 按钮区域 -->
            <div class="flex space-x-4 pt-6 border-t border-gray-200">
              <button 
                type="submit" 
                class="flex-1 bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
                :disabled="saving"
              >
                <span v-if="saving" class="flex items-center justify-center">
                  <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  保存中...
                </span>
                <span v-else class="flex items-center justify-center">
                  <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                  </svg>
                  {{ showCreateModal ? '添加图书' : '保存修改' }}
                </span>
              </button>
              <button 
                type="button" 
                @click="closeModal" 
                class="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
              >
                <span class="flex items-center justify-center">
                  <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                  取消
                </span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 导入图书弹窗 -->
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
              批量导入图书
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
            <p class="text-sm text-yellow-700 mb-3">请先下载模板文件，按照模板格式填写图书信息后再上传：</p>
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
              @click="importBooks"
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '@/api'


const books = ref([])
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
const editingBookId = ref(null)

// 导入相关变量
const selectedFile = ref(null)
const importing = ref(false)
const importResult = ref(null)

const bookForm = ref({
  isbn: '',
  title: '',
  author: '',
  category: '',
  publisher: '',
  publishYear: null,
  description: '',
  stockQuantity: 0,
  price: 0
})

onMounted(async () => {
  await loadBooks()
})

const loadBooks = async (page = 1) => {
  loading.value = true
  try {
    const params = { 
      current: page,
      size: pageSize.value,
      search: searchQuery.value 
    }
    const response = await admin.getBooks(params)
    if (response.data) {
      books.value = response.data.records || []
      total.value = response.data.total || 0
      totalPages.value = Math.ceil(total.value / pageSize.value)
      currentPage.value = page
    } else {
      books.value = []
      total.value = 0
      totalPages.value = 0
    }
  } catch (error) {
    console.error('Failed to load books:', error)
    books.value = []
    total.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

const editBook = (book) => {
  bookForm.value = { ...book }
  editingBookId.value = book.bookId
  showEditModal.value = true
}

const deleteBook = async (book) => {
  if (confirm(`确定要删除图书 "${book.title}" 吗？`)) {
    try {
      await admin.deleteBook(book.bookId)
      await loadBooks(currentPage.value)
      alert('图书删除成功！')
    } catch (error) {
      alert('删除失败：' + (error.response?.data?.message || '请重试'))
    }
  }
}

const saveBook = async () => {
  saving.value = true
  try {
    // 检查ISBN格式和重复性
    const cleanedISBN = bookForm.value.isbn.replace(/[^0-9X]/g, '')
    if (cleanedISBN.length !== 13) {
      alert('请输入有效的13位ISBN号码')
      return
    }
    
    if (!validateISBN(bookForm.value.isbn)) {
      alert('ISBN号码格式不正确。请确保：\n1. 输入13位数字\n2. 以978或979开头\n3. 只包含数字（最后一位可以是X）')
      return
    }
    
    // 检查ISBN是否重复（仅在新增时检查）
    if (showCreateModal.value) {
      const isExists = await checkISBNExists(bookForm.value.isbn)
      if (isExists) {
        alert('该ISBN号码已存在，请使用其他ISBN')
        return
      }
    }
    
    const data = {
      ...bookForm.value,
      stockQuantity: parseInt(bookForm.value.stockQuantity),
      availableQuantity: parseInt(bookForm.value.stockQuantity),
      price: parseFloat(bookForm.value.price),
      publishYear: bookForm.value.publishYear ? parseInt(bookForm.value.publishYear) : null
    }
    
    if (showCreateModal.value) {
      await admin.createBook(data)
      alert('图书创建成功！')
    } else {
      await admin.updateBook(editingBookId.value, data)
      alert('图书更新成功！')
    }
    
    closeModal()
    await loadBooks(currentPage.value)
  } catch (error) {
    alert('保存失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    saving.value = false
  }
}

// ISBN格式化函数
const formatISBN = (value) => {
  // 移除所有非数字和非X字符
  const cleaned = value.replace(/[^0-9X]/g, '').toUpperCase()
  
  // 如果是13位数字，自动添加连字符
  if (cleaned.length >= 3) {
    let formatted = cleaned.slice(0, 3)
    if (cleaned.length > 3) formatted += '-' + cleaned.slice(3, 4)
    if (cleaned.length > 4) formatted += '-' + cleaned.slice(4, 7)
    if (cleaned.length > 7) formatted += '-' + cleaned.slice(7, 12)
    if (cleaned.length > 12) formatted += '-' + cleaned.slice(12, 13)
    return formatted
  }
  return cleaned
}

// ISBN验证函数 - 简化版本，只检查基本格式
const validateISBN = (isbn) => {
  const cleaned = isbn.replace(/[^0-9X]/g, '').toUpperCase()
  
  // 检查长度是否为13位
  if (cleaned.length !== 13) {
    return false
  }
  
  // 检查是否以978或979开头（标准ISBN-13前缀）
  if (!cleaned.startsWith('978') && !cleaned.startsWith('979')) {
    return false
  }
  
  // 检查是否全部是数字（除了最后一位可能是X）
  for (let i = 0; i < 12; i++) {
    if (!/[0-9]/.test(cleaned[i])) {
      return false
    }
  }
  
  // 最后一位必须是数字或X
  if (!/[0-9X]/.test(cleaned[12])) {
    return false
  }
  
  return true
}

// 检查ISBN是否存在
const checkISBNExists = async (isbn) => {
  try {
    const response = await admin.getBooks({ search: isbn })
    const books = response.data.records || []
    return books.some(book => book.isbn === isbn && (!editingBookId.value || book.bookId !== editingBookId.value))
  } catch (error) {
    console.error('检查ISBN失败:', error)
    return false
  }
}

// 生成随机ISBN
const generateRandomISBN = async () => {
  let attempts = 0
  let generatedISBN = ''
  
  do {
    // 生成以978或979开头的随机13位数字
    const prefix = Math.random() > 0.5 ? '978' : '979'
    const middle = Math.floor(Math.random() * 1000000000).toString().padStart(9, '0')
    const checkDigit = Math.floor(Math.random() * 10)
    generatedISBN = prefix + middle + checkDigit
    attempts++
  } while (await checkISBNExists(generatedISBN) && attempts < 10)
  
  bookForm.value.isbn = generatedISBN
  await handleISBNInput({value: generatedISBN})
}

// 处理ISBN输入
const handleISBNInput = async (event) => {
  const value = event.target ? event.target.value : event.value || bookForm.value.isbn
  const formatted = formatISBN(value)
  bookForm.value.isbn = formatted
  
  // 寻找ISBN输入框元素
  const isbnInput = document.querySelector('input[placeholder*="输入13位数字"]')
  const isbnHelper = document.querySelector('.isbn-helper')
  
  if (isbnInput) {
    // 如果超过13位字符，显示验证状态
    if (formatted.replace(/[^0-9X]/g, '').length === 13) {
      const isValid = validateISBN(formatted)
      const isExists = await checkISBNExists(formatted)
      
      if (!isValid) {
        isbnInput.classList.add('border-red-500')
        isbnInput.classList.remove('border-green-500', 'border-gray-300')
        if (isbnHelper) isbnHelper.textContent = '❌ ISBN格式不正确'
      } else if (isExists) {
        isbnInput.classList.add('border-red-500')
        isbnInput.classList.remove('border-green-500', 'border-gray-300')
        if (isbnHelper) isbnHelper.textContent = '❌ 该ISBN号码已存在'
      } else {
        isbnInput.classList.add('border-green-500')
        isbnInput.classList.remove('border-red-500', 'border-gray-300')
        if (isbnHelper) isbnHelper.textContent = '✅ ISBN号码可用'
      }
    } else {
      isbnInput.classList.remove('border-green-500', 'border-red-500')
      isbnInput.classList.add('border-gray-300')
      if (isbnHelper) isbnHelper.textContent = '13位ISBN号码，输入数字即可自动格式化'
    }
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  bookForm.value = {
    isbn: '',
    title: '',
    author: '',
    category: '',
    publisher: '',
    publishYear: null,
    description: '',
    stockQuantity: 0,
    price: 0
  }
  editingBookId.value = null
}

// 分页函数
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadBooks(page)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadBooks(1)
}

// 获取可见的页码
const getVisiblePages = () => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    // 如果总页数少于等于7页，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 总是显示第一页
    pages.push(1)
    
    if (current <= 4) {
      // 当前页在前4页
      for (let i = 2; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      // 当前页在后4页
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      // 当前页在中间
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
    link.href = '/书籍导入模板.xlsx'
    link.download = '书籍导入模板.xlsx'
    link.click()
  } else if (type === 'csv') {
    // 创建CSV模板内容
    const csvContent = '书名,作者,ISBN,分类,出版社,出版年份,库存数量,价格,描述\nJava编程思想,Bruce Eckel,9787111213826,计算机,机械工业出版社,2021,15,89.00,Java编程经典教材\n数据结构与算法,严蔚敏,9787302147510,计算机,清华大学出版社,2020,20,65.00,数据结构基础教材'
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', '书籍导入模板.csv')
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

const importBooks = async () => {
  if (!selectedFile.value) {
    alert('请先选择要导入的文件')
    return
  }

  importing.value = true
  importResult.value = null

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    
    const response = await admin.importBooks(formData)
    
    if (response.code === 200) {
      importResult.value = {
        success: true,
        message: response.data
      }
      // 导入成功后刷新图书列表
      await loadBooks(currentPage.value)
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
</script> 