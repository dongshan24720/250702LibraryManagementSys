import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 认证API
export const auth = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  logout: () => api.post('/auth/logout')
}

// 客户API
export const customer = {
  getProfile: () => api.get('/customer/profile'),
  updateProfile: (data) => api.put('/customer/profile', data),
  changePassword: (data) => api.post('/customer/change-password', data, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [(data) => {
      const params = new URLSearchParams()
      params.append('oldPassword', data.oldPassword)
      params.append('newPassword', data.newPassword)
      return params
    }]
  }),
  recharge: (amount) => api.post('/customer/recharge', { amount }),
  searchBooks: (params = {}) => api.get('/customer/books/search', { params }),
  borrowBook: (data) => api.post('/customer/borrow', data),
  returnBook: (recordId) => api.post(`/customer/return/${recordId}`),
  returnBookWithType: (recordId, returnRequest) => api.post(`/customer/return-with-type/${recordId}`, returnRequest),
  getBorrowHistory: (params = {}) => api.get('/customer/borrowing-records', { params }),
  getStatistics: () => api.get('/customer/statistics')
}

// 管理员API
export const admin = {
  // 个人信息管理
  getProfile: () => api.get('/customer/profile'), // 管理员使用相同的profile接口
  updateProfile: (data) => api.put('/customer/profile', data), // 管理员使用相同的update接口
  changePassword: (data) => api.post('/customer/change-password', data, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [(data) => {
      const params = new URLSearchParams()
      params.append('oldPassword', data.oldPassword)
      params.append('newPassword', data.newPassword)
      return params
    }]
  }),
  
  // 用户管理
  getUsers: (params) => api.get('/admin/users', { params }),
  createUser: (data) => api.post('/admin/users', data),
  updateUser: (id, data) => api.put(`/admin/users/${id}`, data),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  updateUserBalance: (id, balance) => api.put(`/admin/users/${id}/balance`, null, { params: { balance } }),
  importUsers: (formData) => api.post('/admin/import/users', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  
  // 书籍管理
  getBooks: (params) => api.get('/admin/books', { params }),
  createBook: (data) => api.post('/admin/books', data),
  updateBook: (id, data) => api.put(`/admin/books/${id}`, data),
  deleteBook: (id) => api.delete(`/admin/books/${id}`),
  importBooks: (formData) => api.post('/admin/import/books', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  
  // 借阅记录管理
  getBorrowRecords: (params) => api.get('/admin/borrow-records', { params }),
  
  // 收费标准管理
  getFeeStandards: () => api.get('/admin/fee-standards'),
  updateFeeStandard: (id, data) => api.put(`/admin/fee-standards/${id}`, data),
  createFeeStandard: (data) => api.post('/admin/fee-standards', data),
  deleteFeeStandard: (id) => api.delete(`/admin/fee-standards/${id}`),
  initFeeStandards: () => api.post('/admin/fee-standards/init'),
  
  // 统计API
  getStatistics: () => api.get('/admin/statistics'),
  getAgeDistribution: () => api.get('/admin/statistics/age-distribution'),
  getCategoryDistribution: () => api.get('/admin/statistics/category-distribution'),
  getBorrowingStats: () => api.get('/admin/statistics/borrowing'),
  getDamageStats: () => api.get('/admin/statistics/damage'),
  
  // 系统初始化
  initData: () => api.post('/admin/init-data')
}

export default api 