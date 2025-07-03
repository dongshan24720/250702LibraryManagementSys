import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from './stores/auth'

// Layout组件
import Layout from './components/Layout.vue'

// 认证页面
import Login from './views/auth/Login.vue'
import Register from './views/auth/Register.vue'

// 客户页面
import CustomerDashboard from './views/customer/Dashboard.vue'
import BookSearch from './views/customer/BookSearch.vue'
import BorrowHistory from './views/customer/BorrowHistory.vue'
import Profile from './views/customer/Profile.vue'

// 管理员页面
import AdminDashboard from './views/admin/Dashboard.vue'
import UserManagement from './views/admin/UserManagement.vue'
import BookManagement from './views/admin/BookManagement.vue'
import BorrowRecords from './views/admin/BorrowRecords.vue'
import FeeStandards from './views/admin/FeeStandards.vue'
import Statistics from './views/admin/Statistics.vue'
import AdminProfile from './views/admin/Profile.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { requiresGuest: true }
    },
    // 客户路由
    {
      path: '/customer',
      component: Layout,
      meta: { requiresAuth: true, role: 'CUSTOMER' },
      children: [
        {
          path: '',
          redirect: '/customer/dashboard'
        },
        {
          path: 'dashboard',
          name: 'CustomerDashboard',
          component: CustomerDashboard
        },
        {
          path: 'books',
          name: 'BookSearch',
          component: BookSearch
        },
        {
          path: 'history',
          name: 'BorrowHistory',
          component: BorrowHistory
        },
        {
          path: 'profile',
          name: 'CustomerProfile',
          component: Profile
        }
      ]
    },
    // 管理员路由
    {
      path: '/admin',
      component: Layout,
      meta: { requiresAuth: true, role: 'ADMIN' },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: AdminDashboard
        },
        {
          path: 'users',
          name: 'UserManagement',
          component: UserManagement
        },
        {
          path: 'books',
          name: 'BookManagement',
          component: BookManagement
        },
        {
          path: 'records',
          name: 'BorrowRecords',
          component: BorrowRecords
        },
        {
          path: 'fees',
          name: 'FeeStandards',
          component: FeeStandards
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: Statistics
        },
        {
          path: 'profile',
          name: 'AdminProfile',
          component: AdminProfile
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    if (authStore.isAdmin) {
      next('/admin/dashboard')
    } else {
      next('/customer/dashboard')
    }
  } else if (to.meta.role && authStore.user?.userType !== to.meta.role) {
    next('/login')
  } else {
    next()
  }
})

export default router
