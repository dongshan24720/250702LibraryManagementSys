<template>
  <div class="space-y-6">
      <!-- 页面标题 -->
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">客户仪表板</h1>
        <p class="text-gray-600">欢迎回来，{{ authStore.user?.username }}</p>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="card bg-blue-50 border-blue-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <BookOpenIcon class="h-8 w-8 text-blue-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-blue-600">当前借阅</p>
              <p class="text-2xl font-bold text-blue-900">{{ stats.currentBorrows || 0 }}</p>
            </div>
          </div>
        </div>

        <div class="card bg-green-50 border-green-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <ClockIcon class="h-8 w-8 text-green-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-green-600">历史借阅</p>
              <p class="text-2xl font-bold text-green-900">{{ stats.totalBorrows || 0 }}</p>
            </div>
          </div>
        </div>

        <div class="card bg-yellow-50 border-yellow-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <ExclamationTriangleIcon class="h-8 w-8 text-yellow-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-yellow-600">待还书籍</p>
              <p class="text-2xl font-bold text-yellow-900">{{ stats.overdueBorrows || 0 }}</p>
            </div>
          </div>
        </div>

        <div class="card bg-purple-50 border-purple-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <CurrencyDollarIcon class="h-8 w-8 text-purple-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-purple-600">账户余额</p>
              <p class="text-2xl font-bold text-purple-900">¥{{ authStore.user?.balance || 0 }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近借阅 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">最近借阅</h2>
          <router-link
            to="/customer/history"
            class="text-blue-600 hover:text-blue-700 text-sm"
          >
            查看全部
          </router-link>
        </div>
        
        <div v-if="recentBorrows.length === 0" class="text-center py-8 text-gray-500">
          暂无借阅记录
        </div>
        
        <div v-else class="overflow-x-auto">
          <table class="min-w-full">
            <thead>
              <tr>
                <th class="table-header">书名</th>
                <th class="table-header">借阅日期</th>
                <th class="table-header">应还日期</th>
                <th class="table-header">状态</th>
                <th class="table-header">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in recentBorrows" :key="record.id">
                <td class="table-cell">{{ record.bookTitle }}</td>
                <td class="table-cell">{{ formatDate(record.borrowDate) }}</td>
                <td class="table-cell">{{ formatDate(record.dueDate) }}</td>
                <td class="table-cell">
                  <span :class="getStatusClass(record.status)">
                    {{ getStatusText(record.status) }}
                  </span>
                </td>
                <td class="table-cell">
                  <button
                    v-if="record.status === 'BORROWED'"
                    @click="returnBook(record.id)"
                    class="text-blue-600 hover:text-blue-700 text-sm"
                    :disabled="returning[record.id]"
                  >
                    {{ returning[record.id] ? '归还中...' : '归还' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 快速操作 -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">快速操作</h2>
          <div class="space-y-3">
            <router-link
              to="/customer/books"
              class="block w-full btn-primary text-center"
            >
              搜索图书
            </router-link>
            <router-link
              to="/customer/history"
              class="block w-full btn-secondary text-center"
            >
              借阅历史
            </router-link>
          </div>
        </div>

        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">账户充值</h2>
          <div class="space-y-3">
            <input
              v-model="rechargeAmount"
              type="number"
              placeholder="输入充值金额"
              class="form-input"
              min="1"
              step="0.01"
            />
            <button
              @click="handleRecharge"
              :disabled="!rechargeAmount || recharging"
              class="w-full btn-primary"
            >
              {{ recharging ? '充值中...' : '立即充值' }}
            </button>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { customer } from '@/api'
import {
  BookOpenIcon,
  ClockIcon,
  ExclamationTriangleIcon,
  CurrencyDollarIcon
} from '@heroicons/vue/24/outline'

const authStore = useAuthStore()

const stats = ref({})
const recentBorrows = ref([])
const rechargeAmount = ref('')
const recharging = ref(false)
const returning = ref({})

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  try {
    const [statsResponse, historyResponse] = await Promise.all([
      customer.getStatistics(),
      customer.getBorrowHistory({ page: 1, size: 5 })
    ])
    
    stats.value = statsResponse.data
    recentBorrows.value = historyResponse.data.content || []
  } catch (error) {
    console.error('Failed to load data:', error)
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

const returnBook = async (recordId) => {
  returning.value[recordId] = true
  try {
    await customer.returnBook(recordId)
    await loadData()
    // 刷新用户信息以更新余额
    const profileResponse = await customer.getProfile()
    authStore.user = profileResponse.data
    localStorage.setItem('user', JSON.stringify(authStore.user))
  } catch (error) {
    console.error('Failed to return book:', error)
    alert('归还失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    returning.value[recordId] = false
  }
}

const handleRecharge = async () => {
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    alert('请输入有效的充值金额')
    return
  }
  
  recharging.value = true
  try {
    await customer.recharge(parseFloat(rechargeAmount.value))
    // 刷新用户信息
    const profileResponse = await customer.getProfile()
    authStore.user = profileResponse.data
    localStorage.setItem('user', JSON.stringify(authStore.user))
    rechargeAmount.value = ''
    alert('充值成功！')
  } catch (error) {
    console.error('Failed to recharge:', error)
    alert('充值失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    recharging.value = false
  }
}
</script> 