<template>
  <div class="space-y-6">
      <!-- 页面标题 -->
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">管理员个人信息</h1>
        <p class="text-gray-600">管理您的个人资料和账户设置</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 个人信息表单 -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">基本信息</h2>
          <form @submit.prevent="updateProfile" class="space-y-4">
            <div>
              <label class="form-label">用户名</label>
              <input
                v-model="form.username"
                type="text"
                class="form-input"
                disabled
              />
              <p class="text-sm text-gray-500 mt-1">用户名不能修改</p>
            </div>
            
            <div>
              <label class="form-label">真实姓名</label>
              <input
                v-model="form.realName"
                type="text"
                class="form-input"
                placeholder="请输入真实姓名"
              />
            </div>
            
            <div>
              <label class="form-label">性别</label>
              <select v-model="form.gender" class="form-input">
                <option value="">请选择性别</option>
                <option value="0">女</option>
                <option value="1">男</option>
              </select>
            </div>
            
            <div>
              <label class="form-label">年龄</label>
              <input
                v-model="form.age"
                type="number"
                class="form-input"
                placeholder="请输入年龄"
                min="1"
                max="150"
              />
            </div>
            
            <div>
              <label class="form-label">邮箱</label>
              <input
                v-model="form.email"
                type="email"
                class="form-input"
                placeholder="请输入邮箱地址"
              />
            </div>
            
            <div>
              <label class="form-label">电话</label>
              <input
                v-model="form.phone"
                type="tel"
                class="form-input"
                placeholder="请输入手机号码"
              />
            </div>
            
            <div class="flex space-x-3">
              <button
                type="submit"
                :disabled="updating"
                class="btn-primary"
              >
                {{ updating ? '更新中...' : '更新信息' }}
              </button>
              <button
                type="button"
                @click="resetForm"
                class="btn-secondary"
              >
                重置
              </button>
            </div>
          </form>
        </div>

        <!-- 密码修改 -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">密码修改</h2>
          <form @submit.prevent="changePassword" class="space-y-4">
            <div>
              <label class="form-label">当前密码</label>
              <input
                v-model="passwordForm.oldPassword"
                type="password"
                class="form-input"
                required
                placeholder="请输入当前密码"
              />
            </div>
            
            <div>
              <label class="form-label">新密码</label>
              <input
                v-model="passwordForm.newPassword"
                type="password"
                class="form-input"
                required
                placeholder="请输入新密码"
                minlength="6"
              />
              <p class="text-sm text-gray-500 mt-1">密码长度至少6位</p>
            </div>
            
            <div>
              <label class="form-label">确认新密码</label>
              <input
                v-model="passwordForm.confirmPassword"
                type="password"
                class="form-input"
                required
                placeholder="请再次输入新密码"
              />
            </div>
            
            <div class="flex space-x-3">
              <button
                type="submit"
                :disabled="changingPassword || !isPasswordFormValid"
                class="btn-primary"
              >
                {{ changingPassword ? '修改中...' : '修改密码' }}
              </button>
              <button
                type="button"
                @click="resetPasswordForm"
                class="btn-secondary"
              >
                重置
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 管理员账户信息 -->
      <div class="card">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">账户信息</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <div>
              <p class="font-medium text-gray-900">账户余额</p>
              <p class="text-sm text-gray-600">当前可用余额</p>
            </div>
            <p class="text-2xl font-bold text-blue-600">¥{{ authStore.user?.balance || 0 }}</p>
          </div>
          
          <div class="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <div>
              <p class="font-medium text-gray-900">用户类型</p>
              <p class="text-sm text-gray-600">账户类型</p>
            </div>
            <span class="px-3 py-1 text-sm font-medium bg-red-100 text-red-800 rounded-full">
              {{ getUserTypeText(authStore.user?.userType) }}
            </span>
          </div>
          
          <div class="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <div>
              <p class="font-medium text-gray-900">注册时间</p>
              <p class="text-sm text-gray-600">账户创建日期</p>
            </div>
            <p class="text-gray-900">{{ formatDate(authStore.user?.createdAt) }}</p>
          </div>
        </div>
      </div>

      <!-- 管理员权限说明 -->
      <div class="card">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">管理员权限</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div class="flex items-center p-4 bg-green-50 rounded-lg">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-green-900">用户管理</p>
              <p class="text-sm text-green-700">管理系统用户</p>
            </div>
          </div>
          
          <div class="flex items-center p-4 bg-blue-50 rounded-lg">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-blue-900">图书管理</p>
              <p class="text-sm text-blue-700">管理图书信息</p>
            </div>
          </div>
          
          <div class="flex items-center p-4 bg-purple-50 rounded-lg">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-purple-900">借阅管理</p>
              <p class="text-sm text-purple-700">管理借阅记录</p>
            </div>
          </div>
          
          <div class="flex items-center p-4 bg-yellow-50 rounded-lg">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-yellow-900">收费管理</p>
              <p class="text-sm text-yellow-700">管理收费标准</p>
            </div>
          </div>
          
          <div class="flex items-center p-4 bg-indigo-50 rounded-lg">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-indigo-900">数据统计</p>
              <p class="text-sm text-indigo-700">查看系统统计</p>
            </div>
          </div>
          
          <div class="flex items-center p-4 bg-gray-50 rounded-lg">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-900">数据导入</p>
              <p class="text-sm text-gray-700">批量导入数据</p>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { admin } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const form = ref({
  username: '',
  realName: '',
  gender: '',
  age: '',
  email: '',
  phone: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const updating = ref(false)
const changingPassword = ref(false)

// 检查密码表单是否有效
const isPasswordFormValid = computed(() => {
  return passwordForm.value.oldPassword && 
         passwordForm.value.newPassword && 
         passwordForm.value.confirmPassword &&
         passwordForm.value.newPassword === passwordForm.value.confirmPassword &&
         passwordForm.value.newPassword.length >= 6
})

onMounted(async () => {
  await loadProfileData()
})

const loadProfileData = async () => {
  try {
    const response = await admin.getProfile()
    const profileData = response.data
    
    // 更新表单数据
    form.value = {
      username: profileData.username || '',
      realName: profileData.realName || '',
      gender: profileData.gender !== null ? profileData.gender.toString() : '',
      age: profileData.age || '',
      email: profileData.email || '',
      phone: profileData.phone || ''
    }
    
    // 更新authStore中的用户信息
    authStore.user = {
      ...authStore.user,
      ...profileData
    }
    localStorage.setItem('user', JSON.stringify(authStore.user))
  } catch (error) {
    console.error('Failed to load profile:', error)
    // 如果API调用失败，回退到authStore数据
    if (authStore.user) {
      form.value = {
        username: authStore.user.username || '',
        realName: authStore.user.realName || '',
        gender: authStore.user.gender !== null ? authStore.user.gender.toString() : '',
        age: authStore.user.age || '',
        email: authStore.user.email || '',
        phone: authStore.user.phone || ''
      }
    }
  }
}

const updateProfile = async () => {
  updating.value = true
  try {
    const updateData = {
      realName: form.value.realName,
      gender: form.value.gender ? parseInt(form.value.gender) : null,
      age: form.value.age ? parseInt(form.value.age) : null,
      email: form.value.email,
      phone: form.value.phone
    }
    
    await admin.updateProfile(updateData)
    
    // 重新加载个人信息
    await loadProfileData()
    
    alert('个人信息更新成功！')
  } catch (error) {
    console.error('Failed to update profile:', error)
    alert('更新失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    updating.value = false
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    alert('两次输入的新密码不一致')
    return
  }
  
  if (passwordForm.value.newPassword.length < 6) {
    alert('密码长度至少6位')
    return
  }
  
  changingPassword.value = true
  try {
    await admin.changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    resetPasswordForm()
    alert('密码修改成功！')
  } catch (error) {
    console.error('Failed to change password:', error)
    alert('密码修改失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    changingPassword.value = false
  }
}

const resetForm = () => {
  loadProfileData()
}

const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const getUserTypeText = (userType) => {
  switch (userType) {
    case 'ADMIN':
      return '系统管理员'
    case 'CUSTOMER':
      return '客户'
    default:
      return '未知'
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString()
}
</script> 