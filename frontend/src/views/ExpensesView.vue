<template>
  <EmployeeLayout>
    <!-- Header -->
    <div class="flex items-center justify-between mb-4">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-amber-500 animate-pulse"></span>
          <h2 class="text-xl font-extrabold tracking-tight text-slate-900">Chi Phí Vận Hành Trong Ngày</h2>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Shipper ghi nhận chi phí phát sinh (xăng xe, ăn trưa...) để chốt sổ cuối ngày</p>
      </div>

      <button 
        @click="openModal"
        class="inline-flex items-center gap-1.5 px-3.5 py-2 bg-amber-600 hover:bg-amber-700 active:scale-95 text-white text-xs font-bold rounded-xl shadow-sm transition-all"
      >
        <span>➕ Ghi Nhận Chi Phí</span>
      </button>
    </div>

    <!-- Date Filter & Quick Summary -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3 mb-4">
      <!-- Date Picker -->
      <div class="bg-white p-3.5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
        <span class="text-xs font-bold text-slate-700">Ngày xem:</span>
        <input 
          v-model="selectedDate" 
          @change="loadExpenses"
          type="date" 
          class="text-xs px-2.5 py-1.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-amber-500/30"
        />
      </div>

      <!-- Total Amount Card -->
      <div class="bg-gradient-to-br from-amber-500 to-amber-600 p-3.5 rounded-2xl text-white shadow-xs sm:col-span-2 flex items-center justify-between">
        <div>
          <div class="text-[11px] font-medium text-amber-100">Tổng Chi Phí Trong Ngày</div>
          <div class="text-xl font-extrabold">{{ formatCurrency(totalAmount) }}</div>
        </div>
        <div class="text-xs bg-white/20 px-3 py-1.5 rounded-xl font-bold backdrop-blur-xs">
          {{ expenses.length }} khoản chi
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-10 bg-white rounded-2xl border border-slate-200/80">
      <div class="inline-block animate-spin w-6 h-6 border-2 border-amber-600 border-t-transparent rounded-full mb-2"></div>
      <p class="text-xs text-slate-500">Đang tải danh sách chi phí...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="expenses.length === 0" class="text-center py-12 bg-white rounded-2xl border border-slate-200/80">
      <div class="text-3xl mb-2">🧾</div>
      <p class="text-sm font-bold text-slate-700">Hôm nay chưa có khoản chi nào</p>
      <p class="text-xs text-slate-400 mt-1">Bấm "Ghi Nhận Chi Phí" để thêm tiền xăng xe, sửa xe hoặc ăn trưa.</p>
    </div>

    <!-- Expense List Cards -->
    <div v-else class="space-y-3 pb-20">
      <div 
        v-for="e in expenses" 
        :key="e.id"
        class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs hover:border-amber-300 transition-all flex items-center justify-between"
      >
        <div class="space-y-1">
          <div class="flex items-center gap-2">
            <span class="text-sm font-bold text-slate-900">{{ e.description }}</span>
            <span class="text-[11px] font-mono px-2 py-0.5 bg-slate-100 text-slate-600 rounded">
              {{ e.expenseDate }}
            </span>
          </div>
          <div v-if="e.notes" class="text-xs text-slate-500 italic">
            Ghi chú: {{ e.notes }}
          </div>
          <div class="text-[10px] text-slate-400">
            Nhân viên: {{ e.employeeName || 'Tôi' }}
          </div>
        </div>

        <div class="text-right">
          <div class="text-sm font-extrabold text-rose-600">-{{ formatCurrency(e.amount) }}</div>
        </div>
      </div>
    </div>

    <!-- Modal Ghi Nhận Chi Phí -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white w-full max-w-md rounded-2xl border border-slate-200 shadow-xl overflow-hidden animate-in fade-in zoom-in duration-150">
        <div class="p-4 border-b border-slate-100 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-base">⛽</span>
            <h3 class="text-sm font-bold text-slate-900">Ghi Nhận Chi Phí Mới</h3>
          </div>
          <button @click="showModal = false" class="text-slate-400 hover:text-slate-600 text-sm">✕</button>
        </div>

        <form @submit.prevent="submitCreateExpense" class="p-4 space-y-3">
          <!-- Quick Preset Chips -->
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1.5">Chọn nhanh khoản chi</label>
            <div class="flex flex-wrap gap-1.5">
              <button 
                type="button"
                v-for="preset in presets" 
                :key="preset.text"
                @click="applyPreset(preset)"
                class="px-2.5 py-1 text-[11px] rounded-lg border border-slate-200 hover:border-amber-400 hover:bg-amber-50 text-slate-700 font-medium transition-all active:scale-95"
              >
                {{ preset.text }}
              </button>
            </div>
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Mô tả khoản chi *</label>
            <input 
              v-model="form.description" 
              type="text" 
              required
              placeholder="VD: Đổ xăng xe Wave, Ăn trưa..."
              class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-amber-500/30 focus:border-amber-500 focus:outline-none"
            />
          </div>

          <div class="grid grid-cols-2 gap-2">
            <div>
              <label class="block text-xs font-bold text-slate-700 mb-1">Số tiền (VNĐ) *</label>
              <input 
                v-model.number="form.amount" 
                type="number" 
                min="1"
                required
                placeholder="50000"
                class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-amber-500/30 focus:border-amber-500 focus:outline-none"
              />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-700 mb-1">Ngày chi</label>
              <input 
                v-model="form.expenseDate" 
                type="date" 
                required
                class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Ghi chú bổ sung</label>
            <textarea 
              v-model="form.notes" 
              rows="2"
              placeholder="VD: Đổ 50k xăng cây xăng Petrolimex góc đường..."
              class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-amber-500/30 focus:border-amber-500 focus:outline-none"
            ></textarea>
          </div>

          <div class="pt-2 flex items-center justify-end gap-2">
            <button 
              type="button" 
              @click="showModal = false"
              class="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition-all"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="submitting"
              class="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold rounded-xl shadow-sm transition-all active:scale-95 disabled:opacity-50"
            >
              {{ submitting ? 'Đang lưu...' : 'Lưu Khoản Chi' }}
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
import expenseService from '../services/expenseService'

// Mặc định lấy user id của shipper từ localStorage hoặc mock id có sẵn trong data seed
const currentEmployeeId = ref(localStorage.getItem('userId') || '11111111-1111-1111-1111-111111111111')
const selectedDate = ref(new Date().toISOString().slice(0, 10))

const expenses = ref([])
const loading = ref(false)
const showModal = ref(false)
const submitting = ref(false)

const form = ref({
  employeeId: currentEmployeeId.value,
  expenseDate: selectedDate.value,
  description: '',
  amount: null,
  notes: ''
})

const presets = [
  { text: '⛽ Đổ xăng 50.000đ', desc: 'Đổ xăng xe máy', amount: 50000 },
  { text: '⛽ Đổ xăng 70.000đ', desc: 'Đổ xăng xe máy', amount: 70000 },
  { text: '🍛 Cơm trưa 35.000đ', desc: 'Tiền ăn trưa', amount: 35000 },
  { text: '🔧 Vá xe 20.000đ', desc: 'Vá xe máy', amount: 20000 },
  { text: '🅿️ Gửi xe 5.000đ', desc: 'Vé gửi xe', amount: 5000 }
]

const totalAmount = computed(() => {
  return expenses.value.reduce((acc, curr) => acc + (curr.amount || 0), 0)
})

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0)
}

const loadExpenses = async () => {
  loading.value = true
  try {
    const res = await expenseService.getDaily(currentEmployeeId.value, selectedDate.value)
    expenses.value = res.data || []
  } catch (err) {
    console.error('Lỗi tải danh sách chi phí:', err)
  } finally {
    loading.value = false
  }
}

const openModal = () => {
  form.value = {
    employeeId: currentEmployeeId.value,
    expenseDate: selectedDate.value,
    description: '',
    amount: null,
    notes: ''
  }
  showModal.value = true
}

const applyPreset = (preset) => {
  form.value.description = preset.desc
  form.value.amount = preset.amount
}

const submitCreateExpense = async () => {
  if (!form.value.amount || form.value.amount <= 0) {
    alert('Số tiền chi phí phải lớn hơn 0!')
    return
  }
  submitting.value = true
  try {
    const res = await expenseService.create(form.value)
    alert(res.message || 'Ghi nhận chi phí thành công!')
    showModal.value = false
    loadExpenses()
  } catch (err) {
    alert(err.message || 'Lỗi khi ghi nhận chi phí')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadExpenses()
})
</script>
