<template>
    <div class="space-y-6">
      <!-- 页面标题 -->
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">图书检索</h1>
        <p class="text-gray-600">搜索并借阅您喜欢的图书</p>
      </div>

      <!-- 搜索表单 -->
      <div class="card">
        <form @submit.prevent="searchBooks" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="form-label">书名</label>
              <input
                v-model="searchForm.title"
                type="text"
                class="form-input"
                placeholder="输入书名"
              />
            </div>
            <div>
              <label class="form-label">作者</label>
              <input
                v-model="searchForm.author"
                type="text"
                class="form-input"
                placeholder="输入作者"
              />
            </div>
            <div>
              <label class="form-label">类别</label>
              <select v-model="searchForm.category" class="form-input">
                <option value="">全部类别</option>
                <option v-for="category in categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
            </div>
          </div>
          <div class="flex space-x-4">
            <button type="submit" class="btn-primary" :disabled="searching">
              {{ searching ? '搜索中...' : '搜索' }}
            </button>
            <button type="button" @click="resetForm" class="btn-secondary">
              重置
            </button>
          </div>
        </form>
      </div>

      <!-- 搜索结果 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">搜索结果</h2>
          <div v-if="books.length > 0" class="text-sm text-gray-500">
            共找到 {{ totalBooks }} 本图书
          </div>
        </div>

        <div v-if="searching" class="text-center py-8">
          <div class="text-gray-500">搜索中...</div>
        </div>

        <div v-else-if="books.length === 0" class="text-center py-8 text-gray-500">
          {{ hasSearched ? '没有找到符合条件的图书' : '请输入搜索条件开始查找图书' }}
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="book in books"
            :key="book.id"
            class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
          >
            <div class="space-y-3">
              <h3 class="font-semibold text-gray-900 line-clamp-2">{{ book.title }}</h3>
              <p class="text-sm text-gray-600">作者：{{ book.author }}</p>
              <p class="text-sm text-gray-600">类别：{{ book.category }}</p>
              <p class="text-sm text-gray-600">出版社：{{ book.publisher }}</p>
              <p class="text-sm text-gray-600">库存：{{ book.availableQuantity || 0 }}</p>
              <p class="text-sm text-gray-600">价格：¥{{ book.price }}</p>
              
              <div class="flex items-center justify-between pt-2">
                <span :class="getStockClass(book.availableQuantity || 0)">
                  {{ (book.availableQuantity || 0) > 0 ? '可借阅' : '暂无库存' }}
                </span>
                <button
                  @click="openBorrowModal(book)"
                  :disabled="(book.availableQuantity || 0) === 0 || borrowing[book.bookId]"
                  class="btn-primary text-sm px-3 py-1"
                >
                  {{ borrowing[book.bookId] ? '借阅中...' : '借阅' }}
                </button>
              </div>
            </div>
          </div>
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

    <!-- 借阅确认弹窗 -->
    <div
      v-if="showBorrowModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="closeBorrowModal"
    >
      <div
        class="bg-white rounded-lg p-6 max-w-md w-full mx-4"
        @click.stop
      >
        <h3 class="text-lg font-semibold text-gray-900 mb-4">确认借阅</h3>
        <div v-if="selectedBook" class="space-y-3 mb-6">
          <p><strong>书名：</strong>{{ selectedBook.title }}</p>
          <p><strong>作者：</strong>{{ selectedBook.author }}</p>
          <p><strong>书籍价值：</strong>¥{{ selectedBook.price }}</p>
          <div>
            <label class="form-label">借阅天数</label>
            <input
              v-model="borrowDays"
              type="number"
              min="1"
              max="30"
              class="form-input"
              placeholder="输入借阅天数（1-30天）"
            />
          </div>
          <p class="text-sm text-gray-600">
            借阅费用：¥2.00（固定费用）
          </p>
        </div>
        <div class="flex space-x-3">
          <button @click="confirmBorrow" class="btn-primary flex-1">
            确认借阅
          </button>
          <button @click="closeBorrowModal" class="btn-secondary flex-1">
            取消
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

const searchForm = ref({
  title: '',
  author: '',
  category: ''
})

const books = ref([])
const categories = ref([])
const totalBooks = ref(0)
const totalPages = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

const searching = ref(false)
const hasSearched = ref(false)
const borrowing = ref({})

const showBorrowModal = ref(false)
const selectedBook = ref(null)
const borrowDays = ref(7)

onMounted(async () => {
  await loadInitialData()
})

const loadInitialData = async () => {
  try {
    // 加载默认图书列表
    const response = await customer.searchBooks({ page: 1, size: 100 })
    console.log('API Response:', response) // 调试日志
    
    // 使用与searchBooks相同的数据处理逻辑
    let bookList = []
    if (response) {
      if (response.data) {
        if (Array.isArray(response.data)) {
          bookList = response.data
        } else if (response.data.records && Array.isArray(response.data.records)) {
          bookList = response.data.records
        } else if (response.data.content && Array.isArray(response.data.content)) {
          bookList = response.data.content
        }
      } else if (Array.isArray(response)) {
        bookList = response
      }
    }
    
    // 确保每个书籍对象都有必要的字段
    books.value = bookList.map(book => ({
      ...book,
      bookId: book.bookId || book.id, // 确保有bookId字段
      availableQuantity: book.availableQuantity || book.stock || 0,
      price: book.price || 0
    }))
    
    // 从书籍数据中提取分类
    categories.value = [...new Set(books.value.map(book => book.category))].filter(Boolean)
    totalBooks.value = books.value.length
    hasSearched.value = true
    
    console.log('Initial books loaded:', books.value.length) // 调试日志
  } catch (error) {
    console.error('Failed to load initial data:', error)
    books.value = []
    // 不显示错误提示，因为这是初始加载
  }
}

const searchBooks = async (page = 1) => {
  searching.value = true
  try {
    const params = {
      page,
      size: pageSize.value,
      ...searchForm.value
    }
    
    // 过滤空值
    Object.keys(params).forEach(key => {
      if (!params[key]) {
        delete params[key]
      }
    })
    
    const response = await customer.searchBooks(params)
    console.log('Search response:', response) // 调试日志
    
    // 修复数据处理逻辑 - 处理多种响应格式
    let bookList = []
    if (response) {
      if (response.data) {
        if (Array.isArray(response.data)) {
          bookList = response.data
        } else if (response.data.records && Array.isArray(response.data.records)) {
          bookList = response.data.records
        } else if (response.data.content && Array.isArray(response.data.content)) {
          bookList = response.data.content
        }
      } else if (Array.isArray(response)) {
        bookList = response
      }
    }
    
    // 确保每个书籍对象都有必要的字段
    books.value = bookList.map(book => ({
      ...book,
      bookId: book.bookId || book.id, // 确保有bookId字段
      availableQuantity: book.availableQuantity || book.stock || 0,
      price: book.price || 0
    }))
    
    totalBooks.value = books.value.length
    totalPages.value = Math.ceil(totalBooks.value / pageSize.value)
    currentPage.value = page
    hasSearched.value = true
    
    console.log('Processed books:', books.value) // 调试日志
  } catch (error) {
    console.error('Failed to search books:', error)
    books.value = []
    totalBooks.value = 0
    totalPages.value = 0
    alert('搜索失败：' + (error.response?.data?.message || error.message || '请检查网络连接'))
  } finally {
    searching.value = false
  }
}

const resetForm = () => {
  searchForm.value = {
    title: '',
    author: '',
    category: ''
  }
  searchBooks()
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    searchBooks(page)
  }
}

const getStockClass = (stock) => {
  return stock > 0
    ? 'text-green-600 text-sm font-medium'
    : 'text-red-600 text-sm font-medium'
}

const openBorrowModal = (book) => {
  selectedBook.value = book
  borrowDays.value = 7
  showBorrowModal.value = true
}

const closeBorrowModal = () => {
  showBorrowModal.value = false
  selectedBook.value = null
}

const confirmBorrow = async () => {
  // 添加空值检查
  if (!selectedBook.value) {
    alert('请先选择要借阅的书籍')
    return
  }
  
  if (!borrowDays.value || borrowDays.value < 1 || borrowDays.value > 30) {
    alert('请输入有效的借阅天数（1-30天）')
    return
  }
  
  const bookId = selectedBook.value.bookId
  if (!bookId) {
    alert('书籍信息异常，请重试')
    return
  }
  
  borrowing.value[bookId] = true
  try {
    const response = await customer.borrowBook({
      bookId: bookId,
      borrowDays: parseInt(borrowDays.value)
    })
    
    console.log('Borrow response:', response)
    alert('借阅成功！')
    closeBorrowModal()
    
    // 刷新图书列表和用户信息
    await searchBooks(currentPage.value)
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
    console.error('Failed to borrow book:', error)
    let errorMessage = '借阅失败，请重试'
    if (error.response?.data?.message) {
      errorMessage = '借阅失败：' + error.response.data.message
    } else if (error.message) {
      errorMessage = '借阅失败：' + error.message
    }
    alert(errorMessage)
  } finally {
    if (bookId) {
      borrowing.value[bookId] = false
    }
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style> 