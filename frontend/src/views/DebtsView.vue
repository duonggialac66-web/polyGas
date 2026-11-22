<template>
  <EmployeeLayout :debt-count="debtList.length">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-rose-500"></span>
          <h2 class="text-lg font-bold text-slate-900 tracking-tight">Sổ Theo Dõi Công Nợ Khách Hàng</h2>
          <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-rose-100 text-rose-700">
            {{ debtList.length }} Đơn Nợ
          </span>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Toàn bộ công nợ tích lũy của khách hàng - Thu tiền mặt hoặc chuyển khoản để quyết toán</p>
      </div>

      <div class="px-4 py-2 bg-rose-50 border border-rose-200 rounded-lg text-right self-start sm:self-auto">
        <div class="text-[10px] font-bold uppercase tracking-wider text-rose-600">Tổng Nợ Cần Thu Hồi</div>
        <div class="text-base font-extrabold text-rose-700 font-mono">{{ formatCurrency(totalDebtAmount) }}</div>
      </div>
    </div>

    <!-- Search & Filter Bar -->
    <div class="bg-white p-3 border border-slate-200 rounded-xl mb-4 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between shadow-2xs">
      <div class="relative flex-1">
        <input 
          type="text" 
          v-model="searchKeyword" 
          @input="onSearchInput"
          placeholder="Tìm theo tên khách, quán ăn, số điện thoại..." 
          class="w-full pl-9 pr-8 py-2 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
        />
        <svg class="w-4 h-4 text-slate-400 absolute left-2.5 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <button 
          v-if="searchKeyword" 
          @click="searchKeyword = ''; loadDebts()" 
          class="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 text-xs"
        >
          ✕
        </button>
      </div>

      <button 
        @click="loadDebts" 
        :disabled="loading"
        class="inline-flex items-center justify-center gap-1.5 px-3 py-2 bg-white text-slate-700 hover:bg-slate-50 border border-slate-300 text-xs font-semibold rounded-lg shadow-2xs transition-colors disabled:opacity-50"
      >
        <svg class="w-3.5 h-3.5" :class="loading ? 'animate-spin' : ''" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        <span>Làm Mới</span>
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="bg-white rounded-xl border border-slate-200 p-12 text-center text-slate-500 text-xs">
      <div class="inline-block w-6 h-6 border-2 border-slate-300 border-t-slate-800 rounded-full animate-spin mb-2"></div>
      <div>Đang tải sổ nợ...</div>
    </div>

    <!-- Empty State -->
    <div v-else-if="debtList.length === 0" class="bg-white rounded-xl border border-dashed border-slate-300 p-12 text-center shadow-2xs">
      <div class="w-12 h-12 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center text-xl mx-auto mb-3">
        ✓
      </div>
      <h3 class="text-sm font-semibold text-slate-800">Không có hóa đơn nợ nào!</h3>
      <p class="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
        {{ searchKeyword ? 'Không tìm thấy hóa đơn nợ khớp với từ khóa tìm kiếm.' : 'Toàn bộ khách hàng đã thanh toán đầy đủ tiền gas. Tuyệt vời!' }}
      </p>
    </div>

    <!-- Debt Cards List -->
    <div v-else class="space-y-3">
      <div 
        v-for="item in debtList" 
        :key="item.reportId"
        class="bg-white rounded-xl border border-slate-200 p-4 shadow-2xs hover:border-slate-300 transition-all flex flex-col md:flex-row md:items-center justify-between gap-4"
      >
        <!-- Info Column -->
        <div class="flex-1">
          <div class="flex items-center gap-2 flex-wrap mb-1.5">
            <h4 class="text-sm font-bold text-slate-900">{{ item.customerName }}</h4>
            <span v-if="item.customerPhone" class="px-2 py-0.5 bg-slate-100 text-slate-700 rounded text-[11px] font-mono">
              📞 <a :href="'tel:' + item.customerPhone" class="hover:underline">{{ item.customerPhone }}</a>
            </span>
            <span class="px-2 py-0.5 bg-sky-50 text-sky-800 border border-sky-200 rounded text-[11px] font-semibold">
              {{ item.containerType }} (x{{ item.quantity }})
            </span>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-3 gap-2 text-xs text-slate-500 mt-2">
            <div>
              <span class="text-slate-400">Ngày giao:</span> 
              <span class="font-medium text-slate-700 ml-1">{{ item.reportDate }}</span>
            </div>
            <div>
              <span class="text-slate-400">Shipper:</span> 
              <span class="font-medium text-slate-700 ml-1">{{ item.employeeName || 'Nhân viên' }}</span>
            </div>
            <div>
              <span class="text-slate-400">Đã trả:</span> 
              <span class="font-medium text-emerald-700 ml-1 font-mono">{{ formatCurrency(item.actualReceived) }}</span>
            </div>
          </div>

          <div v-if="item.notes" class="text-xs text-slate-500 italic mt-2 bg-slate-50 p-2 rounded-lg border border-slate-100">
            💬 "{{ item.notes }}"
          </div>
        </div>

        <!-- Debt Amount & Actions Column -->
        <div class="flex items-center justify-between md:justify-end gap-4 pt-3 md:pt-0 border-t md:border-t-0 border-slate-100">
          <div class="text-left md:text-right">
            <div class="text-[10px] uppercase font-bold text-slate-400 tracking-wider">Số Tiền Còn Nợ</div>
            <div class="text-base sm:text-lg font-bold text-rose-600 font-mono tracking-tight">
              {{ formatCurrency(item.remainingDebt) }}
            </div>
            <div class="text-[10px] text-slate-400">Tổng đơn: {{ formatCurrency(item.total) }}</div>
          </div>

          <div class="flex items-center gap-2">
            <!-- Nút Thu Đủ Nhanh -->
            <button 
              @click="quickFullPay(item)"
              class="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-semibold rounded-lg transition-colors shadow-2xs"
              title="Xác nhận khách trả đủ toàn bộ số nợ còn lại"
            >
              Thu Đủ
            </button>

            <!-- Nút Mở Modal Thu Nợ (hỗ trợ trả 1 phần) -->
            <button 
              @click="openPayModal(item)"
              class="px-3.5 py-2 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors flex items-center gap-1.5"
            >
              <span>💵 Thu Nợ</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL THU TIỀN NỢ -->
    <div 
      v-if="showPayModal" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs animate-fade-in"
    >
      <div class="bg-white rounded-2xl border border-slate-200 shadow-xl w-full max-w-md overflow-hidden animate-scale-up">
        <div class="p-4 bg-slate-900 text-white flex items-center justify-between">
          <div>
            <h3 class="text-sm font-bold">Thu Tiền Nợ Khách Hàng</h3>
            <p class="text-[11px] text-slate-300">{{ selectedDebt?.customerName }}</p>
          </div>
          <button @click="showPayModal = false" class="text-slate-400 hover:text-white text-lg font-bold">✕</button>
        </div>

        <form @submit.prevent="submitPayDebt" class="p-5 space-y-4">
          <!-- Summary Info Box -->
          <div class="bg-rose-50 border border-rose-200 rounded-xl p-3 flex items-center justify-between">
            <div>
              <div class="text-[10px] font-bold text-rose-600 uppercase">Khoản Nợ Hiện Tại</div>
              <div class="text-lg font-extrabold text-rose-700 font-mono">
                {{ formatCurrency(selectedDebt?.remainingDebt) }}
              </div>
            </div>
            <div class="text-right text-xs text-slate-500">
              <div>Tổng đơn: {{ formatCurrency(selectedDebt?.total) }}</div>
              <div>Đã trả: {{ formatCurrency(selectedDebt?.actualReceived) }}</div>
            </div>
          </div>

          <!-- Quick Amount Buttons -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">Chọn nhanh số tiền thu:</label>
            <div class="grid grid-cols-3 gap-2">
              <button 
                type="button"
                @click="payForm.amountPaid = selectedDebt?.remainingDebt"
                class="px-2 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-medium rounded-lg text-center"
              >
                Trả Hết (100%)
              </button>
              <button 
                type="button"
                @click="payForm.amountPaid = Math.round((selectedDebt?.remainingDebt || 0) / 2)"
                class="px-2 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-medium rounded-lg text-center"
              >
                Trả 50%
              </button>
              <button 
                type="button"
                @click="payForm.amountPaid = 100000"
                class="px-2 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-medium rounded-lg text-center"
              >
                100.000 đ
              </button>
            </div>
          </div>

          <!-- Amount Input -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">
              Số tiền thu đợt này (VNĐ) <span class="text-rose-500">*</span>
            </label>
            <input 
              type="number" 
              v-model.number="payForm.amountPaid" 
              :max="selectedDebt?.remainingDebt"
              min="1000"
              step="1000"
              required
              class="w-full px-3 py-2 text-sm font-mono font-bold text-slate-900 border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800"
            />
            <p v-if="payForm.amountPaid >= selectedDebt?.remainingDebt" class="text-[11px] text-emerald-600 font-semibold mt-1">
              ✓ Đơn hàng sẽ được chuyển thành "ĐÃ THANH TOÁN ĐỦ"
            </p>
            <p v-else-if="payForm.amountPaid > 0" class="text-[11px] text-amber-600 mt-1">
              Khách sẽ còn nợ lại: <span class="font-bold">{{ formatCurrency(selectedDebt?.remainingDebt - payForm.amountPaid) }}</span>
            </p>
          </div>

          <!-- Notes Input -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Ghi chú thu tiền</label>
            <input 
              type="text" 
              v-model="payForm.notes" 
              placeholder="VD: Khách trả tiền mặt tại quán / Chuyển khoản VietQR..." 
              class="w-full px-3 py-2 text-xs text-slate-800 border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800"
            />
          </div>

          <!-- Modal Actions -->
          <div class="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
            <button 
              type="button" 
              @click="showPayModal = false"
              class="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="submitting || !payForm.amountPaid || payForm.amountPaid <= 0"
              class="px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg disabled:opacity-50 flex items-center gap-1.5"
            >
              <span v-if="submitting" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
              <span>Xác Nhận Thu Tiền</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import debtService from '../services/debtService.js'

const debtList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
let searchTimeout = null

// Modal state
const showPayModal = ref(false)
const submitting = ref(false)
const selectedDebt = ref(null)
const payForm = ref({
  amountPaid: 0,
  notes: ''
})

const totalDebtAmount = computed(() => {
  return debtList.value.reduce((sum, item) => sum + (Number(item.remainingDebt) || 0), 0)
})

function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0)
}

async function loadDebts() {
  loading.value = true
  try {
    const params = {}
    if (searchKeyword.value && searchKeyword.value.trim()) {
      params.customer = searchKeyword.value.trim()
    }
    const data = await debtService.getDebts(params)
    debtList.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error('Lỗi tải sổ nợ:', err)
  } finally {
    loading.value = false
  }
}

function onSearchInput() {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    loadDebts()
  }, 350)
}

function openPayModal(item) {
  selectedDebt.value = item
  payForm.value = {
    amountPaid: item.remainingDebt || item.total,
    notes: 'Thu tiền nợ giao gas'
  }
  showPayModal.value = true
}

async function quickFullPay(item) {
  const amount = item.remainingDebt || item.total
  if (!confirm(`Xác nhận đã thu đủ ${formatCurrency(amount)} từ khách "${item.customerName}"?`)) {
    return
  }
  try {
    await debtService.payDebt(item.reportId, {
      amountPaid: amount,
      notes: 'Thu đủ toàn bộ tiền nợ'
    })
    await loadDebts()
  } catch (err) {
    alert('Không thể cập nhật thu nợ: ' + err.message)
  }
}

async function submitPayDebt() {
  if (!selectedDebt.value || !payForm.value.amountPaid || payForm.value.amountPaid <= 0) {
    alert('Vui lòng nhập số tiền thu hợp lệ')
    return
  }

  submitting.value = true
  try {
    await debtService.payDebt(selectedDebt.value.reportId, {
      amountPaid: payForm.value.amountPaid,
      notes: payForm.value.notes
    })
    showPayModal.value = false
    await loadDebts()
  } catch (err) {
    alert('Lỗi thu tiền nợ: ' + err.message)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadDebts()
})
</script>
