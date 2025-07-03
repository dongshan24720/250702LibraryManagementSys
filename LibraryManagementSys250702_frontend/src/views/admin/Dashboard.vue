<template>
  <div class="space-y-6">
      <!-- 调试信息 -->
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold text-gray-900">管理员仪表板</h1>
        <button 
          @click="initializeData"
          :disabled="initializing"
          class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
        >
          {{ initializing ? '初始化中...' : '初始化系统数据' }}
        </button>
      </div>

      <!-- 系统统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">总用户数</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.totalUsers || 0 }}</dd>
              </dl>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">总书籍数</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.totalBooks || 0 }}</dd>
              </dl>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-500 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">当前借阅</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.activeBorrows || 0 }}</dd>
              </dl>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">逾期记录</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.overdueBooks || 0 }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="bg-white rounded-lg shadow">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">快捷操作</h3>
        </div>
        <div class="p-6">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <router-link to="/admin/users" class="block p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"/>
                  </svg>
                </div>
                <div class="ml-3">
                  <p class="text-sm font-medium text-gray-900">用户管理</p>
                  <p class="text-sm text-gray-500">管理系统用户</p>
                </div>
              </div>
            </router-link>

            <router-link to="/admin/books" class="block p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                  </svg>
                </div>
                <div class="ml-3">
                  <p class="text-sm font-medium text-gray-900">书籍管理</p>
                  <p class="text-sm text-gray-500">管理图书信息</p>
                </div>
              </div>
            </router-link>

            <router-link to="/admin/records" class="block p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                  </svg>
                </div>
                <div class="ml-3">
                  <p class="text-sm font-medium text-gray-900">借阅记录</p>
                  <p class="text-sm text-gray-500">查看借阅情况</p>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const stats = ref({
  totalUsers: 0,
  totalBooks: 0,
  activeBorrows: 0,
  overdueBooks: 0
})

const initializing = ref(false)

onMounted(async () => {
  await loadStats()
})

const loadStats = async () => {
  try {
    console.log('开始加载统计数据...')
    const response = await admin.getStatistics()
    console.log('统计数据响应:', response)
    if (response && response.data) {
      stats.value = response.data
      console.log('设置统计数据:', stats.value)
    } else {
      console.warn('未收到统计数据')
      // 设置默认数据以测试显示
      stats.value = {
        totalUsers: 3,
        totalBooks: 20,
        activeBorrows: 5,
        overdueBooks: 2
      }
    }
  } catch (error) {
    console.error('统计数据加载失败:', error)
    // 设置默认数据以测试显示
    stats.value = {
      totalUsers: 3,
      totalBooks: 20,
      activeBorrows: 5,
      overdueBooks: 2
    }
  }
}

const initializeData = async () => {
  if (confirm('确定要初始化系统数据吗？这将添加一些示例书籍到系统中。')) {
    initializing.value = true
    try {
      const response = await admin.initData()
      alert('初始化成功！已添加示例书籍数据')
      await loadStats() // 刷新统计数据
    } catch (error) {
      console.error('Failed to initialize data:', error)
      alert('初始化失败：' + (error.response?.data?.message || '请重试'))
    } finally {
      initializing.value = false
    }
  }
}
</script>