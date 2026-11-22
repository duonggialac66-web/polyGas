<template>
  <EmployeeLayout :wide="true">
    <!-- Header Section (Corporate Enterprise) -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-slate-900"></span>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">Quản Lý Nhân Sự & Phân Quyền</h2>
          <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-slate-100 text-slate-700 border border-slate-200">Nhân Sự</span>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Quản lý hồ sơ nhân viên giao vận, phân quyền tài khoản và trạng thái hoạt động</p>
      </div>

      <div class="flex items-center gap-2">
        <button 
          @click="openCreateModal"
          class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          <span>Thêm Nhân Viên</span>
        </button>
      </div>
    </div>

    <!-- 4 KPI Summary Cards -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-3.5 mb-6">
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-1">Tổng Nhân Sự</div>
        <div class="text-2xl font-bold font-mono text-slate-900">{{ users.length }} <span class="text-xs font-normal text-slate-400">người</span></div>
        <div class="text-[10px] text-slate-400 mt-1">Toàn bộ nhân sự trong hệ thống</div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-emerald-700 uppercase mb-1">Đang Hoạt Động</div>
        <div class="text-2xl font-bold font-mono text-emerald-700">
          {{ activeCount }} <span class="text-xs font-normal text-slate-400">người</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Tài khoản ACTIVE được phép đăng nhập</div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-600 uppercase mb-1">Tài Xế / Shipper</div>
        <div class="text-2xl font-bold font-mono text-slate-900">
          {{ shipperCount }} <span class="text-xs font-normal text-slate-400">nhân sự</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Đội ngũ tác nghiệp ngoài tuyến</div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs" :class="inactiveCount > 0 ? 'border-amber-300 bg-amber-50/15' : ''">
        <div class="text-[11px] font-semibold tracking-wider uppercase mb-1" :class="inactiveCount > 0 ? 'text-amber-700' : 'text-slate-500'">
          Tài Khoản Tạm Khóa
        </div>
        <div class="text-2xl font-bold font-mono" :class="inactiveCount > 0 ? 'text-amber-700' : 'text-slate-900'">
          {{ inactiveCount }} <span class="text-xs font-normal text-slate-400">tài khoản</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Ngừng quyền truy cập hệ thống</div>
      </div>
    </div>

    <!-- Filter & Search Bar -->
    <div class="bg-white p-3 border border-slate-200 rounded-xl mb-4 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between">
      <div class="relative flex-1">
        <input 
          type="text" 
          v-model="keyword" 
          @input="fetchUsers"
          placeholder="Tìm theo họ tên, username, số điện thoại..." 
          class="w-full pl-9 pr-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
        />
        <svg class="w-4 h-4 text-slate-400 absolute left-2.5 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>

      <div class="flex items-center gap-2">
        <select 
          v-model="selectedRole" 
          @change="fetchUsers"
          class="px-2.5 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
        >
          <option value="">Tất cả vai trò</option>
          <option value="ROLE_ADMIN">Quản Trị Viên (ADMIN)</option>
          <option value="ROLE_EMPLOYEE">Nhân Viên Giao Gas (EMPLOYEE)</option>
        </select>

        <select 
          v-model="selectedStatus" 
          @change="fetchUsers"
          class="px-2.5 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="ACTIVE">Hoạt động (ACTIVE)</option>
          <option value="INACTIVE">Tạm khóa (INACTIVE)</option>
        </select>
      </div>
    </div>

    <!-- Data Table -->
    <div class="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-2xs mb-6">
      <div v-if="loading" class="p-8 text-center text-xs text-slate-500">
        Đang tải danh sách nhân sự...
      </div>

      <div v-else-if="users.length === 0" class="p-8 text-center text-xs text-slate-500">
        Không tìm thấy nhân viên nào phù hợp.
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
              <th class="py-2.5 px-4">Nhân Viên</th>
              <th class="py-2.5 px-4">Tên Đăng Nhập</th>
              <th class="py-2.5 px-4">Số Điện Thoại</th>
              <th class="py-2.5 px-4 text-center">Vai Trò</th>
              <th class="py-2.5 px-4 text-right">Lương Cơ Bản</th>
              <th class="py-2.5 px-4 text-center">Trạng Thái</th>
              <th class="py-2.5 px-4 text-right">Thao Tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="u in users" :key="u.id" class="hover:bg-slate-50/70 transition-colors">
              <td class="py-3 px-4">
                <div class="font-bold text-slate-900">{{ u.name }}</div>
                <div class="text-[10px] text-slate-400">Vào làm: {{ u.startDate || '—' }}</div>
              </td>
              <td class="py-3 px-4 font-mono font-semibold text-slate-700">
                {{ u.username }}
              </td>
              <td class="py-3 px-4 font-mono text-slate-600">
                {{ u.phone || '—' }}
              </td>
              <td class="py-3 px-4 text-center">
                <span 
                  class="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold"
                  :class="u.role === 'ROLE_ADMIN' || u.role === 'ADMIN' ? 'bg-indigo-50 text-indigo-700 border border-indigo-200' : 'bg-slate-100 text-slate-700 border border-slate-200'"
                >
                  {{ u.role === 'ROLE_ADMIN' || u.role === 'ADMIN' ? 'ADMIN' : 'SHIPPER' }}
                </span>
              </td>
              <td class="py-3 px-4 text-right font-mono font-semibold text-slate-800">
                {{ formatCurrency(u.baseSalary) }}
              </td>
              <td class="py-3 px-4 text-center">
                <button 
                  @click="handleToggleStatus(u)"
                  class="px-2 py-0.5 rounded text-[10px] font-bold border transition-colors"
                  :class="u.status === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100' : 'bg-rose-50 text-rose-700 border-rose-200 hover:bg-rose-100'"
                  title="Nhấn để đổi trạng thái khóa/mở"
                >
                  {{ u.status === 'ACTIVE' ? 'HOẠT ĐỘNG' : 'TẠM KHÓA' }}
                </button>
              </td>
              <td class="py-3 px-4 text-right whitespace-nowrap">
                <button 
                  @click="openEditModal(u)"
                  class="text-slate-600 hover:text-slate-900 font-semibold px-2 py-1 rounded hover:bg-slate-100 transition-colors mr-1"
                >
                  Sửa
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Thêm / Sửa Nhân Viên -->
    <div 
      v-if="modal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs"
    >
      <div class="bg-white rounded-xl shadow-xl border border-slate-200 max-w-md w-full p-6 overflow-hidden">
        <div class="flex items-center justify-between pb-3 border-b border-slate-100 mb-4">
          <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight">
            {{ modal.isEdit ? 'Cập Nhật Hồ Sơ Nhân Viên' : 'Thêm Nhân Viên Mới' }}
          </h3>
          <button @click="closeModal" class="text-slate-400 hover:text-slate-600 text-lg leading-none">
            ✕
          </button>
        </div>

        <div v-if="modal.error" class="mb-4 p-2.5 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg">
          {{ modal.error }}
        </div>

        <form @submit.prevent="submitForm" class="space-y-3 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Họ và Tên *</label>
            <input 
              type="text" 
              v-model="form.name" 
              required
              placeholder="VD: Nguyễn Văn Giao"
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            />
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Tên Đăng Nhập *</label>
              <input 
                type="text" 
                v-model="form.username" 
                required
                :disabled="modal.isEdit"
                placeholder="VD: shipper02"
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors disabled:opacity-60"
              />
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">{{ modal.isEdit ? 'Mật khẩu mới (bỏ trống nếu giữ nguyên)' : 'Mật Khẩu *' }}</label>
              <input 
                type="password" 
                v-model="form.password" 
                :required="!modal.isEdit"
                placeholder="Tối thiểu 6 ký tự"
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Số Điện Thoại</label>
              <input 
                type="text" 
                v-model="form.phone" 
                placeholder="VD: 0901234567"
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              />
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Vai Trò Hệ Thống *</label>
              <select 
                v-model="form.role" 
                required
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors font-medium"
              >
                <option value="EMPLOYEE">Nhân Viên Giao Gas (EMPLOYEE)</option>
                <option value="ADMIN">Quản Trị Viên (ADMIN)</option>
              </select>
            </div>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Lương Cơ Bản (VNĐ/Tháng)</label>
            <input 
              type="number" 
              v-model.number="form.baseSalary" 
              min="0"
              step="500000"
              placeholder="VD: 8000000"
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            />
          </div>

          <div class="flex items-center justify-end gap-2 pt-3 border-t border-slate-100">
            <button 
              type="button" 
              @click="closeModal"
              class="px-3.5 py-1.5 text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-lg font-semibold transition-colors"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="modal.submitting"
              class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 disabled:opacity-50 text-white rounded-lg font-semibold shadow-2xs transition-colors"
            >
              {{ modal.submitting ? 'Đang lưu...' : (modal.isEdit ? 'Lưu Thay Đổi' : 'Tạo Tài Khoản') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import apiClient from '../services/api.js'

const users = ref([])
const loading = ref(false)
const keyword = ref('')
const selectedRole = ref('')
const selectedStatus = ref('')

const modal = reactive({
  isOpen: false,
  isEdit: false,
  editId: null,
  submitting: false,
  error: ''
})

const form = reactive({
  name: '',
  username: '',
  password: '',
  phone: '',
  role: 'EMPLOYEE',
  baseSalary: 7000000
})

const activeCount = computed(() => users.value.filter(u => u.status === 'ACTIVE').length)
const inactiveCount = computed(() => users.value.filter(u => u.status === 'INACTIVE').length)
const shipperCount = computed(() => users.value.filter(u => u.role === 'ROLE_EMPLOYEE' || u.role === 'EMPLOYEE').length)

function formatCurrency(amount) {
  if (amount === undefined || amount === null) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

async function fetchUsers() {
  loading.value = true
  try {
    const params = {}
    if (keyword.value) params.keyword = keyword.value
    if (selectedRole.value) params.role = selectedRole.value
    if (selectedStatus.value) params.status = selectedStatus.value

    const res = await apiClient.get('/users', { params })
    users.value = res.data || res || []
  } catch (err) {
    console.error('Lỗi tải danh sách người dùng:', err)
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  modal.isOpen = true
  modal.isEdit = false
  modal.editId = null
  modal.error = ''
  form.name = ''
  form.username = ''
  form.password = ''
  form.phone = ''
  form.role = 'EMPLOYEE'
  form.baseSalary = 7500000
}

function openEditModal(u) {
  modal.isOpen = true
  modal.isEdit = true
  modal.editId = u.id
  modal.error = ''
  form.name = u.name
  form.username = u.username
  form.password = ''
  form.phone = u.phone || ''
  form.role = (u.role === 'ROLE_ADMIN' || u.role === 'ADMIN') ? 'ADMIN' : 'EMPLOYEE'
  form.baseSalary = u.baseSalary || 7000000
}

function closeModal() {
  modal.isOpen = false
  modal.error = ''
}

async function submitForm() {
  modal.error = ''
  modal.submitting = true
  try {
    const payload = {
      name: form.name,
      username: form.username,
      phone: form.phone,
      role: form.role,
      baseSalary: form.baseSalary
    }
    if (form.password) {
      payload.password = form.password
    }

    if (modal.isEdit) {
      await apiClient.put(`/users/${modal.editId}`, payload)
    } else {
      await apiClient.post('/users', payload)
    }

    closeModal()
    await fetchUsers()
  } catch (err) {
    modal.error = err.message || 'Thao tác không thành công!'
  } finally {
    modal.submitting = false
  }
}

async function handleToggleStatus(u) {
  const nextStatus = u.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const actionName = nextStatus === 'ACTIVE' ? 'MỞ KHÓA' : 'TẠM KHÓA'
  if (confirm(`Bạn có chắc chắn muốn ${actionName} tài khoản [${u.username}]?`)) {
    try {
      await apiClient.patch(`/users/${u.id}/toggle-status`)
      await fetchUsers()
    } catch (err) {
      alert('Lỗi đổi trạng thái: ' + err.message)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>
