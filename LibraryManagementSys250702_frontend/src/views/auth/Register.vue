<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          用户注册
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          创建您的账户
        </p>
      </div>
      <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
        <div class="space-y-4">
          <div>
            <label for="username" class="form-label">用户名</label>
            <input
              id="username"
              name="username"
              type="text"
              required
              v-model="form.username"
              class="form-input"
              :class="{ 'border-red-500': errors.username }"
              placeholder="请输入用户名（3-20个字符）"
              @blur="validateUsername"
            />
            <p v-if="errors.username" class="mt-1 text-sm text-red-600">{{ errors.username }}</p>
          </div>
          <div>
            <label for="realName" class="form-label">真实姓名</label>
            <input
              id="realName"
              name="realName"
              type="text"
              required
              v-model="form.realName"
              class="form-input"
              :class="{ 'border-red-500': errors.realName }"
              placeholder="请输入真实姓名"
              @blur="validateRealName"
            />
            <p v-if="errors.realName" class="mt-1 text-sm text-red-600">{{ errors.realName }}</p>
          </div>
          <div>
            <label for="password" class="form-label">密码</label>
            <input
              id="password"
              name="password"
              type="password"
              required
              v-model="form.password"
              class="form-input"
              :class="{ 'border-red-500': errors.password }"
              placeholder="请输入密码（至少6位）"
              @blur="validatePassword"
            />
            <p v-if="errors.password" class="mt-1 text-sm text-red-600">{{ errors.password }}</p>
          </div>
          <div>
            <label for="email" class="form-label">邮箱</label>
            <input
              id="email"
              name="email"
              type="email"
              required
              v-model="form.email"
              class="form-input"
              :class="{ 'border-red-500': errors.email }"
              placeholder="请输入邮箱"
              @blur="validateEmail"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
          </div>
          <div>
            <label for="phone" class="form-label">电话</label>
            <input
              id="phone"
              name="phone"
              type="tel"
              required
              v-model="form.phone"
              class="form-input"
              :class="{ 'border-red-500': errors.phone }"
              placeholder="请输入手机号"
              @blur="validatePhone"
            />
            <p v-if="errors.phone" class="mt-1 text-sm text-red-600">{{ errors.phone }}</p>
          </div>
          <div>
            <label for="age" class="form-label">年龄</label>
            <input
              id="age"
              name="age"
              type="number"
              required
              v-model="form.age"
              class="form-input"
              :class="{ 'border-red-500': errors.age }"
              placeholder="请输入年龄"
              min="1"
              max="150"
              @blur="validateAge"
            />
            <p v-if="errors.age" class="mt-1 text-sm text-red-600">{{ errors.age }}</p>
          </div>
        </div>

        <div class="flex items-center justify-between">
          <div class="text-sm">
            <router-link
              to="/login"
              class="font-medium text-blue-600 hover:text-blue-500"
            >
              已有账户？立即登录
            </router-link>
          </div>
        </div>

        <div>
          <button
            type="submit"
            :disabled="authStore.isLoading || !isFormValid"
            class="btn-primary w-full"
            :class="{ 'opacity-50 cursor-not-allowed': authStore.isLoading || !isFormValid }"
          >
            <span v-if="authStore.isLoading">注册中...</span>
            <span v-else>注册</span>
          </button>
        </div>

        <div v-if="error" class="text-red-600 text-sm text-center">
          {{ error }}
        </div>
        <div v-if="success" class="text-green-600 text-sm text-center">
          {{ success }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()

const form = ref({
  username: '',
  realName: '',
  password: '',
  email: '',
  phone: '',
  age: ''
})

const errors = ref({
  username: '',
  realName: '',
  password: '',
  email: '',
  phone: '',
  age: ''
})

const error = ref('')
const success = ref('')

// 表单验证方法
const validateUsername = () => {
  const username = form.value.username.trim()
  if (!username) {
    errors.value.username = '用户名不能为空'
  } else if (username.length < 3 || username.length > 20) {
    errors.value.username = '用户名长度必须在3-20个字符之间'
  } else {
    errors.value.username = ''
  }
}

const validateRealName = () => {
  const realName = form.value.realName.trim()
  if (!realName) {
    errors.value.realName = '真实姓名不能为空'
  } else {
    errors.value.realName = ''
  }
}

const validatePassword = () => {
  const password = form.value.password
  if (!password) {
    errors.value.password = '密码不能为空'
  } else if (password.length < 6) {
    errors.value.password = '密码长度至少6位'
  } else {
    errors.value.password = ''
  }
}

const validateEmail = () => {
  const email = form.value.email.trim()
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!email) {
    errors.value.email = '邮箱不能为空'
  } else if (!emailRegex.test(email)) {
    errors.value.email = '邮箱格式不正确'
  } else {
    errors.value.email = ''
  }
}

const validatePhone = () => {
  const phone = form.value.phone.trim()
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phone) {
    errors.value.phone = '手机号不能为空'
  } else if (!phoneRegex.test(phone)) {
    errors.value.phone = '手机号格式不正确'
  } else {
    errors.value.phone = ''
  }
}

const validateAge = () => {
  const age = parseInt(form.value.age)
  if (!form.value.age) {
    errors.value.age = '年龄不能为空'
  } else if (isNaN(age) || age < 1 || age > 150) {
    errors.value.age = '年龄必须在1-150之间'
  } else {
    errors.value.age = ''
  }
}

// 验证整个表单
const validateForm = () => {
  validateUsername()
  validateRealName()
  validatePassword()
  validateEmail()
  validatePhone()
  validateAge()
}

// 检查表单是否有效
const isFormValid = computed(() => {
  return form.value.username && 
         form.value.realName && 
         form.value.password && 
         form.value.email && 
         form.value.phone && 
         form.value.age &&
         !errors.value.username && 
         !errors.value.realName && 
         !errors.value.password && 
         !errors.value.email && 
         !errors.value.phone && 
         !errors.value.age
})

const handleRegister = async () => {
  error.value = ''
  success.value = ''
  
  // 验证表单
  validateForm()
  
  // 如果有验证错误，不提交
  if (!isFormValid.value) {
    error.value = '请检查并修正表单中的错误'
    return
  }
  
  try {
    await authStore.register({
      username: form.value.username.trim(),
      realName: form.value.realName.trim(),
      password: form.value.password,
      email: form.value.email.trim(),
      phone: form.value.phone.trim(),
      age: parseInt(form.value.age),
      userType: 'CUSTOMER'
    })
    
    success.value = '注册成功！请登录您的账户'
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (err) {
    error.value = err.response?.data?.message || '注册失败，请重试'
  }
}
</script> 