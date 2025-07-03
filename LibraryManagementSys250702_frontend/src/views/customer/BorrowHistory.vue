<template>
    <div class="space-y-6">
      <!-- 页面标题 -->
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">借阅历史</h1>
        <p class="text-gray-600">查看您的所有借阅记录</p>
      </div>

      <!-- 筛选器 -->
      <div class="card">
        <form @submit.prevent="handleSearch" class="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
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
            <input
              v-model="filter.startDate"
              type="date"
              class="form-input"
            />
          </div>
          <div>
            <label class="form-label">结束日期</label>
            <input
              v-model="filter.endDate"
              type="date"
              class="form-input"
            />
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
      </div>

      <!-- 借阅记录列表 -->
      <div class="card">
        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>

        <div v-else-if="records.length === 0" class="text-center py-8 text-gray-500">
          暂无借阅记录
        </div>

        <div v-else>
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead>
                <tr>
                  <th class="table-header">书名</th>
                  <th class="table-header">作者</th>
                  <th class="table-header">借阅日期</th>
                  <th class="table-header">应还日期</th>
                  <th class="table-header">实际归还</th>
                  <th class="table-header">状态</th>
                  <th class="table-header">费用</th>
                  <th class="table-header">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in records" :key="record.id">
                  <td class="table-cell font-medium">{{ record.bookTitle }}</td>
                  <td class="table-cell">{{ record.bookAuthor }}</td>
                  <td class="table-cell">{{ formatDate(record.borrowDate) }}</td>
                  <td class="table-cell">{{ formatDate(record.dueDate) }}</td>
                  <td class="table-cell">
                    {{ record.returnDate ? formatDate(record.returnDate) : '-' }}
                  </td>
                  <td class="table-cell">
                    <span :class="getStatusClass(record.status)">
                      {{ getStatusText(record.status) }}
                    </span>
                  </td>
                  <td class="table-cell">¥{{ record.totalFee || 0 }}</td>
                  <td class="table-cell">
                    <button
                      v-if="record.status === 'BORROWED'"
                      @click="showReturnModal(record)"
                      class="text-blue-600 hover:text-blue-700 text-sm"
                      :disabled="returning[record.id]"
                    >
                      {{ returning[record.id] ? '归还中...' : '归还' }}
                    </button>
                    <span v-else class="text-gray-400 text-sm">-</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 分页 -->
          <div v-if="totalPages > 1" class="mt-6 flex justify-center">
            <nav class="flex space-x-2">
              <button
                @click="goToPage(currentPage - 1)"
                :disabled="currentPage <= 1"
                class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
              >
                上一页
              </button>
              <span class="px-3 py-1 text-sm text-gray-700">
                第 {{ currentPage }} 页，共 {{ totalPages }} 页
              </span>
              <button
                @click="goToPage(currentPage + 1)"
                :disabled="currentPage >= totalPages"
                class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
              >
                下一页
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>

    <!-- 归还类型选择弹窗 -->
    <div v-if="showReturnModalFlag" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 class="text-lg font-medium text-gray-900 mb-4">归还图书</h3>
        <div class="mb-4">
          <p class="text-sm text-gray-600 mb-2">
            图书：{{ selectedRecord?.bookTitle || '未知' }}
          </p>
          <p class="text-sm text-gray-600 mb-4">
            借阅时间：{{ selectedRecord?.borrowDate ? formatDate(selectedRecord.borrowDate) : '未知' }}
          </p>
        </div>

        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">归还类型</label>
          <select v-model="returnForm.returnType" class="w-full border border-gray-300 rounded-md px-3 py-2">
            <option value="">请选择归还类型</option>
            <option value="1">正常归还</option>
            <option value="2">超期归还</option>
            <option value="3">损坏归还</option>
            <option value="4">丢失归还</option>
          </select>
        </div>

        <div v-if="returnForm.returnType === '3'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">损坏描述</label>
          <textarea
            v-model="returnForm.damageDescription"
            class="w-full border border-gray-300 rounded-md px-3 py-2"
            rows="3"
            placeholder="请详细描述书籍损坏情况..."
          ></textarea>
        </div>

        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">备注说明</label>
          <textarea
            v-model="returnForm.remarks"
            class="w-full border border-gray-300 rounded-md px-3 py-2"
            rows="2"
            placeholder="可选择填写备注信息..."
          ></textarea>
        </div>

        <!-- 费用提示 -->
        <div v-if="returnForm.returnType" class="mb-4 p-3 bg-yellow-50 rounded-md">
          <p class="text-sm text-yellow-800">
            <strong>费用说明：</strong>
            <span v-if="returnForm.returnType === '1'">正常归还只收取基本借阅费用（2元/天）</span>
            <span v-else-if="returnForm.returnType === '2'">超期归还将收取1.5倍借阅费用 + 超期罚款（每天1元），罚款记录将保存到系统中</span>
            <span v-else-if="returnForm.returnType === '3'">损坏归还将收取2倍借阅费用 + 损坏赔偿费（20元）+ 可能的超期罚款，罚款记录将保存到系统中</span>
            <span v-else-if="returnForm.returnType === '4'">丢失归还将收取3倍借阅费用 + 丢失赔偿费（50元）+ 可能的超期罚款，且书籍库存不恢复，罚款记录将保存到系统中</span>
          </p>
        </div>

        <div class="flex justify-end space-x-3">
          <button
            @click="closeReturnModal"
            class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md"
          >
            取消
          </button>
          <button
            @click="confirmReturn"
            :disabled="!returnForm.returnType || returning[selectedRecord?.id]"
            class="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 rounded-md"
          >
            {{ returning[selectedRecord?.id] ? '归还中...' : '确认归还' }}
          </button>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { customer } from '@/api'
import { useAuthStore } from '@/stores/auth'


const authStore = useAuthStore()

const records = ref([])
const totalPages = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const returning = ref({})

const filter = ref({
  status: '',
  startDate: '',
  endDate: ''
})

// 归还弹窗相关
const showReturnModalFlag = ref(false)
const selectedRecord = ref(null)
const returnForm = ref({
  returnType: '',
  remarks: '',
  damageDescription: ''
})

onMounted(async () => {
  await loadHistory()
})

const handleSearch = () => {
  loadHistory(1) // 搜索时总是从第1页开始
}

const loadHistory = async (page = 1) => {
  loading.value = true
  try {
    const params = {
      page,
      size: pageSize.value,
      ...filter.value
    }
    
    // 过滤空值
    Object.keys(params).forEach(key => {
      if (!params[key]) {
        delete params[key]
      }
    })
    
    const response = await customer.getBorrowHistory(params)
    console.log('Borrow history response:', response) // 调试日志
    
    // 处理多种响应格式
    let recordList = []
    if (response) {
      if (response.data) {
        if (Array.isArray(response.data)) {
          recordList = response.data
        } else if (response.data.records && Array.isArray(response.data.records)) {
          recordList = response.data.records
        } else if (response.data.content && Array.isArray(response.data.content)) {
          recordList = response.data.content
        }
      } else if (Array.isArray(response)) {
        recordList = response
      }
    }
    
    // 确保每个记录都有必要的字段
    records.value = recordList.map(record => ({
      ...record,
      id: record.id || record.recordId, // 确保有id字段
      recordId: record.recordId || record.id, // 确保有recordId字段
      totalFee: record.totalFee || 0
    }))
    
    totalPages.value = Math.ceil(records.value.length / pageSize.value)
    currentPage.value = page
    
    console.log('Processed records:', records.value) // 调试日志
  } catch (error) {
    console.error('Failed to load history:', error)
    records.value = []
    alert('加载借阅记录失败：' + (error.response?.data?.message || error.message || '请检查网络连接'))
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filter.value = {
    status: '',
    startDate: '',
    endDate: ''
  }
  loadHistory()
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadHistory(page)
  }
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

// 显示归还弹窗
const showReturnModal = (record) => {
  selectedRecord.value = record
  returnForm.value = {
    returnType: '',
    remarks: '',
    damageDescription: ''
  }
  showReturnModalFlag.value = true
}

// 关闭归还弹窗
const closeReturnModal = () => {
  showReturnModalFlag.value = false
  selectedRecord.value = null
  returnForm.value = {
    returnType: '',
    remarks: '',
    damageDescription: ''
  }
}

// 确认归还
const confirmReturn = async () => {
  if (!selectedRecord.value || !returnForm.value.returnType) {
    alert('请选择归还类型')
    return
  }

  const recordId = selectedRecord.value.id
  returning.value[recordId] = true
  
  try {
    const returnRequest = {
      returnType: parseInt(returnForm.value.returnType),
      remarks: returnForm.value.remarks,
      damageDescription: returnForm.value.damageDescription
    }
    
    const response = await customer.returnBookWithType(recordId, returnRequest)
    console.log('Return response:', response)
    
    alert('归还成功！')
    closeReturnModal()
    await loadHistory(currentPage.value)
    
    // 刷新用户信息以更新余额
    try {
      const profileResponse = await customer.getProfile()
      if (profileResponse && profileResponse.data) {
        authStore.user = profileResponse.data
        localStorage.setItem('user', JSON.stringify(authStore.user))
      }
    } catch (profileError) {
      console.warn('Failed to refresh user profile:', profileError)
    }
  } catch (error) {
    console.error('Failed to return book:', error)
    let errorMessage = '归还失败，请重试'
    if (error.response?.data?.message) {
      errorMessage = '归还失败：' + error.response.data.message
    } else if (error.message) {
      errorMessage = '归还失败：' + error.message
    }
    alert(errorMessage)
  } finally {
    returning.value[recordId] = false
  }
}

// 保留原有的简单归还方法（用于兼容）
const returnBook = async (recordId) => {
  if (!recordId) {
    alert('记录ID异常，请重试')
    return
  }
  
  returning.value[recordId] = true
  try {
    const response = await customer.returnBook(recordId)
    console.log('Return response:', response)
    
    alert('归还成功！')
    await loadHistory(currentPage.value)
    
    // 刷新用户信息以更新余额
    try {
      const profileResponse = await customer.getProfile()
      if (profileResponse && profileResponse.data) {
        authStore.user = profileResponse.data
        localStorage.setItem('user', JSON.stringify(authStore.user))
      }
    } catch (profileError) {
      console.warn('Failed to refresh user profile:', profileError)
    }
  } catch (error) {
    console.error('Failed to return book:', error)
    let errorMessage = '归还失败，请重试'
    if (error.response?.data?.message) {
      errorMessage = '归还失败：' + error.response.data.message
    } else if (error.message) {
      errorMessage = '归还失败：' + error.message
    }
    alert(errorMessage)
  } finally {
    returning.value[recordId] = false
  }
}
</script> 