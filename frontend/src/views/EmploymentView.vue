<template>
  <AppLayout :is-backend-connected="!isMockData" @refresh="fetchReports">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">
        <h2>🚚 Báo Cáo Giao Gas Hôm Nay</h2>
        <p>Nhật ký ghi nhận số lượng bình giao, giá bán và theo dõi công nợ khách hàng trong ngày của nhân viên</p>
      </div>
      <div style="display: flex; gap: 0.75rem; align-items: center;">
        <!-- Date Selector -->
        <input 
          type="date" 
          v-model="selectedDate" 
          @change="fetchReports"
          class="form-control" 
          style="width: auto; font-weight: 600;"
        />

        <button class="btn btn-primary" @click="openCreateModal">
          <span>➕</span>
          <span>Nhập Phiếu Giao Gas</span>
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
          <strong>Lưu ý:</strong> Backend Spring Boot (cổng 8080) chưa kết nối. Giao diện đang dùng Mock Data để bạn thử nghiệm đầy đủ tính năng tạo đơn, tính tiền, đổi nợ/thanh toán.
        </span>
      </div>
      <button class="btn btn-sm btn-outline" @click="fetchReports">Thử kết nối lại</button>
    </div>

    <!-- Stats Grid (Tổng hợp ca làm việc hôm nay) -->
    <div class="stats-grid">
      <!-- 1. Total Cylinders Delivered -->
      <div class="stat-card">
        <div class="stat-icon primary">🛢️</div>
        <div class="stat-info">
          <p>Tổng Bình Giao Hôm Nay</p>
          <h3>{{ stats.totalQuantity }} bình</h3>
        </div>
      </div>

      <!-- 2. Total Revenue -->
      <div class="stat-card">
        <div class="stat-icon info">💵</div>
        <div class="stat-info">
          <p>Tổng Tiền Bán Được</p>
          <h3>{{ formatCurrency(stats.totalAmount) }}</h3>
        </div>
      </div>

      <!-- 3. Paid Amount -->
      <div class="stat-card">
        <div class="stat-icon success">✅</div>
        <div class="stat-info">
          <p>Đã Thu Tiền (Thanh Toán)</p>
          <h3>{{ formatCurrency(stats.paidAmount) }}</h3>
        </div>
      </div>

      <!-- 4. Debt Amount -->
      <div class="stat-card">
        <div class="stat-icon warning">⚠️</div>
        <div class="stat-info">
          <p>Khách Còn Nợ (Ghi Nợ)</p>
          <h3 style="color: var(--danger);">{{ formatCurrency(stats.debtAmount) }}</h3>
        </div>
      </div>
    </div>

    <!-- Main Content Card -->
    <div class="content-card">
      <!-- Toolbar Filter & Search -->
      <div class="card-toolbar">
        <div class="search-input-group">
          <span class="search-icon">🔍</span>
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="Tìm theo tên khách hàng, ghi chú..."
          />
        </div>

        <div class="toolbar-filters">
          <!-- Filter by Payment Status -->
          <select v-model="filterStatus" class="select-filter">
            <option value="">-- Tất cả trạng thái --</option>
            <option value="PAID">Đã thanh toán</option>
            <option value="DEBT">Ghi nợ</option>
          </select>

          <!-- Filter by Container Type -->
          <select v-model="filterContainer" class="select-filter">
            <option value="">-- Tất cả loại bình --</option>
            <option v-for="g in GAS_TYPES" :key="g.type" :value="g.type">
              {{ g.type }}
            </option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="empty-state">
        <div class="loading-spinner"></div>
        <p style="margin-top: 0.75rem; font-size: 0.9rem;">Đang tải danh sách giao gas...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredReports.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h4 style="font-weight: 700; color: #334155;">Chưa có phiếu giao gas nào hôm nay</h4>
        <p style="font-size: 0.85rem; margin-top: 0.25rem;">Hãy bấm nút <strong>"Nhập Phiếu Giao Gas"</strong> để báo cáo chuyến giao đầu tiên trong ngày!</p>
        <button class="btn btn-primary" style="margin-top: 1rem;" @click="openCreateModal">
          ➕ Nhập Phiếu Đầu Tiên
        </button>
      </div>

      <!-- Table View -->
      <div v-else class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>Khách Hàng</th>
              <th>Loại Bình Gas</th>
              <th style="text-align: center;">Số Lượng</th>
              <th style="text-align: right;">Đơn Giá</th>
              <th style="text-align: right;">Thành Tiền</th>
              <th style="text-align: center;">Trạng Thái</th>
              <th>Ghi Chú</th>
              <th style="text-align: right;">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredReports" :key="item.id">
              <!-- Customer Name -->
              <td>
                <div style="font-weight: 700; color: #0f172a; font-size: 0.95rem;">
                  {{ item.customerName }}
                </div>
                <div style="font-size: 0.75rem; color: #64748b;">
                  Người lập: {{ item.employeeName || 'Nhân viên giao' }}
                </div>
              </td>

              <!-- Gas Container Type -->
              <td>
                <span class="badge" :style="getContainerBadgeStyle(item.containerType)">
                  🛢️ {{ item.containerType }}
                </span>
              </td>

              <!-- Quantity -->
              <td style="text-align: center;">
                <span style="font-weight: 700; font-size: 1rem; color: var(--primary);">
                  {{ item.quantity }}
                </span>
              </td>

              <!-- Unit Price -->
              <td style="text-align: right; color: #475569; font-weight: 500;">
                {{ formatCurrency(item.unitPrice) }}
              </td>

              <!-- Total Amount -->
              <td style="text-align: right; font-weight: 800; font-size: 1rem; color: #0f172a;">
                {{ formatCurrency(item.total) }}
              </td>

              <!-- Payment Status (Click to toggle) -->
              <td style="text-align: center;">
                <button 
                  :class="['badge', item.paymentStatus === 'PAID' ? 'badge-active' : 'badge-inactive']"
                  style="cursor: pointer; border: none; font-size: 0.8rem; padding: 0.35rem 0.75rem;"
                  :title="item.paymentStatus === 'PAID' ? 'Bấm để chuyển sang Nợ' : 'Bấm để đánh dấu Đã thanh toán'"
                  @click="handleTogglePayment(item)"
                >
                  <span v-if="item.paymentStatus === 'PAID'">✅ Đã thanh toán</span>
                  <span v-else>⚠️ Ghi nợ</span>
                </button>
              </td>

              <!-- Notes -->
              <td>
                <span style="color: #64748b; font-size: 0.85rem; font-style: italic;">
                  {{ item.notes || '—' }}
                </span>
              </td>

              <!-- Actions -->
              <td style="text-align: right;">
                <div style="display: inline-flex; gap: 0.35rem;">
                  <button 
                    class="btn btn-outline btn-sm" 
                    title="Chỉnh sửa đơn này"
                    @click="openEditModal(item)"
                  >
                    ✏️ Sửa
                  </button>
                  <button 
                    class="btn btn-danger-outline btn-sm" 
                    title="Xóa phiếu"
                    @click="handleDelete(item)"
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

    <!-- Create / Edit Modal -->
    <div v-if="modal.isOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ modal.isEdit ? 'Chỉnh Sửa Phiếu Giao Gas' : 'Nhập Phiếu Báo Cáo Giao Gas Mới' }}</h3>
          <button class="btn-icon" @click="closeModal">✕</button>
        </div>

        <form @submit.prevent="handleSubmitForm">
          <div class="modal-body">
            <!-- 1. Customer Name -->
            <div class="form-group">
              <label>Tên Khách Hàng <span style="color: var(--danger);">*</span></label>
              <input 
                type="text" 
                v-model="form.customerName" 
                class="form-control" 
                placeholder="VD: Quán Cơm Tấm 24, Cô Lan, Bún Bò Huế..." 
                required 
              />
            </div>

            <!-- 2. Container Type & Quantity -->
            <div class="form-row">
              <div class="form-group">
                <label>Loại Bình Gas <span style="color: var(--danger);">*</span></label>
                <select v-model="form.containerType" @change="onContainerTypeChange" class="form-control" required>
                  <option v-for="g in GAS_TYPES" :key="g.type" :value="g.type">
                    {{ g.type }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label>Số Lượng (Bình) <span style="color: var(--danger);">*</span></label>
                <input 
                  type="number" 
                  min="1" 
                  max="100"
                  v-model.number="form.quantity" 
                  class="form-control" 
                  required 
                />
              </div>
            </div>

            <!-- 3. Unit Price & Computed Total -->
            <div class="form-row">
              <div class="form-group">
                <label>Đơn Giá (VNĐ) <span style="color: var(--danger);">*</span></label>
                <input 
                  type="number" 
                  step="5000"
                  v-model.number="form.unitPrice" 
                  class="form-control" 
                  placeholder="350000"
                  required 
                />
              </div>

              <div class="form-group">
                <label>Thành Tiền (VNĐ)</label>
                <div 
                  class="form-control" 
                  style="background: #f1f5f9; font-weight: 800; color: #0f172a; display: flex; align-items: center;"
                >
                  {{ formatCurrency(computedTotal) }}
                </div>
              </div>
            </div>

            <!-- 4. Payment Status (Nợ / Đã thanh toán) -->
            <div class="form-group">
              <label>Trạng Thái Thanh Toán <span style="color: var(--danger);">*</span></label>
              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; margin-top: 0.25rem;">
                <label 
                  :style="{
                    padding: '0.75rem',
                    borderRadius: '8px',
                    border: form.paymentStatus === 'PAID' ? '2px solid #10b981' : '1px solid #cbd5e1',
                    background: form.paymentStatus === 'PAID' ? '#d1fae5' : '#ffffff',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem',
                    fontWeight: '700',
                    color: form.paymentStatus === 'PAID' ? '#065f46' : '#475569'
                  }"
                >
                  <input type="radio" value="PAID" v-model="form.paymentStatus" style="accent-color: #10b981;" />
                  <span>✅ Đã Thanh Toán</span>
                </label>

                <label 
                  :style="{
                    padding: '0.75rem',
                    borderRadius: '8px',
                    border: form.paymentStatus === 'DEBT' ? '2px solid #ef4444' : '1px solid #cbd5e1',
                    background: form.paymentStatus === 'DEBT' ? '#fee2e2' : '#ffffff',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem',
                    fontWeight: '700',
                    color: form.paymentStatus === 'DEBT' ? '#991b1b' : '#475569'
                  }"
                >
                  <input type="radio" value="DEBT" v-model="form.paymentStatus" style="accent-color: #ef4444;" />
                  <span>⚠️ Ghi Nợ (Chưa Trả)</span>
                </label>
              </div>
            </div>

            <!-- 5. Notes -->
            <div class="form-group">
              <label>Ghi Chú Giao Hàng</label>
              <textarea 
                v-model="form.notes" 
                class="form-control" 
                rows="2" 
                placeholder="VD: Giao lên lầu 3, khách đổi bình rỉ sét, hẹn 18h quay lại lấy tiền..."
              ></textarea>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-outline" @click="closeModal">Hủy bỏ</button>
            <button type="submit" class="btn btn-primary" :disabled="formSubmitting">
              {{ formSubmitting ? 'Đang lưu...' : (modal.isEdit ? 'Lưu Thay Đổi' : 'Lập Phiếu Giao') }}
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
import { deliveryReportService, GAS_TYPES } from '../services/deliveryReportService'

// State
const reports = ref([])
const loading = ref(false)
const isMockData = ref(false)
const formSubmitting = ref(false)
const selectedDate = ref(new Date().toISOString().split('T')[0])

// Filters
const searchKeyword = ref('')
const filterStatus = ref('')
const filterContainer = ref('')

// Modal state
const modal = reactive({
  isOpen: false,
  isEdit: false,
  currentId: null
})

// Form data
const form = reactive({
  customerName: '',
  containerType: 'Bình 12kg Xám',
  quantity: 1,
  unitPrice: 350000,
  paymentStatus: 'PAID',
  notes: ''
})

// Computed Total for Form
const computedTotal = computed(() => {
  const qty = Number(form.quantity) || 0
  const price = Number(form.unitPrice) || 0
  return qty * price
})

// Filtered reports
const filteredReports = computed(() => {
  return reports.value.filter(item => {
    // Keyword match
    if (searchKeyword.value.trim()) {
      const kw = searchKeyword.value.toLowerCase().trim()
      const matchName = item.customerName && item.customerName.toLowerCase().includes(kw)
      const matchNotes = item.notes && item.notes.toLowerCase().includes(kw)
      if (!matchName && !matchNotes) return false
    }

    // Payment status match
    if (filterStatus.value && item.paymentStatus !== filterStatus.value) {
      return false
    }

    // Container type match
    if (filterContainer.value && item.containerType !== filterContainer.value) {
      return false
    }

    return true
  })
})

// Statistics
const stats = computed(() => {
  const totalQuantity = reports.value.reduce((sum, r) => sum + (Number(r.quantity) || 0), 0)
  const totalAmount = reports.value.reduce((sum, r) => sum + (Number(r.total) || 0), 0)
  const paidAmount = reports.value
    .filter(r => r.paymentStatus === 'PAID')
    .reduce((sum, r) => sum + (Number(r.total) || 0), 0)
  const debtAmount = totalAmount - paidAmount

  return { totalQuantity, totalAmount, paidAmount, debtAmount }
})

// Methods
const fetchReports = async () => {
  loading.value = true
  try {
    const res = await deliveryReportService.getTodayReports(selectedDate.value)
    reports.value = res.data || []
    isMockData.value = !!res.isMock
  } catch (err) {
    console.error('Lỗi tải báo cáo giao gas:', err)
  } finally {
    loading.value = false
  }
}

const onContainerTypeChange = () => {
  const found = GAS_TYPES.find(g => g.type === form.containerType)
  if (found) {
    form.unitPrice = found.defaultPrice
  }
}

const openCreateModal = () => {
  modal.isOpen = true
  modal.isEdit = false
  modal.currentId = null
  Object.assign(form, {
    customerName: '',
    containerType: 'Bình 12kg Xám',
    quantity: 1,
    unitPrice: 350000,
    paymentStatus: 'PAID',
    notes: ''
  })
}

const openEditModal = (item) => {
  modal.isOpen = true
  modal.isEdit = true
  modal.currentId = item.id
  Object.assign(form, {
    customerName: item.customerName,
    containerType: item.containerType,
    quantity: item.quantity,
    unitPrice: item.unitPrice,
    paymentStatus: item.paymentStatus,
    notes: item.notes || ''
  })
}

const closeModal = () => {
  modal.isOpen = false
}

const handleSubmitForm = async () => {
  formSubmitting.value = true
  try {
    const payload = {
      ...form,
      total: computedTotal.value,
      reportDate: selectedDate.value
    }

    if (modal.isEdit) {
      await deliveryReportService.updateReport(modal.currentId, payload)
      alert('Đã cập nhật phiếu giao gas thành công!')
    } else {
      await deliveryReportService.createReport(payload)
      alert('Đã ghi nhận phiếu giao gas mới thành công!')
    }
    closeModal()
    fetchReports()
  } catch (err) {
    alert('Thao tác thất bại: ' + err.message)
  } finally {
    formSubmitting.value = false
  }
}

const handleTogglePayment = async (item) => {
  const nextStatusText = item.paymentStatus === 'PAID' ? 'Ghi Nợ' : 'Đã Thanh Toán'
  if (confirm(`Chuyển trạng thái đơn của [${item.customerName}] sang "${nextStatusText}"?`)) {
    try {
      await deliveryReportService.togglePaymentStatus(item.id)
      fetchReports()
    } catch (err) {
      alert('Lỗi cập nhật: ' + err.message)
    }
  }
}

const handleDelete = async (item) => {
  if (confirm(`Xác nhận xóa phiếu giao gas cho khách [${item.customerName}]?`)) {
    try {
      await deliveryReportService.deleteReport(item.id)
      fetchReports()
    } catch (err) {
      alert('Không thể xóa phiếu: ' + err.message)
    }
  }
}

// Formatters
const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

const getContainerBadgeStyle = (type) => {
  if (type.includes('45kg')) {
    return { background: '#fef3c7', color: '#92400e', border: '1px solid #fde68a' }
  }
  if (type.includes('Đỏ')) {
    return { background: '#fee2e2', color: '#991b1b', border: '1px solid #fecaca' }
  }
  if (type.includes('Mini')) {
    return { background: '#ede9fe', color: '#5b21b6', border: '1px solid #ddd6fe' }
  }
  return { background: '#e0f2fe', color: '#0369a1', border: '1px solid #bae6fd' }
}

onMounted(() => {
  fetchReports()
})
</script>
