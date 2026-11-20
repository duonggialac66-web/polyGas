<template>
  <AppLayout :is-backend-connected="!isMockData" @refresh="fetchEmployees">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">
        <h2>👥 Quản Lý Nhân Viên (Employment)</h2>
        <p>Quản lý hồ sơ nhân sự, tài khoản hệ thống, chức vụ và mức lương cơ sở của OriginGas</p>
      </div>
      <div>
        <button class="btn btn-primary" @click="openCreateModal">
          <span>➕</span>
          <span>Thêm Nhân Viên Mới</span>
        </button>
      </div>
    </div>

    <!-- Alert Banner when in Mock mode -->
    <div 
      v-if="isMockData" 
      style="background: #fffbeb; border: 1px solid #fde68a; border-radius: 8px; padding: 0.75rem 1rem; margin-bottom: 1.25rem; display: flex; align-items: center; justify-content: space-between;"
    >
      <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; color: #92400e;">
        <span>ℹ️</span>
        <span>
          <strong>Lưu ý:</strong> Backend Spring Boot (cổng 8080) hiện chưa kết nối. Giao diện đang hiển thị dữ liệu mẫu để thử nghiệm. Khi khởi động Backend, dữ liệu thực sẽ tự động cập nhật!
        </span>
      </div>
      <button class="btn btn-sm btn-outline" @click="fetchEmployees">Thử kết nối lại</button>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon primary">👥</div>
        <div class="stat-info">
          <p>Tổng Nhân Sự</p>
          <h3>{{ stats.total }}</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon success">✅</div>
        <div class="stat-info">
          <p>Đang Hoạt Động</p>
          <h3>{{ stats.active }}</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon warning">⏸️</div>
        <div class="stat-info">
          <p>Tạm Ngừng / Khóa</p>
          <h3>{{ stats.inactive }}</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon danger">💵</div>
        <div class="stat-info">
          <p>Tổng Quỹ Lương Cơ Sở</p>
          <h3>{{ formatCurrency(stats.totalSalary) }}</h3>
        </div>
      </div>
    </div>

    <!-- Table Content Card -->
    <div class="content-card">
      <!-- Toolbar Filter & Search -->
      <div class="card-toolbar">
        <!-- Search Input -->
        <div class="search-input-group">
          <span class="search-icon">🔍</span>
          <input 
            type="text" 
            v-model="filters.keyword" 
            @input="handleSearchDebounce"
            placeholder="Tìm theo tên, username, số ĐT..."
          />
        </div>

        <!-- Filter Selects -->
        <div class="toolbar-filters">
          <!-- Role Filter -->
          <select v-model="filters.role" @change="fetchEmployees" class="select-filter">
            <option value="">-- Tất cả chức vụ --</option>
            <option value="ADMIN">Quản Trị Viên (ADMIN)</option>
            <option value="EMPLOYEE">Nhân Viên Giao Hàng (EMPLOYEE)</option>
          </select>

          <!-- Status Filter -->
          <select v-model="filters.status" @change="fetchEmployees" class="select-filter">
            <option value="">-- Tất cả trạng thái --</option>
            <option value="ACTIVE">Đang hoạt động</option>
            <option value="INACTIVE">Đã tạm khóa</option>
          </select>

          <!-- Clear Filters -->
          <button 
            v-if="filters.keyword || filters.role || filters.status" 
            class="btn btn-outline btn-sm"
            @click="resetFilters"
            title="Xóa bộ lọc"
          >
            Reset bộ lọc
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="empty-state">
        <div class="loading-spinner"></div>
        <p style="margin-top: 0.75rem; font-size: 0.9rem;">Đang tải danh sách nhân viên...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="employees.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <h4 style="font-weight: 700; color: #334155;">Không tìm thấy nhân viên nào</h4>
        <p style="font-size: 0.85rem; margin-top: 0.25rem;">Hãy thử tìm kiếm với từ khóa khác hoặc tạo nhân viên mới</p>
      </div>

      <!-- Table -->
      <div v-else class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>Nhân Viên</th>
              <th>Số Điện Thoại</th>
              <th>Chức Vụ / Vai Trò</th>
              <th>Ngày Vào Làm</th>
              <th>Lương Cơ Sở</th>
              <th>Trạng Thái</th>
              <th style="text-align: right;">Hành Động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="emp in employees" :key="emp.id">
              <!-- Name & Username -->
              <td>
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                  <div class="user-avatar-sm" style="font-size: 0.85rem; background: #e0f2fe; color: #0284c7;">
                    {{ getInitials(emp.name) }}
                  </div>
                  <div>
                    <div style="font-weight: 700; color: #0f172a;">{{ emp.name }}</div>
                    <div style="font-size: 0.75rem; color: #64748b;">@{{ emp.username }}</div>
                  </div>
                </div>
              </td>

              <!-- Phone -->
              <td>
                <span style="font-weight: 500; color: #334155;">
                  {{ emp.phone || '—' }}
                </span>
              </td>

              <!-- Role -->
              <td>
                <span :class="['badge', emp.role === 'ADMIN' ? 'badge-admin' : 'badge-employee']">
                  {{ emp.role === 'ADMIN' ? '👑 Quản Trị' : '🚚 Giao Hàng' }}
                </span>
              </td>

              <!-- Start Date -->
              <td>
                <span style="color: #64748b; font-size: 0.85rem;">
                  {{ formatDate(emp.startDate) }}
                </span>
              </td>

              <!-- Base Salary -->
              <td>
                <span style="font-weight: 700; color: #0f172a;">
                  {{ formatCurrency(emp.baseSalary) }}
                </span>
              </td>

              <!-- Status -->
              <td>
                <span :class="['badge', emp.status === 'ACTIVE' ? 'badge-active' : 'badge-inactive']">
                  {{ emp.status === 'ACTIVE' ? '● Hoạt Động' : '● Đã Khóa' }}
                </span>
              </td>

              <!-- Actions -->
              <td style="text-align: right;">
                <div style="display: inline-flex; gap: 0.35rem;">
                  <!-- Edit Button -->
                  <button 
                    class="btn btn-outline btn-sm" 
                    title="Chỉnh sửa thông tin"
                    @click="openEditModal(emp)"
                  >
                    ✏️ Sửa
                  </button>

                  <!-- Toggle Status Button -->
                  <button 
                    :class="['btn', 'btn-sm', emp.status === 'ACTIVE' ? 'btn-outline' : 'btn-primary']"
                    :title="emp.status === 'ACTIVE' ? 'Khóa tài khoản' : 'Mở khóa tài khoản'"
                    @click="handleToggleStatus(emp)"
                  >
                    {{ emp.status === 'ACTIVE' ? '🔒 Khóa' : '🔓 Mở' }}
                  </button>

                  <!-- Delete Button -->
                  <button 
                    class="btn btn-danger-outline btn-sm" 
                    title="Xóa nhân viên"
                    @click="handleDelete(emp)"
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create / Edit Employee Modal -->
    <div v-if="modal.isOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ modal.isEdit ? 'Chỉnh Sửa Thông Tin Nhân Viên' : 'Thêm Nhân Viên Mới' }}</h3>
          <button class="btn-icon" @click="closeModal">✕</button>
        </div>

        <form @submit.prevent="handleSubmitForm">
          <div class="modal-body">
            <!-- Full Name -->
            <div class="form-group">
              <label>Họ và Tên <span style="color: var(--danger);">*</span></label>
              <input 
                type="text" 
                v-model="form.name" 
                class="form-control" 
                placeholder="Nhập họ và tên đầy đủ..." 
                required 
              />
            </div>

            <!-- Username & Password Row -->
            <div class="form-row">
              <div class="form-group">
                <label>Tên Đăng Nhập <span style="color: var(--danger);">*</span></label>
                <input 
                  type="text" 
                  v-model="form.username" 
                  :disabled="modal.isEdit"
                  class="form-control" 
                  placeholder="VD: nguyenvana" 
                  required 
                />
              </div>

              <div class="form-group">
                <label>
                  Mật Khẩu 
                  <span v-if="!modal.isEdit" style="color: var(--danger);">*</span>
                  <span v-else style="font-size: 0.7rem; color: #64748b;">(để trống nếu giữ nguyên)</span>
                </label>
                <input 
                  type="password" 
                  v-model="form.password" 
                  class="form-control" 
                  :placeholder="modal.isEdit ? '••••••••' : 'Nhập mật khẩu...'"
                  :required="!modal.isEdit" 
                />
              </div>
            </div>

            <!-- Role & Phone Row -->
            <div class="form-row">
              <div class="form-group">
                <label>Vai Trò / Phân Quyền</label>
                <select v-model="form.role" class="form-control">
                  <option value="EMPLOYEE">Nhân Viên Giao Hàng (EMPLOYEE)</option>
                  <option value="ADMIN">Quản Trị Viên (ADMIN)</option>
                </select>
              </div>

              <div class="form-group">
                <label>Số Điện Thoại</label>
                <input 
                  type="text" 
                  v-model="form.phone" 
                  class="form-control" 
                  placeholder="VD: 0987654321" 
                />
              </div>
            </div>

            <!-- Start Date & Salary Row -->
            <div class="form-row">
              <div class="form-group">
                <label>Ngày Vào Làm</label>
                <input 
                  type="date" 
                  v-model="form.startDate" 
                  class="form-control" 
                />
              </div>

              <div class="form-group">
                <label>Lương Cơ Bản (VNĐ)</label>
                <input 
                  type="number" 
                  step="500000"
                  v-model.number="form.baseSalary" 
                  class="form-control" 
                  placeholder="VD: 7000000" 
                />
              </div>
            </div>

            <!-- Status -->
            <div class="form-group">
              <label>Trạng Thái Tài Khoản</label>
              <select v-model="form.status" class="form-control">
                <option value="ACTIVE">Đang hoạt động (ACTIVE)</option>
                <option value="INACTIVE">Tạm dừng / Khóa (INACTIVE)</option>
              </select>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-outline" @click="closeModal">Hủy bỏ</button>
            <button type="submit" class="btn btn-primary" :disabled="formSubmitting">
              {{ formSubmitting ? 'Đang lưu...' : (modal.isEdit ? 'Lưu Thay Đổi' : 'Thêm Mới') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import AppLayout from '../components/layout/AppLayout.vue'
import { userService } from '../services/userService'

// State
const employees = ref([])
const loading = ref(false)
const isMockData = ref(false)
const formSubmitting = ref(false)

const filters = reactive({
  keyword: '',
  role: '',
  status: ''
})

const modal = reactive({
  isOpen: false,
  isEdit: false,
  currentId: null
})

const form = reactive({
  name: '',
  username: '',
  password: '',
  role: 'EMPLOYEE',
  phone: '',
  startDate: '',
  baseSalary: 7000000,
  status: 'ACTIVE'
})

// Computed Statistics
const stats = computed(() => {
  const total = employees.value.length
  const active = employees.value.filter(e => e.status === 'ACTIVE').length
  const inactive = total - active
  const totalSalary = employees.value.reduce((sum, e) => sum + (e.baseSalary || 0), 0)
  return { total, active, inactive, totalSalary }
})

// Methods
const fetchEmployees = async () => {
  loading.value = true
  try {
    const res = await userService.getUsers({
      keyword: filters.keyword,
      role: filters.role,
      status: filters.status
    })
    employees.value = res.data || []
    isMockData.value = !!res.isMock
  } catch (error) {
    console.error('Không thể tải nhân viên:', error)
  } finally {
    loading.value = false
  }
}

let debounceTimer = null
const handleSearchDebounce = () => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    fetchEmployees()
  }, 300)
}

const resetFilters = () => {
  filters.keyword = ''
  filters.role = ''
  filters.status = ''
  fetchEmployees()
}

// Modal controls
const openCreateModal = () => {
  modal.isOpen = true
  modal.isEdit = false
  modal.currentId = null
  Object.assign(form, {
    name: '',
    username: '',
    password: '',
    role: 'EMPLOYEE',
    phone: '',
    startDate: new Date().toISOString().split('T')[0],
    baseSalary: 7000000,
    status: 'ACTIVE'
  })
}

const openEditModal = (emp) => {
  modal.isOpen = true
  modal.isEdit = true
  modal.currentId = emp.id
  Object.assign(form, {
    name: emp.name,
    username: emp.username,
    password: '',
    role: emp.role,
    phone: emp.phone || '',
    startDate: emp.startDate || '',
    baseSalary: emp.baseSalary || 0,
    status: emp.status
  })
}

const closeModal = () => {
  modal.isOpen = false
}

const handleSubmitForm = async () => {
  formSubmitting.value = true
  try {
    if (modal.isEdit) {
      await userService.updateUser(modal.currentId, { ...form })
      alert('Đã cập nhật thông tin nhân viên thành công!')
    } else {
      await userService.createUser({ ...form })
      alert('Đã thêm mới nhân viên thành công!')
    }
    closeModal()
    fetchEmployees()
  } catch (err) {
    alert('Thao tác thất bại: ' + err.message)
  } finally {
    formSubmitting.value = false
  }
}

const handleToggleStatus = async (emp) => {
  const actionText = emp.status === 'ACTIVE' ? 'khóa' : 'mở khóa'
  if (confirm(`Bạn có chắc chắn muốn ${actionText} tài khoản của ${emp.name}?`)) {
    try {
      await userService.toggleStatus(emp.id)
      fetchEmployees()
    } catch (err) {
      alert('Không thể cập nhật trạng thái: ' + err.message)
    }
  }
}

const handleDelete = async (emp) => {
  if (confirm(`Xác nhận xóa vĩnh viễn nhân viên [${emp.name}] khỏi hệ thống?`)) {
    try {
      await userService.deleteUser(emp.id)
      fetchEmployees()
    } catch (err) {
      alert('Không thể xóa nhân viên: ' + err.message)
    }
  }
}

// Formatters
const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  return isNaN(d.getTime()) ? dateStr : d.toLocaleDateString('vi-VN')
}

const getInitials = (name) => {
  if (!name) return 'NV'
  const parts = name.trim().split(' ')
  if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

onMounted(() => {
  fetchEmployees()
})
</script>

