<template>
    <div class="space-y-6">
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">借阅记录</h1>
        <p class="text-gray-600">查看和管理所有借阅记录</p>
      </div>

      <div class="card">
        <form @submit.prevent="handleFilter" class="grid grid-cols-1 md:grid-cols-4 gap-4 items-end mb-6">
          <div>
            <label class="form-label">用户名</label>
            <input v-model="filter.username" type="text" class="form-input" placeholder="输入用户名" />
          </div>
          <div>
            <label class="form-label">状态</label>
            <select v-model="filter.status" class="form-input">
              <option value="">全部状态</option>
              <option value="BORROWED">已借阅</option>
              <option value="RETURNED">已归还</option>
              <option value="OVERDUE">已逾期</option>
              <option value="DAMAGED">已损坏</option>
              <option value="LOST">已丢失</option>
            </select>
          </div>
          <div>
            <label class="form-label">开始日期</label>
            <input v-model="filter.startDate" type="date" class="form-input" />
          </div>
          <div class="flex space-x-2">
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? '查询中...' : '查询' }}
            </button>
            <button type="button" @click="resetFilter" class="btn-secondary">
              重置
            </button>
          </div>
        </form>

        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>

        <div v-else-if="records.length === 0" class="text-center py-8 text-gray-500">
          暂无借阅记录
        </div>

        <div v-else class="overflow-x-auto border border-gray-200 rounded-lg">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="table-header border-r border-gray-200">用户</th>
                <th class="table-header border-r border-gray-200">书名</th>
                <th class="table-header border-r border-gray-200">借阅日期</th>
                <th class="table-header border-r border-gray-200">应还日期</th>
                <th class="table-header border-r border-gray-200">实际归还</th>
                <th class="table-header border-r border-gray-200">状态</th>
                <th class="table-header">费用</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="record in records" :key="record.id" class="hover:bg-gray-50 transition-colors">
                <td class="table-cell border-r border-gray-200">{{ record.username }}</td>
                <td class="table-cell font-medium border-r border-gray-200">{{ record.bookTitle }}</td>
                <td class="table-cell border-r border-gray-200">{{ formatDate(record.borrowDate) }}</td>
                <td class="table-cell border-r border-gray-200">{{ formatDate(record.dueDate) }}</td>
                <td class="table-cell border-r border-gray-200">
                  {{ record.returnDate ? formatDate(record.returnDate) : '-' }}
                </td>
                <td class="table-cell border-r border-gray-200">
                  <span :class="getStatusClass(record.status)">
                    {{ getStatusText(record.status) }}
                  </span>
                </td>
                <td class="table-cell">¥{{ record.totalFee || 0 }}</td>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '@/api'


const records = ref([])
const loading = ref(false)

// 分页相关变量
const totalPages = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filter = ref({
  username: '',
  status: '',
  startDate: ''
})

onMounted(async () => {
  await loadRecords()
})

const loadRecords = async (page = 1) => {
  loading.value = true
  try {
    const params = { 
      current: page,
      size: pageSize.value,
      ...filter.value 
    }
    Object.keys(params).forEach(key => {
      if (!params[key]) {
        delete params[key]
      }
    })
    
    const response = await admin.getBorrowRecords(params)
    if (response.data) {
      records.value = response.data.records || []
      total.value = response.data.total || 0
      totalPages.value = Math.ceil(total.value / pageSize.value)
      currentPage.value = page
    } else {
      records.value = []
      total.value = 0
      totalPages.value = 0
    }
  } catch (error) {
    console.error('Failed to load records:', error)
    records.value = []
    total.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filter.value = {
    username: '',
    status: '',
    startDate: ''
  }
  currentPage.value = 1
  loadRecords(1)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString()
}

const getStatusClass = (status) => {
  switch (status) {
    case 'BORROWED':
      return 'px-2 py-1 text-xs font-medium bg-yellow-100 text-yellow-800 rounded-full'
    case 'RETURNED':
      return 'px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded-full'
    case 'OVERDUE':
      return 'px-2 py-1 text-xs font-medium bg-red-100 text-red-800 rounded-full'
    case 'LOST':
      return 'px-2 py-1 text-xs font-medium bg-purple-100 text-purple-800 rounded-full'
    case 'DAMAGED':
      return 'px-2 py-1 text-xs font-medium bg-orange-100 text-orange-800 rounded-full'
    default:
      return 'px-2 py-1 text-xs font-medium bg-gray-100 text-gray-800 rounded-full'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'BORROWED':
      return '已借阅'
    case 'RETURNED':
      return '已归还'
    case 'OVERDUE':
      return '已逾期'
    case 'LOST':
      return '已丢失'
    case 'DAMAGED':
      return '已损坏'
    default:
      return '未知'
  }
}

// 分页函数
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadRecords(page)
  }
}

const handleFilter = () => {
  currentPage.value = 1
  loadRecords(1)
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
</script> 