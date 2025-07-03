<template>
  <div class="space-y-6">
      <!-- 页面标题 -->
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">个人信息</h1>
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

      <!-- 账户信息 -->
      <div class="card">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">账户信息</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
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
            <span class="px-3 py-1 text-sm font-medium bg-blue-100 text-blue-800 rounded-full">
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

        <!-- 充值区域 -->
        <div class="pt-6 border-t border-gray-200">
          <h3 class="text-md font-semibold text-gray-900 mb-3">账户充值</h3>
          <div class="flex space-x-3">
            <input
              v-model="rechargeAmount"
              type="number"
              placeholder="输入充值金额"
              class="form-input flex-1"
              min="1"
              step="0.01"
            />
            <button
              @click="handleRecharge"
              :disabled="!rechargeAmount || recharging"
              class="btn-primary whitespace-nowrap"
            >
              {{ recharging ? '充值中...' : '立即充值' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="card">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">借阅统计</h2>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="text-center p-4 bg-blue-50 rounded-lg">
            <p class="text-2xl font-bold text-blue-600">{{ stats.totalBorrows || 0 }}</p>
            <p class="text-sm text-blue-600">总借阅次数</p>
          </div>
          <div class="text-center p-4 bg-green-50 rounded-lg">
            <p class="text-2xl font-bold text-green-600">{{ stats.currentBorrows || 0 }}</p>
            <p class="text-sm text-green-600">当前借阅</p>
          </div>
          <div class="text-center p-4 bg-yellow-50 rounded-lg">
            <p class="text-2xl font-bold text-yellow-600">{{ stats.overdueBorrows || 0 }}</p>
            <p class="text-sm text-yellow-600">逾期次数</p>
          </div>
          <div class="text-center p-4 bg-purple-50 rounded-lg">
            <p class="text-2xl font-bold text-purple-600">¥{{ stats.totalFees || 0 }}</p>
            <p class="text-sm text-purple-600">累计消费</p>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { customer } from '@/api'
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

const stats = ref({})
const rechargeAmount = ref('')
const updating = ref(false)
const recharging = ref(false)
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
  await loadStats()
})

const loadProfileData = async () => {
  try {
    const response = await customer.getProfile()
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

const loadStats = async () => {
  try {
    const response = await customer.getStatistics()
    stats.value = response.data
  } catch (error) {
    console.error('Failed to load stats:', error)
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
    
    await customer.updateProfile(updateData)
    
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
    await customer.changePassword({
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

const handleRecharge = async () => {
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    alert('请输入有效的充值金额')
    return
  }
  
  recharging.value = true
  try {
    await customer.recharge(parseFloat(rechargeAmount.value))
    // 刷新用户信息
    await loadProfileData()
    rechargeAmount.value = ''
    alert('充值成功！')
  } catch (error) {
    console.error('Failed to recharge:', error)
    alert('充值失败：' + (error.response?.data?.message || '请重试'))
  } finally {
    recharging.value = false
  }
}

const getUserTypeText = (userType) => {
  switch (userType) {
    case 'ADMIN':
      return '管理员'
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