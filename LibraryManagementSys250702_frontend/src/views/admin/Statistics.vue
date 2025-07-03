<template>
    <div class="space-y-6">
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">统计分析</h1>
        <p class="text-gray-600">系统数据分析和报表</p>
      </div>

      <!-- 总体统计 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="card">
          <div class="text-center">
            <div class="text-3xl font-bold text-blue-600">{{ overallStats.totalUsers }}</div>
            <div class="text-sm text-gray-600 mt-1">总用户数</div>
          </div>
        </div>
        <div class="card">
          <div class="text-center">
            <div class="text-3xl font-bold text-green-600">{{ overallStats.totalBooks }}</div>
            <div class="text-sm text-gray-600 mt-1">总图书数</div>
          </div>
        </div>
        <div class="card">
          <div class="text-center">
            <div class="text-3xl font-bold text-yellow-600">{{ overallStats.totalBorrows }}</div>
            <div class="text-sm text-gray-600 mt-1">总借阅次数</div>
          </div>
        </div>
        <div class="card">
          <div class="text-center">
            <div class="text-3xl font-bold text-purple-600">¥{{ overallStats.totalIncome }}</div>
            <div class="text-sm text-gray-600 mt-1">总收入</div>
          </div>
        </div>
      </div>

      <!-- 用户年龄分布 -->
      <div class="card">
        <h2 class="text-lg font-bold text-gray-900 mb-4">用户年龄分布</h2>
        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>
        <div v-else class="space-y-4">
          <div v-for="item in ageDistribution" :key="item.ageRange" class="flex items-center">
            <div class="w-20 text-sm text-gray-600">{{ item.ageRange }}</div>
            <div class="flex-1 mx-4">
              <div class="bg-gray-200 rounded-full h-4">
                <div 
                  class="bg-blue-600 h-4 rounded-full" 
                  :style="{ width: `${(item.count / maxAgeCount) * 100}%` }"
                ></div>
              </div>
            </div>
            <div class="w-16 text-sm text-gray-900 font-medium">{{ item.count }}人</div>
          </div>
        </div>
      </div>

      <!-- 图书类别分布 -->
      <div class="card">
        <h2 class="text-lg font-bold text-gray-900 mb-4">图书类别分布</h2>
        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-for="item in categoryDistribution" :key="item.category" class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div>
              <div class="font-medium text-gray-900">{{ item.category }}</div>
              <div class="text-sm text-gray-600">{{ item.count }}本</div>
            </div>
            <div class="text-right">
              <div class="text-lg font-bold text-green-600">{{ item.percentage }}%</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门图书排行 -->
      <div class="card">
        <h2 class="text-lg font-bold text-gray-900 mb-4">热门图书排行</h2>
        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>
        <div v-else-if="popularBooks.length === 0" class="text-center py-8 text-gray-500">
          暂无数据
        </div>
        <div v-else class="overflow-x-auto">
          <table class="min-w-full">
            <thead>
              <tr>
                <th class="table-header">排名</th>
                <th class="table-header">书名</th>
                <th class="table-header">作者</th>
                <th class="table-header">类别</th>
                <th class="table-header">借阅次数</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(book, index) in popularBooks" :key="book.id">
                <td class="table-cell">
                  <span :class="getRankClass(index + 1)">
                    {{ index + 1 }}
                  </span>
                </td>
                <td class="table-cell font-medium">{{ book.title }}</td>
                <td class="table-cell">{{ book.author }}</td>
                <td class="table-cell">{{ book.category }}</td>
                <td class="table-cell">
                  <span class="font-bold text-blue-600">{{ book.borrowCount }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { admin } from '@/api'


const loading = ref(false)

const overallStats = ref({
  totalUsers: 0,
  totalBooks: 0,
  totalBorrows: 0,
  totalIncome: 0
})

const ageDistribution = ref([])
const categoryDistribution = ref([])
const popularBooks = ref([])

const maxAgeCount = computed(() => {
  return Math.max(...ageDistribution.value.map(item => item.count), 1)
})

onMounted(async () => {
  await loadStatistics()
})

const loadStatistics = async () => {
  loading.value = true
  try {
    // 加载总体统计
    const statsResponse = await admin.getStatistics()
    if (statsResponse.data) {
      overallStats.value = statsResponse.data
    }

    // 模拟年龄分布数据
    ageDistribution.value = [
      { ageRange: '18-25', count: 45 },
      { ageRange: '26-35', count: 38 },
      { ageRange: '36-45', count: 22 },
      { ageRange: '46-55', count: 15 },
      { ageRange: '55+', count: 8 }
    ]

    // 模拟类别分布数据
    categoryDistribution.value = [
      { category: '文学', count: 85, percentage: 35 },
      { category: '科技', count: 62, percentage: 26 },
      { category: '历史', count: 48, percentage: 20 },
      { category: '艺术', count: 28, percentage: 12 },
      { category: '其他', count: 17, percentage: 7 }
    ]

    // 模拟热门图书数据
    popularBooks.value = [
      { id: 1, title: '三体', author: '刘慈欣', category: '科幻', borrowCount: 45 },
      { id: 2, title: '活着', author: '余华', category: '文学', borrowCount: 38 },
      { id: 3, title: '百年孤独', author: '马尔克斯', category: '文学', borrowCount: 32 },
      { id: 4, title: '人类简史', author: '尤瓦尔·赫拉利', category: '历史', borrowCount: 28 },
      { id: 5, title: '时间简史', author: '霍金', category: '科学', borrowCount: 25 }
    ]

  } catch (error) {
    console.error('Failed to load statistics:', error)
  } finally {
    loading.value = false
  }
}

const getRankClass = (rank) => {
  switch (rank) {
    case 1:
      return 'inline-flex items-center justify-center w-6 h-6 bg-yellow-100 text-yellow-600 text-sm font-bold rounded-full'
    case 2:
      return 'inline-flex items-center justify-center w-6 h-6 bg-gray-100 text-gray-600 text-sm font-bold rounded-full'
    case 3:
      return 'inline-flex items-center justify-center w-6 h-6 bg-orange-100 text-orange-600 text-sm font-bold rounded-full'
    default:
      return 'inline-flex items-center justify-center w-6 h-6 bg-blue-100 text-blue-600 text-sm font-bold rounded-full'
  }
}
</script>