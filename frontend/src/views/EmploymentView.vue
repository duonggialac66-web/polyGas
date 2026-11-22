<template>
  <EmployeeLayout :is-backend-connected="!isMockData" :debt-count="debtBillsCount" @refresh="fetchReports">
    <!-- Top Action Bar & Date Selector -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2 h-2 rounded-full bg-sky-500"></span>
          <h2 class="text-xl font-extrabold tracking-tight text-slate-900">Báo Cáo Giao Gas Hôm Nay</h2>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Nhập số lượng bình giao và cập nhật tiền thu / công nợ khách</p>
      </div>

      <div class="flex items-center gap-2">
        <!-- Date Selector -->
        <div class="relative">
          <input 
            type="date" 
            v-model="selectedDate" 
            @change="fetchReports"
            class="text-xs font-semibold text-slate-700 bg-white border border-slate-200 rounded-xl px-3 py-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all cursor-pointer"
          />
        </div>

        <!-- Main CTA Button -->
        <button 
          @click="openCreateModal"
          class="inline-flex items-center gap-1.5 px-4 py-2 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white text-xs font-bold rounded-xl shadow-sm shadow-sky-600/20 transition-all duration-150"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          <span>Nhập Phiếu Giao</span>
        </button>
      </div>
    </div>

    <!-- Alert Banner when Backend is Offline (Mock Mode) -->
    <div 
      v-if="isMockData" 
      class="mb-4 p-3 bg-amber-50/90 border border-amber-200/80 rounded-2xl flex items-center justify-between text-xs text-amber-900"
    >
      <div class="flex items-center gap-2">
        <svg class="w-4 h-4 text-amber-600 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span>
          <strong>Backend chưa kết nối:</strong> Đang chạy chế độ dữ liệu thử nghiệm (Mock Data). Bạn vẫn có thể nhập phiếu, tính tiền và đổi nợ bình thường!
        </span>
      </div>
      <button @click="fetchReports" class="text-[11px] font-bold text-amber-800 hover:underline flex-shrink-0 ml-2">
        Thử lại
      </button>
    </div>

    <!-- Summary Metrics (Cards Thống Kê Nhanh) -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-5">
      <!-- Total Cylinders -->
      <div class="bg-white p-3.5 rounded-2xl border border-slate-200/70 shadow-sm">
        <div class="flex items-center justify-between text-slate-500 mb-1">
          <span class="text-[11px] font-medium uppercase tracking-wider">Bình Đã Giao</span>
          <span class="text-sm">🛢️</span>
        </div>
        <div class="text-xl font-black text-slate-900 tracking-tight">
          {{ stats.totalQuantity }} <span class="text-xs font-semibold text-slate-500 font-normal">bình</span>
        </div>
      </div>

      <!-- Total Turnover -->
      <div class="bg-white p-3.5 rounded-2xl border border-slate-200/70 shadow-sm">
        <div class="flex items-center justify-between text-slate-500 mb-1">
          <span class="text-[11px] font-medium uppercase tracking-wider">Tổng Doanh Thu</span>
          <span class="text-sm">💵</span>
        </div>
        <div class="text-base sm:text-lg font-black text-slate-900 tracking-tight">
          {{ formatCurrency(stats.totalAmount) }}
        </div>
      </div>

      <!-- Paid Amount -->
      <div class="bg-white p-3.5 rounded-2xl border border-slate-200/70 shadow-sm">
        <div class="flex items-center justify-between text-emerald-700 mb-1">
          <span class="text-[11px] font-bold uppercase tracking-wider">Đã Thu Tiền</span>
          <span class="w-2 h-2 rounded-full bg-emerald-500"></span>
        </div>
        <div class="text-base sm:text-lg font-black text-emerald-600 tracking-tight">
          {{ formatCurrency(stats.paidAmount) }}
        </div>
      </div>

      <!-- Debt Amount -->
      <div class="bg-white p-3.5 rounded-2xl border border-slate-200/70 shadow-sm">
        <div class="flex items-center justify-between text-rose-700 mb-1">
          <span class="text-[11px] font-bold uppercase tracking-wider">Khách Còn Nợ</span>
          <span class="w-2 h-2 rounded-full bg-rose-500"></span>
        </div>
        <div class="text-base sm:text-lg font-black text-rose-600 tracking-tight">
          {{ formatCurrency(stats.debtAmount) }}
        </div>
      </div>
    </div>

    <!-- Filter & Search Toolbar -->
    <div class="bg-white p-2.5 rounded-2xl border border-slate-200/70 shadow-sm mb-4 flex flex-col sm:flex-row gap-2.5 items-stretch sm:items-center justify-between">
      <!-- Search Input -->
      <div class="relative flex-1">
        <svg class="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="Tìm tên khách, ghi chú..." 
          class="w-full pl-9 pr-3 py-1.5 text-xs text-slate-700 bg-slate-50 border border-slate-200/80 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all placeholder:text-slate-400"
        />
      </div>

      <!-- Segmented Status Tabs -->
      <div class="flex items-center gap-1 p-1 bg-slate-100 rounded-xl self-start sm:self-auto">
        <button 
          @click="filterStatus = ''" 
          class="px-2.5 py-1 text-xs font-semibold rounded-lg transition-all"
          :class="filterStatus === '' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'"
        >
          Tất Cả ({{ reports.length }})
        </button>
        <button 
          @click="filterStatus = 'PAID'" 
          class="px-2.5 py-1 text-xs font-semibold rounded-lg transition-all flex items-center gap-1"
          :class="filterStatus === 'PAID' ? 'bg-emerald-500 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'"
        >
          <span>Đã Thu</span>
        </button>
        <button 
          @click="filterStatus = 'DEBT'" 
          class="px-2.5 py-1 text-xs font-semibold rounded-lg transition-all flex items-center gap-1"
          :class="filterStatus === 'DEBT' ? 'bg-rose-500 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'"
        >
          <span>Nợ ({{ debtBillsCount }})</span>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="py-16 text-center text-slate-400">
      <div class="w-7 h-7 border-2 border-sky-500/30 border-t-sky-600 rounded-full animate-spin mx-auto mb-2"></div>
      <p class="text-xs font-medium">Đang đồng bộ dữ liệu giao hàng...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredReports.length === 0" class="bg-white rounded-2xl border border-dashed border-slate-200 p-12 text-center">
      <div class="w-12 h-12 rounded-2xl bg-slate-100 text-slate-400 flex items-center justify-center text-xl mx-auto mb-3">
        📝
      </div>
      <h3 class="text-sm font-bold text-slate-800">Chưa có phiếu giao gas nào hôm nay</h3>
      <p class="text-xs text-slate-500 mt-1 max-w-xs mx-auto">
        Bấm nút bên dưới để ghi nhận chuyến giao gas đầu tiên trong ca làm việc.
      </p>
      <button 
        @click="openCreateModal"
        class="mt-4 inline-flex items-center gap-1.5 px-4 py-2 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white text-xs font-bold rounded-xl shadow-sm transition-all"
      >
        <span>➕ Nhập Phiếu Đầu Tiên</span>
      </button>
    </div>

    <!-- Delivery Reports List (Thiết kế Card tinh tế, tối ưu cho nhân viên thao tác nhanh) -->
    <div v-else class="space-y-2.5">
      <div 
        v-for="item in filteredReports" 
        :key="item.id"
        class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs hover:border-slate-300 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3 group"
      >
        <!-- Left: Customer Info, Gas Type, Notes -->
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 flex-wrap mb-1">
            <h4 class="text-sm font-bold text-slate-900 tracking-tight">
              {{ item.customerName }}
            </h4>

            <!-- Gas Type Pill with distinct color mark -->
            <span 
              class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-semibold border"
              :class="getContainerPillClass(item.containerType)"
            >
              <span class="w-1.5 h-1.5 rounded-full" :class="getContainerDotClass(item.containerType)"></span>
              {{ item.containerType }}
            </span>

            <!-- Quantity Multiplier -->
            <span class="text-xs font-extrabold text-sky-700 bg-sky-50 px-2 py-0.5 rounded-md border border-sky-100">
              SL: {{ item.quantity }}
            </span>
          </div>

          <!-- Unit price & Note snippet -->
          <div class="text-xs text-slate-500 flex items-center gap-2 flex-wrap">
            <span>Đơn giá: <strong class="text-slate-700 font-medium">{{ formatCurrency(item.unitPrice) }}</strong></span>
            <span v-if="item.notes" class="text-slate-400">•</span>
            <span v-if="item.notes" class="text-slate-600 italic truncate max-w-xs">
              "{{ item.notes }}"
            </span>
          </div>
        </div>

        <!-- Right: Total Amount, Payment Toggle Pill & Actions -->
        <div class="flex items-center justify-between sm:justify-end gap-3 pt-2 sm:pt-0 border-t border-slate-100 sm:border-0">
          <!-- Total Price -->
          <div class="text-left sm:text-right">
            <div class="text-sm sm:text-base font-black text-slate-900 tracking-tight">
              {{ formatCurrency(item.total) }}
            </div>
            <div class="text-[10px] text-slate-400 sm:text-right">Thành tiền</div>
          </div>

          <!-- One-Tap Payment Status Toggle -->
          <button 
            @click="handleTogglePayment(item)"
            class="px-2.5 py-1.5 rounded-xl text-xs font-bold border transition-all active:scale-90 flex items-center gap-1.5"
            :class="item.paymentStatus === 'PAID' 
              ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100' 
              : 'bg-rose-50 text-rose-700 border-rose-200 hover:bg-rose-100 animate-pulse'"
            :title="item.paymentStatus === 'PAID' ? 'Bấm để chuyển sang Nợ' : 'Bấm để xác nhận Đã thu tiền'"
          >
            <span v-if="item.paymentStatus === 'PAID'">✅ Đã Thu Tiền</span>
            <span v-else>⚠️ Ghi Nợ (Bấm thu)</span>
          </button>

          <!-- Edit & Delete Mini Buttons -->
          <div class="flex items-center gap-1 opacity-80 group-hover:opacity-100 transition-opacity">
            <button 
              @click="openEditModal(item)"
              class="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
              title="Chỉnh sửa phiếu"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
            </button>
            <button 
              @click="handleDelete(item)"
              class="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
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

    <!-- Create / Edit Modal (Dialog Gọn Gàng, Đẹp, Tối Ưu Thao Tác) -->
    <div 
      v-if="modal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs transition-opacity"
      @click.self="closeModal"
    >
      <div class="bg-white w-full max-w-lg rounded-3xl shadow-xl border border-slate-200 overflow-hidden transform transition-all animate-in fade-in zoom-in-95 duration-150">
        <!-- Modal Header -->
        <div class="px-5 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div>
            <h3 class="text-sm font-bold text-slate-900">
              {{ modal.isEdit ? 'Chỉnh Sửa Phiếu Giao Gas' : 'Nhập Phiếu Báo Cáo Giao Gas' }}
            </h3>
            <p class="text-[11px] text-slate-500">Ghi nhận thông tin bình gas giao cho khách</p>
          </div>
          <button @click="closeModal" class="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Form Body -->
        <form @submit.prevent="handleSubmitForm" class="p-5 space-y-4">
          <!-- 1. Customer Name -->
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">
              Tên Khách Hàng <span class="text-rose-500">*</span>
            </label>
            <input 
              type="text" 
              v-model="form.customerName" 
              required
              placeholder="VD: Quán Cơm Tấm 24, Cô Lan, Bún Bò Huế 36..."
              class="w-full px-3.5 py-2.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all placeholder:text-slate-400 font-medium"
            />
          </div>

          <!-- 2. Gas Container Selector Chips -->
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1.5">
              Chọn Loại Bình Gas <span class="text-rose-500">*</span>
            </label>
            <div class="grid grid-cols-2 gap-2">
              <button
                type="button"
                v-for="g in GAS_TYPES"
                :key="g.type"
                @click="selectGasType(g)"
                class="p-2.5 text-left rounded-xl border transition-all text-xs flex flex-col justify-between"
                :class="form.containerType === g.type 
                  ? 'border-sky-500 bg-sky-50/80 ring-2 ring-sky-500/20 text-sky-950 font-bold' 
                  : 'border-slate-200 hover:border-slate-300 bg-white text-slate-700 font-medium'"
              >
                <span>🛢️ {{ g.type }}</span>
                <span class="text-[11px] text-slate-500 mt-1">{{ formatCurrency(g.defaultPrice) }}</span>
              </button>
            </div>
          </div>

          <!-- 3. Quantity & Unit Price Stepper -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-bold text-slate-700 mb-1">
                Số Lượng Bình <span class="text-rose-500">*</span>
              </label>
              <div class="flex items-center border border-slate-200 rounded-xl bg-slate-50 overflow-hidden">
                <button 
                  type="button" 
                  @click="form.quantity = Math.max(1, form.quantity - 1)"
                  class="px-3 py-2 text-slate-600 hover:bg-slate-200 font-bold transition-colors active:scale-95"
                >
                  -
                </button>
                <input 
                  type="number" 
                  min="1" 
                  max="100" 
                  v-model.number="form.quantity" 
                  class="w-full text-center py-2 text-xs font-bold text-slate-900 bg-transparent focus:outline-none"
                />
                <button 
                  type="button" 
                  @click="form.quantity = form.quantity + 1"
                  class="px-3 py-2 text-slate-600 hover:bg-slate-200 font-bold transition-colors active:scale-95"
                >
                  +
                </button>
              </div>
            </div>

            <div>
              <label class="block text-xs font-bold text-slate-700 mb-1">
                Đơn Giá (VNĐ) <span class="text-rose-500">*</span>
              </label>
              <input 
                type="number" 
                step="5000"
                v-model.number="form.unitPrice" 
                required
                class="w-full px-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all"
              />
            </div>
          </div>

          <!-- Total Calculation Display -->
          <div class="p-3 bg-slate-100/80 rounded-xl flex items-center justify-between">
            <span class="text-xs font-medium text-slate-600">Thành tiền tạm tính:</span>
            <span class="text-base font-black text-slate-900 tracking-tight">
              {{ formatCurrency(computedTotal) }}
            </span>
          </div>

          <!-- 4. Payment Status (Đã thu tiền / Ghi nợ) -->
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1.5">
              Trạng Thái Thanh Toán <span class="text-rose-500">*</span>
            </label>
            <div class="grid grid-cols-2 gap-2">
              <label 
                class="flex items-center gap-2 p-2.5 rounded-xl border cursor-pointer transition-all text-xs font-bold"
                :class="form.paymentStatus === 'PAID' 
                  ? 'border-emerald-500 bg-emerald-50 text-emerald-800 ring-2 ring-emerald-500/20' 
                  : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'"
              >
                <input type="radio" value="PAID" v-model="form.paymentStatus" class="sr-only" />
                <span class="text-sm">✅</span>
                <span>Khách Đã Trả Tiền</span>
              </label>

              <label 
                class="flex items-center gap-2 p-2.5 rounded-xl border cursor-pointer transition-all text-xs font-bold"
                :class="form.paymentStatus === 'DEBT' 
                  ? 'border-rose-500 bg-rose-50 text-rose-800 ring-2 ring-rose-500/20' 
                  : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'"
              >
                <input type="radio" value="DEBT" v-model="form.paymentStatus" class="sr-only" />
                <span class="text-sm">⚠️</span>
                <span>Ghi Nợ (Chưa Thu)</span>
              </label>
            </div>
          </div>

          <!-- 5. Notes -->
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Ghi Chú Giao Hàng</label>
            <textarea 
              v-model="form.notes" 
              rows="2"
              placeholder="VD: Giao lên lầu 2, đổi bình gas cũ rỉ sét, hẹn 18h quay lại lấy tiền..."
              class="w-full px-3.5 py-2 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all placeholder:text-slate-400 font-medium"
            ></textarea>
          </div>

          <!-- Form Buttons -->
          <div class="pt-2 flex items-center justify-end gap-2">
            <button 
              type="button" 
              @click="closeModal" 
              class="px-4 py-2 text-xs font-bold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-xl transition-all"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="formSubmitting"
              class="px-5 py-2 text-xs font-bold text-white bg-sky-600 hover:bg-sky-700 active:scale-95 rounded-xl shadow-sm transition-all"
            >
              {{ formSubmitting ? 'Đang lưu...' : (modal.isEdit ? 'Lưu Thay Đổi' : 'Xác Nhận Lưu Phiếu') }}
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

// Modal state
const modal = reactive({
  isOpen: false,
  isEdit: false,
  currentId: null
})

// Form state
const form = reactive({
  customerName: '',
  containerType: 'Bình 12kg Xám',
  quantity: 1,
  unitPrice: 350000,
  paymentStatus: 'PAID',
  notes: ''
})

// Computed Total
const computedTotal = computed(() => {
  return (Number(form.quantity) || 0) * (Number(form.unitPrice) || 0)
})

// Filtered reports
const filteredReports = computed(() => {
  return reports.value.filter(item => {
    if (searchKeyword.value.trim()) {
      const kw = searchKeyword.value.toLowerCase().trim()
      const matchName = item.customerName && item.customerName.toLowerCase().includes(kw)
      const matchNotes = item.notes && item.notes.toLowerCase().includes(kw)
      if (!matchName && !matchNotes) return false
    }

    if (filterStatus.value && item.paymentStatus !== filterStatus.value) {
      return false
    }

    return true
  })
})

// Debt Bills Count
const debtBillsCount = computed(() => {
  return reports.value.filter(r => r.paymentStatus === 'DEBT').length
})

// Summary Stats
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
    console.error('Lỗi khi tải phiếu giao:', err)
  } finally {
    loading.value = false
  }
}

const selectGasType = (g) => {
  form.containerType = g.type
  form.unitPrice = g.defaultPrice
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
    } else {
      await deliveryReportService.createReport(payload)
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
  const nextStatus = item.paymentStatus === 'PAID' ? 'Ghi Nợ' : 'Đã Thu Tiền'
  if (confirm(`Chuyển trạng thái đơn của [${item.customerName}] sang "${nextStatus}"?`)) {
    try {
      await deliveryReportService.togglePaymentStatus(item.id)
      fetchReports()
    } catch (err) {
      alert('Không thể cập nhật trạng thái: ' + err.message)
    }
  }
}

const handleDelete = async (item) => {
  if (confirm(`Xác nhận xóa phiếu giao cho khách [${item.customerName}]?`)) {
    try {
      await deliveryReportService.deleteReport(item.id)
      fetchReports()
    } catch (err) {
      alert('Không thể xóa phiếu: ' + err.message)
    }
  }
}

// Formatters & UI Helpers
const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

const getContainerPillClass = (type) => {
  if (type.includes('45kg')) return 'bg-amber-50 text-amber-900 border-amber-200'
  if (type.includes('Đỏ')) return 'bg-rose-50 text-rose-900 border-rose-200'
  if (type.includes('Mini')) return 'bg-purple-50 text-purple-900 border-purple-200'
  return 'bg-slate-100 text-slate-800 border-slate-200'
}

const getContainerDotClass = (type) => {
  if (type.includes('45kg')) return 'bg-amber-500'
  if (type.includes('Đỏ')) return 'bg-rose-500'
  if (type.includes('Mini')) return 'bg-purple-500'
  return 'bg-slate-400'
}

onMounted(() => {
  fetchReports()
})
</script>
