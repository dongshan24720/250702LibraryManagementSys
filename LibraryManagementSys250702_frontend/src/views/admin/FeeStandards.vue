<template>
    <div class="space-y-6">
      <div class="border-b border-gray-200 pb-4">
        <h1 class="text-2xl font-bold text-gray-900">收费标准</h1>
        <p class="text-gray-600">管理图书借阅收费标准</p>
      </div>

      <!-- 收费标准列表 -->
      <div class="card">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-lg font-bold text-gray-900">收费标准设置</h2>
          <div class="flex space-x-2">
            <button @click="initDefaultData" class="btn-secondary flex items-center">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
              </svg>
              初始化默认标准
            </button>
            <button @click="showForm = true" class="btn-primary flex items-center">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              新增标准
            </button>
          </div>
        </div>

        <div v-if="loading" class="text-center py-8">
          <div class="text-gray-500">加载中...</div>
        </div>

        <div v-else-if="feeStandards.length === 0" class="text-center py-8 text-gray-500">
          <div class="mb-4">暂无收费标准</div>
          <button @click="initDefaultData" class="btn-primary">
            初始化默认收费标准
          </button>
        </div>

        <div v-else class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200 border border-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="table-header border-r border-gray-200">收费类型</th>
                <th class="table-header border-r border-gray-200">书籍分类</th>
                <th class="table-header border-r border-gray-200">费用金额</th>
                <th class="table-header border-r border-gray-200">计费单位</th>
                <th class="table-header border-r border-gray-200">描述</th>
                <th class="table-header border-r border-gray-200">状态</th>
                <th class="table-header">操作</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="standard in feeStandards" :key="standard.feeId" class="hover:bg-gray-50 transition-colors">
                <td class="table-cell font-medium border-r border-gray-200">
                  <span class="px-2 py-1 text-xs font-medium rounded-full"
                        :class="getFeeTypeClass(standard.feeType)">
                    {{ getFeeTypeName(standard.feeType) }}
                  </span>
                </td>
                <td class="table-cell border-r border-gray-200">
                  {{ standard.bookCategory || '通用' }}
                </td>
                <td class="table-cell border-r border-gray-200">
                  ¥{{ standard.feeAmount }}
                </td>
                <td class="table-cell border-r border-gray-200">
                  {{ getBillingUnitName(standard.billingUnit) }}
                </td>
                <td class="table-cell border-r border-gray-200">
                  {{ standard.description }}
                </td>
                <td class="table-cell border-r border-gray-200">
                  <span :class="standard.isActive ? 'px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded-full' : 'px-2 py-1 text-xs font-medium bg-gray-100 text-gray-800 rounded-full'">
                    {{ standard.isActive ? '启用' : '禁用' }}
                  </span>
                </td>
                <td class="table-cell">
                  <div class="flex space-x-2">
                    <button @click="editStandard(standard)" class="text-blue-600 hover:text-blue-700 text-sm font-medium px-2 py-1 rounded hover:bg-blue-50 transition-colors">
                      编辑
                    </button>
                    <button @click="toggleStatus(standard)" :class="standard.isActive ? 'text-red-600 hover:text-red-700 text-sm font-medium px-2 py-1 rounded hover:bg-red-50 transition-colors' : 'text-green-600 hover:text-green-700 text-sm font-medium px-2 py-1 rounded hover:bg-green-50 transition-colors'">
                      {{ standard.isActive ? '禁用' : '启用' }}
                    </button>
                    <button @click="deleteStandard(standard)" class="text-red-600 hover:text-red-700 text-sm font-medium px-2 py-1 rounded hover:bg-red-50 transition-colors">
                      删除
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 表单弹窗 -->
      <div v-if="showForm" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
          <div class="mb-4">
            <h3 class="text-lg font-bold text-gray-900">{{ editingStandard ? '编辑收费标准' : '新增收费标准' }}</h3>
          </div>

          <form @submit.prevent="saveStandard" class="space-y-4">
            <div>
              <label class="form-label">收费类型</label>
              <select v-model="form.feeType" class="form-input" required>
                <option value="1">借阅费</option>
                <option value="2">超期罚款</option>
                <option value="3">损坏赔偿</option>
                <option value="4">丢失赔偿</option>
              </select>
            </div>

            <div>
              <label class="form-label">书籍分类</label>
              <select v-model="form.bookCategory" class="form-input">
                <option value="">通用（所有分类）</option>
                <option value="小说">小说</option>
                <option value="教材">教材</option>
                <option value="计算机">计算机</option>
                <option value="科幻">科幻</option>
                <option value="历史">历史</option>
                <option value="科学">科学</option>
                <option value="经济">经济</option>
                <option value="管理">管理</option>
                <option value="艺术">艺术</option>
              </select>
            </div>

            <div>
              <label class="form-label">费用金额</label>
              <input v-model.number="form.feeAmount" type="number" step="0.01" class="form-input" required />
            </div>

            <div>
              <label class="form-label">计费单位</label>
              <select v-model="form.billingUnit" class="form-input" required>
                <option value="1">按天</option>
                <option value="2">按本</option>
                <option value="3">按次</option>
                <option value="4">固定金额</option>
              </select>
            </div>

            <div>
              <label class="form-label">描述</label>
              <textarea v-model="form.description" class="form-input" rows="3" required></textarea>
            </div>

            <div class="flex items-center">
              <input v-model="form.isActive" type="checkbox" class="mr-2" />
              <label class="text-sm text-gray-700">启用此标准</label>
            </div>

            <div class="flex justify-end space-x-2 pt-4">
              <button type="button" @click="cancelForm" class="btn-secondary">取消</button>
              <button type="submit" class="btn-primary" :disabled="submitting">
                {{ submitting ? '保存中...' : '保存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '@/api'

const feeStandards = ref([])
const loading = ref(false)
const submitting = ref(false)
const showForm = ref(false)
const editingStandard = ref(null)

const form = ref({
  feeType: 1,
  bookCategory: '',
  feeAmount: 0,
  billingUnit: 1,
  description: '',
  isActive: true
})

onMounted(async () => {
  await loadFeeStandards()
})

const loadFeeStandards = async () => {
  loading.value = true
  try {
    const response = await admin.getFeeStandards()
    console.log('Fee standards response:', response)
    
    // 处理不同的响应格式
    if (response.code === 200 && response.data) {
      if (response.data.records) {
        feeStandards.value = response.data.records
      } else if (Array.isArray(response.data)) {
        feeStandards.value = response.data
      } else {
        feeStandards.value = []
      }
    } else if (response.success && response.data) {
      if (response.data.records) {
        feeStandards.value = response.data.records
      } else if (Array.isArray(response.data)) {
        feeStandards.value = response.data
      } else {
        feeStandards.value = []
      }
    } else if (Array.isArray(response)) {
      feeStandards.value = response
    } else {
      feeStandards.value = []
    }
  } catch (error) {
    console.error('Failed to load fee standards:', error)
    feeStandards.value = []
  } finally {
    loading.value = false
  }
}

const initDefaultData = async () => {
  try {
    const response = await admin.initFeeStandards()
    console.log('Init response:', response)
    await loadFeeStandards()
  } catch (error) {
    console.error('Failed to init fee standards:', error)
    alert('初始化失败，请检查后端服务')
  }
}

const editStandard = (standard) => {
  editingStandard.value = standard
  form.value = { 
    feeType: standard.feeType,
    bookCategory: standard.bookCategory || '',
    feeAmount: standard.feeAmount,
    billingUnit: standard.billingUnit,
    description: standard.description,
    isActive: standard.isActive === 1
  }
  showForm.value = true
}

const saveStandard = async () => {
  submitting.value = true
  try {
    const submitData = {
      ...form.value,
      isActive: form.value.isActive ? 1 : 0
    }
    
    if (editingStandard.value) {
      await admin.updateFeeStandard(editingStandard.value.feeId, submitData)
    } else {
      await admin.createFeeStandard(submitData)
    }
    await loadFeeStandards()
    cancelForm()
  } catch (error) {
    console.error('Failed to save fee standard:', error)
    alert('保存失败，请检查输入数据')
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (standard) => {
  try {
    const updateData = {
      ...standard,
      isActive: standard.isActive === 1 ? 0 : 1
    }
    await admin.updateFeeStandard(standard.feeId, updateData)
    await loadFeeStandards()
  } catch (error) {
    console.error('Failed to toggle status:', error)
    alert('状态更新失败')
  }
}

const deleteStandard = async (standard) => {
  if (confirm('确定要删除这个收费标准吗？')) {
    try {
      await admin.deleteFeeStandard(standard.feeId)
      await loadFeeStandards()
    } catch (error) {
      console.error('Failed to delete fee standard:', error)
      alert('删除失败')
    }
  }
}

const cancelForm = () => {
  showForm.value = false
  editingStandard.value = null
  form.value = {
    feeType: 1,
    bookCategory: '',
    feeAmount: 0,
    billingUnit: 1,
    description: '',
    isActive: true
  }
}

const getFeeTypeName = (feeType) => {
  const types = {
    1: '借阅费',
    2: '超期罚款',
    3: '损坏赔偿',
    4: '丢失赔偿'
  }
  return types[feeType] || '未知'
}

const getFeeTypeClass = (feeType) => {
  const classes = {
    1: 'bg-blue-100 text-blue-800',
    2: 'bg-yellow-100 text-yellow-800',
    3: 'bg-orange-100 text-orange-800',
    4: 'bg-red-100 text-red-800'
  }
  return classes[feeType] || 'bg-gray-100 text-gray-800'
}

const getBillingUnitName = (billingUnit) => {
  const units = {
    1: '按天',
    2: '按本',
    3: '按次',
    4: '固定金额'
  }
  return units[billingUnit] || '未知'
}
</script> 