<template>
  <EmployeeLayout :is-backend-connected="!isMockData" :debt-count="debtBillsCount" @refresh="fetchReports">
    <!-- Header Section -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <h2 class="text-lg font-bold text-slate-900 tracking-tight">Báo Cáo Giao Gas Trong Ngày</h2>
        <p class="text-xs text-slate-500 mt-0.5">Quản lý các đợt giao vỏ/bình, công nợ và thu tiền khách hàng</p>
      </div>

      <div class="flex items-center gap-2">
        <!-- Date Selector -->
        <input 
          type="date" 
          v-model="selectedDate" 
          @change="fetchReports"
          class="text-xs font-semibold text-slate-700 bg-white border border-slate-300 rounded-lg px-3 py-1.5 shadow-2xs focus:outline-none focus:border-slate-800 transition-colors cursor-pointer"
        />

        <!-- Action CTA -->
        <button 
          @click="openCreateModal"
          class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
        >
          <span>➕ Nhập Phiếu Giao</span>
        </button>
      </div>
    </div>

    <!-- Alert Banner when Backend is Offline (Mock Mode) -->
    <div 
      v-if="isMockData" 
      class="mb-5 p-3 bg-amber-50 border border-amber-200 rounded-lg flex items-center justify-between text-xs text-amber-800"
    >
      <div class="flex items-center gap-2">
        <span class="font-bold text-amber-900">Chế độ Demo (Offline):</span>
        <span>Hệ thống backend chưa khởi động. Bạn vẫn có thể thao tác với dữ liệu mẫu bên dưới.</span>
      </div>
      <button @click="fetchReports" class="text-xs font-semibold text-amber-900 hover:underline ml-2">
        Thử lại
      </button>
    </div>

    <!-- KPI Summary Metrics (Professional Clean Corporate) -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-3.5 mb-6">
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-1">Bình Đã Giao</div>
        <div class="text-xl font-bold text-slate-900">
          {{ stats.totalQuantity }} <span class="text-xs font-normal text-slate-500">bình</span>
        </div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-1">Tổng Doanh Thu</div>
        <div class="text-lg font-bold text-slate-900">
          {{ formatCurrency(stats.totalAmount) }}
        </div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-emerald-600 uppercase mb-1">Đã Thu Tiền</div>
        <div class="text-lg font-bold text-emerald-600">
          {{ formatCurrency(stats.paidAmount) }}
        </div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-rose-600 uppercase mb-1">Khách Còn Nợ</div>
        <div class="text-lg font-bold text-rose-600">
          {{ formatCurrency(stats.debtAmount) }}
        </div>
      </div>
    </div>

    <!-- Toolbar: Search & Segmented Filter -->
    <div class="bg-white p-2.5 rounded-xl border border-slate-200 mb-4 flex flex-col sm:flex-row gap-2.5 items-stretch sm:items-center justify-between shadow-2xs">
      <div class="relative flex-1">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="Tìm tên khách hàng hoặc ghi chú..." 
          class="w-full px-3 py-1.5 text-xs text-slate-900 bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:border-slate-800 transition-colors placeholder:text-slate-400 font-medium"
        />
      </div>

      <div class="flex items-center gap-1 self-start sm:self-auto">
        <button 
          @click="filterStatus = ''" 
          class="px-2.5 py-1 text-xs rounded-md transition-colors font-medium"
          :class="filterStatus === '' ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Tất cả ({{ reports.length }})
        </button>
        <button 
          @click="filterStatus = 'PAID'" 
          class="px-2.5 py-1 text-xs rounded-md transition-colors font-medium"
          :class="filterStatus === 'PAID' ? 'bg-emerald-600 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Đã Thu
        </button>
        <button 
          @click="filterStatus = 'DEBT'" 
          class="px-2.5 py-1 text-xs rounded-md transition-colors font-medium"
          :class="filterStatus === 'DEBT' ? 'bg-rose-600 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Ghi Nợ ({{ debtBillsCount }})
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="py-16 text-center text-slate-500 bg-white rounded-xl border border-slate-200">
      <div class="w-6 h-6 border-2 border-slate-300 border-t-slate-800 rounded-full animate-spin mx-auto mb-2"></div>
      <p class="text-xs">Đang tải dữ liệu báo cáo...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredReports.length === 0" class="bg-white rounded-xl border border-dashed border-slate-300 p-12 text-center">
      <h3 class="text-sm font-semibold text-slate-700">Chưa có phiếu giao gas nào</h3>
      <p class="text-xs text-slate-500 mt-1">Chọn ngày khác hoặc tạo mới một phiếu giao gas cho ca làm việc.</p>
    </div>

    <!-- Reports Table / Cards List -->
    <div v-else class="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-2xs divide-y divide-slate-100">
      <div 
        v-for="item in filteredReports" 
        :key="item.id"
        class="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-slate-50 transition-colors"
      >
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 mb-1">
            <h4 class="text-sm font-bold text-slate-900">{{ item.customerName }}</h4>
            <span class="px-2 py-0.5 rounded text-[11px] font-semibold border bg-slate-100 text-slate-700 border-slate-200">
              {{ item.containerType }}
            </span>
            <span class="text-xs font-semibold text-slate-600">
              x{{ item.quantity }}
            </span>
          </div>
          <div class="text-xs text-slate-500 flex items-center gap-2">
            <span>Đơn giá: <strong class="text-slate-700">{{ formatCurrency(item.unitPrice) }}</strong></span>
            <span v-if="item.notes" class="text-slate-300">•</span>
            <span v-if="item.notes" class="text-slate-600 italic">"{{ item.notes }}"</span>
          </div>
        </div>

        <div class="flex items-center justify-between sm:justify-end gap-3 pt-2 sm:pt-0 border-t border-slate-100 sm:border-0">
          <div class="text-left sm:text-right">
            <div class="text-sm sm:text-base font-bold text-slate-900">
              {{ formatCurrency(item.total) }}
            </div>
            <div class="text-[10px] text-slate-400">Thành tiền</div>
          </div>

          <!-- Status Pill -->
          <button 
            @click="handleTogglePayment(item)"
            class="px-2.5 py-1 rounded text-xs font-semibold border transition-colors"
            :class="item.paymentStatus === 'PAID' 
              ? 'bg-emerald-50 text-emerald-700 border-emerald-300 hover:bg-emerald-100' 
              : 'bg-rose-50 text-rose-700 border-rose-300 hover:bg-rose-100'"
          >
            {{ item.paymentStatus === 'PAID' ? '✓ Đã thu tiền' : '⚠️ Ghi nợ' }}
          </button>

          <!-- Edit & Delete -->
          <div class="flex items-center gap-1">
            <button 
              @click="openEditModal(item)"
              class="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-200 rounded transition-colors"
              title="Sửa phiếu"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
            </button>
            <button 
              @click="handleDelete(item)"
              class="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-rose-50 rounded transition-colors"
              title="Xóa phiếu"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Form -->
    <div 
      v-if="modal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs transition-opacity"
      @click.self="closeModal"
    >
      <div class="bg-white w-full max-w-lg rounded-xl shadow-lg border border-slate-200 overflow-hidden">
        <div class="px-5 py-3.5 border-b border-slate-200 flex items-center justify-between bg-slate-50">
          <h3 class="text-sm font-bold text-slate-900">
            {{ modal.isEdit ? 'Chỉnh Sửa Phiếu Giao Gas' : 'Nhập Phiếu Giao Gas Mới' }}
          </h3>
          <button @click="closeModal" class="p-1 text-slate-400 hover:text-slate-600">✕</button>
        </div>

        <form @submit.prevent="handleSubmitForm" class="p-5 space-y-4">
          <div v-if="errors.general" class="p-3 bg-rose-50 border border-rose-200 rounded-lg text-xs text-rose-700">
            {{ errors.general }}
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">
              Tên Khách Hàng <span class="text-rose-500">*</span>
            </label>
            <input 
              type="text" 
              v-model="form.customerName" 
              @input="delete errors.customerName"
              placeholder="VD: Quán Ăn Cô Ba, Bún Bò Huế 36..."
              :class="[
                'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                errors.customerName ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
              ]"
            />
            <p v-if="errors.customerName" class="text-xs text-rose-600 mt-1 font-medium">
              {{ errors.customerName }}
            </p>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">
              Loại Bình Gas <span class="text-rose-500">*</span>
            </label>
            <select 
              v-model="form.containerType" 
              @change="onSelectGasTypeChange"
              class="w-full px-3 py-2 text-xs text-slate-900 bg-white border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800 font-medium cursor-pointer"
            >
              <option v-for="g in GAS_TYPES" :key="g.type" :value="g.type">
                {{ g.type }} (Đơn giá chuẩn: {{ formatCurrency(g.defaultPrice) }})
              </option>
            </select>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1">
                Số Lượng <span class="text-rose-500">*</span>
              </label>
              <input 
                type="number" 
                min="1" 
                v-model.number="form.quantity" 
                @input="delete errors.quantity"
                :class="[
                  'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                  errors.quantity ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
                ]"
              />
              <p v-if="errors.quantity" class="text-xs text-rose-600 mt-1 font-medium">
                {{ errors.quantity }}
              </p>
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1">
                Đơn Giá (VNĐ) <span class="text-rose-500">*</span>
              </label>
              <input 
                type="number" 
                step="1000" 
                v-model.number="form.unitPrice" 
                @input="delete errors.unitPrice"
                :class="[
                  'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                  errors.unitPrice ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
                ]"
              />
              <p v-if="errors.unitPrice" class="text-xs text-rose-600 mt-1 font-medium">
                {{ errors.unitPrice }}
              </p>
            </div>
          </div>

          <div class="p-3 bg-slate-50 border border-slate-200 rounded-lg flex items-center justify-between text-xs font-bold text-slate-800">
            <span>Tổng Tiền Hóa Đơn:</span>
            <span class="text-sm font-extrabold text-slate-900">{{ formatCurrency(calculatedTotal) }}</span>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Trạng Thái Thanh Toán</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                type="button"
                @click="form.paymentStatus = 'PAID'"
                class="py-2 px-3 rounded-lg border text-xs font-semibold transition-colors"
                :class="form.paymentStatus === 'PAID' ? 'bg-emerald-50 text-emerald-800 border-emerald-300' : 'bg-white text-slate-600 border-slate-300'"
              >
                Đã Thu Tiền Mặt
              </button>
              <button
                type="button"
                @click="form.paymentStatus = 'DEBT'"
                class="py-2 px-3 rounded-lg border text-xs font-semibold transition-colors"
                :class="form.paymentStatus === 'DEBT' ? 'bg-rose-50 text-rose-800 border-rose-300' : 'bg-white text-slate-600 border-slate-300'"
              >
                Ghi Nợ Khách Hàng
              </button>
            </div>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Ghi Chú Đơn Hàng</label>
            <input 
              type="text" 
              v-model="form.notes" 
              placeholder="VD: Hẹn 17h thu tiền, đổi van an toàn..."
              class="w-full px-3 py-2 text-xs text-slate-900 bg-white border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800 font-medium"
            />
          </div>

          <div class="pt-3 border-t border-slate-200 flex items-center justify-end gap-2">
            <button 
              type="button" 
              @click="closeModal"
              class="px-3.5 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="submitting"
              class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-400 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
            >
              {{ modal.isEdit ? 'Lưu Thay Đổi' : 'Lưu Phiếu Giao' }}
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
import deliveryReportService, { GAS_TYPES } from '../services/deliveryReportService.js'

const selectedDate = ref(new Date().toISOString().substring(0, 10))
const reports = ref([])
const loading = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const isMockData = ref(false)
const errors = ref({})

const modal = reactive({
  isOpen: false,
  isEdit: false,
  editingId: null
})

const form = reactive({
  customerName: '',
  containerType: 'Bình 12kg Xám',
  quantity: 1,
  unitPrice: 350000,
  paymentStatus: 'PAID',
  notes: ''
})

const calculatedTotal = computed(() => {
  return (form.quantity || 0) * (form.unitPrice || 0)
})

const stats = computed(() => {
  const totalQuantity = reports.value.reduce((sum, r) => sum + (r.quantity || 0), 0)
  const totalAmount = reports.value.reduce((sum, r) => sum + (r.total || 0), 0)
  const paidAmount = reports.value
    .filter(r => r.paymentStatus === 'PAID')
    .reduce((sum, r) => sum + (r.total || 0), 0)
  const debtAmount = reports.value
    .filter(r => r.paymentStatus === 'DEBT')
    .reduce((sum, r) => sum + (r.total || 0), 0)

  return { totalQuantity, totalAmount, paidAmount, debtAmount }
})

const debtBillsCount = computed(() => {
  return reports.value.filter(r => r.paymentStatus === 'DEBT').length
})

const filteredReports = computed(() => {
  return reports.value.filter(item => {
    const matchStatus = !filterStatus.value || item.paymentStatus === filterStatus.value
    const kw = searchKeyword.value.toLowerCase().trim()
    const matchKw = !kw || 
      (item.customerName && item.customerName.toLowerCase().includes(kw)) ||
      (item.notes && item.notes.toLowerCase().includes(kw))
    return matchStatus && matchKw
  })
})

function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0)
}

function onSelectGasTypeChange() {
  const found = GAS_TYPES.find(g => g.type === form.containerType)
  if (found) {
    form.unitPrice = found.defaultPrice
  }
}

async function fetchReports() {
  loading.value = true
  try {
    const res = await deliveryReportService.getTodayReports(selectedDate.value)
    if (res && res.data) {
      reports.value = res.data
      isMockData.value = res.isMock
    } else {
      reports.value = []
    }
  } catch (err) {
    console.warn('Lỗi lấy báo cáo:', err)
  } finally {
    loading.value = false
  }
}

function validateForm() {
  const errs = {}
  if (!form.customerName || !form.customerName.trim()) {
    errs.customerName = 'Vui lòng nhập tên khách hàng'
  }
  if (!form.containerType) {
    errs.containerType = 'Vui lòng chọn loại bình gas'
  }
  if (!form.quantity || form.quantity < 1) {
    errs.quantity = 'Số lượng bình phải lớn hơn hoặc bằng 1'
  }
  if (!form.unitPrice || form.unitPrice < 1000) {
    errs.unitPrice = 'Đơn giá không hợp lệ'
  }
  errors.value = errs
  return Object.keys(errs).length === 0
}

function openCreateModal() {
  modal.isOpen = true
  modal.isEdit = false
  modal.editingId = null
  errors.value = {}
  form.customerName = ''
  form.containerType = 'Bình 12kg Xám'
  form.quantity = 1
  form.unitPrice = 350000
  form.paymentStatus = 'PAID'
  form.notes = ''
}

function openEditModal(item) {
  modal.isOpen = true
  modal.isEdit = true
  modal.editingId = item.id
  errors.value = {}
  form.customerName = item.customerName
  form.containerType = item.containerType
  form.quantity = item.quantity
  form.unitPrice = item.unitPrice
  form.paymentStatus = item.paymentStatus
  form.notes = item.notes || ''
}

function closeModal() {
  modal.isOpen = false
  errors.value = {}
}

async function handleSubmitForm() {
  if (!validateForm()) return

  submitting.value = true
  errors.value = {}
  try {
    const payload = {
      customerName: form.customerName.trim(),
      containerType: form.containerType,
      quantity: form.quantity,
      unitPrice: form.unitPrice,
      paymentStatus: form.paymentStatus,
      reportDate: selectedDate.value,
      notes: form.notes ? form.notes.trim() : ''
    }

    if (modal.isEdit) {
      await deliveryReportService.updateReport(modal.editingId, payload)
    } else {
      await deliveryReportService.createReport(payload)
    }

    closeModal()
    await fetchReports()
  } catch (err) {
    if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
      errors.value = err.fieldErrors
    } else {
      errors.value = { general: err.message || 'Lưu phiếu giao thất bại' }
    }
  } finally {
    submitting.value = false
  }
}

async function handleTogglePayment(item) {
  try {
    await deliveryReportService.togglePaymentStatus(item.id)
    await fetchReports()
  } catch (err) {
    console.error('Không thể đổi trạng thái thu tiền:', err)
  }
}

async function handleDelete(item) {
  if (!confirm(`Xác nhận xóa phiếu giao cho khách "${item.customerName}"?`)) return
  try {
    await deliveryReportService.deleteReport(item.id)
    await fetchReports()
  } catch (err) {
    alert(err.message || 'Xóa phiếu thất bại')
  }
}

onMounted(() => {
  fetchReports()
})
</script>
