<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <h1 class="text-xl font-semibold text-gray-900">书店租书管理系统</h1>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-700">{{ authStore.user?.username }}</span>
            <button
              @click="handleLogout"
              class="text-sm text-gray-500 hover:text-gray-700"
            >
              退出登录
            </button>
          </div>
        </div>
      </div>
    </nav>

    <div class="flex">
      <!-- 侧边栏 -->
      <aside class="w-64 bg-white shadow-sm min-h-screen">
        <nav class="mt-8">
          <div class="px-4">
            <ul class="space-y-2">
              <li v-for="item in menuItems" :key="item.name">
                <router-link
                  :to="item.path"
                  class="flex items-center px-4 py-2 text-sm font-medium rounded-md"
                  :class="[
                    $route.path === item.path
                      ? 'bg-blue-100 text-blue-700'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  ]"
                >
                  <component :is="item.icon" class="w-5 h-5 mr-3" />
                  {{ item.name }}
                </router-link>
              </li>
            </ul>
          </div>
        </nav>
      </aside>

      <!-- 主内容 -->
      <main class="flex-1 p-8">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  HomeIcon,
  BookOpenIcon,
  ClockIcon,
  UserIcon,
  UsersIcon,
  DocumentTextIcon,
  CurrencyDollarIcon,
  ChartBarIcon
} from '@heroicons/vue/24/outline'

const authStore = useAuthStore()
const router = useRouter()

const menuItems = computed(() => {
  if (authStore.isAdmin) {
    return [
      { name: '仪表板', path: '/admin/dashboard', icon: HomeIcon },
      { name: '用户管理', path: '/admin/users', icon: UsersIcon },
      { name: '书籍管理', path: '/admin/books', icon: BookOpenIcon },
      { name: '借阅记录', path: '/admin/records', icon: DocumentTextIcon },
      { name: '收费标准', path: '/admin/fees', icon: CurrencyDollarIcon },
      { name: '统计分析', path: '/admin/statistics', icon: ChartBarIcon },
      { name: '个人信息', path: '/admin/profile', icon: UserIcon }
    ]
  } else {
    return [
      { name: '仪表板', path: '/customer/dashboard', icon: HomeIcon },
      { name: '图书检索', path: '/customer/books', icon: BookOpenIcon },
      { name: '借阅历史', path: '/customer/history', icon: ClockIcon },
      { name: '个人信息', path: '/customer/profile', icon: UserIcon }
    ]
  }
})

const handleLogout = async () => {
  try {
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
    router.push('/login')
  }
}
</script> 