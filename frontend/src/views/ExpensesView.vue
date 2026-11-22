<template>
  <EmployeeLayout>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <h2 class="text-lg font-bold text-slate-900 tracking-tight">Chi Phí Vận Hành Trong Ca</h2>
        <p class="text-xs text-slate-500 mt-0.5">Khai báo các chi phí phát sinh (xăng xe, ăn trưa, bảo dưỡng xe) để quyết toán</p>
      </div>

      <button 
        @click="openModal"
        class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors self-start sm:self-auto"
      >
        <span>➕ Ghi Nhận Chi Phí Mới</span>
      </button>
    </div>

    <!-- Filter & Summary -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3.5 mb-6">
      <div class="bg-white p-3.5 rounded-xl border border-slate-200 flex items-center justify-between shadow-2xs">
        <span class="text-xs font-semibold text-slate-700">Ngày ghi nhận:</span>
        <input 
          v-model="selectedDate" 
          @change="loadExpenses"
          type="date" 
          class="text-xs font-semibold text-slate-800 bg-slate-50 border border-slate-300 rounded-lg px-2.5 py-1 focus:outline-none focus:border-slate-800"
        />
      </div>

      <div class="bg-white border border-slate-200 p-3.5 rounded-xl sm:col-span-2 flex items-center justify-between shadow-2xs">
        <div>
          <div class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">Tổng Chi Phí Trong Ngày</div>
          <div class="text-xl font-bold text-slate-900 mt-0.5">{{ formatCurrency(totalAmount) }}</div>
        </div>
        <div class="text-xs bg-slate-100 text-slate-700 px-3 py-1 rounded-md font-semibold border border-slate-200">
          {{ expenses.length }} khoản chi
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-16 bg-white rounded-xl border border-slate-200">
      <div class="w-6 h-6 border-2 border-slate-300 border-t-slate-800 rounded-full animate-spin mx-auto mb-2"></div>
      <p class="text-xs text-slate-500">Đang tải chi phí...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="expenses.length === 0" class="text-center py-12 bg-white rounded-xl border border-dashed border-slate-300">
      <p class="text-sm font-semibold text-slate-700">Chưa phát sinh khoản chi phí nào hôm nay</p>
      <p class="text-xs text-slate-500 mt-1">Bấm nút trên để khai báo các khoản xăng xe hoặc ăn trưa.</p>
    </div>

    <!-- Expense List -->
    <div v-else class="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-2xs divide-y divide-slate-100">
      <div
        v-for="e in expenses"
        :key="e.id"
        class="p-4 flex items-center justify-between gap-3 hover:bg-slate-50 transition-colors"
      >
        <div class="flex-1 min-w-0">
          <h4 class="text-sm font-bold text-slate-900 mb-1">{{ e.description }}</h4>
          <div class="text-xs text-slate-500 flex items-center gap-2">
            <span>{{ e.expenseDate }}</span>
            <span v-if="e.notes" class="text-slate-300">•</span>
            <span v-if="e.notes" class="text-slate-600 italic truncate">{{ e.notes }}</span>
          </div>
        </div>

        <div class="text-right">
          <div class="text-base font-bold text-slate-900">
            {{ formatCurrency(e.amount) }}
          </div>
          <div class="text-[10px] text-slate-400">Đã chi</div>
        </div>
      </div>
    </div>

    <!-- Create Expense Modal -->
    <div
      v-if="modal.isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs transition-opacity"
      @click.self="closeModal"
    >
      <div class="bg-white w-full max-w-md rounded-xl shadow-lg border border-slate-200 overflow-hidden">
        <div class="px-5 py-3.5 border-b border-slate-200 flex items-center justify-between bg-slate-50">
          <h3 class="text-sm font-bold text-slate-900">Khai Báo Chi Phí Mới</h3>
          <button @click="closeModal" class="p-1 text-slate-400 hover:text-slate-600">✕</button>
        </div>

        <form @submit.prevent="submitCreateExpense" class="p-5 space-y-4">
          <div v-if="errors.general" class="p-3 bg-rose-50 border border-rose-200 rounded-lg text-xs text-rose-700">
            {{ errors.general }}
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Mẫu Khoản Chi Nhanh</label>
            <div class="grid grid-cols-3 gap-2">
              <button
                type="button"
                v-for="p in PRESETS"
                :key="p.desc"
                @click="applyPreset(p)"
                class="p-2 bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 rounded-lg text-xs font-medium transition-colors"
              >
                {{ p.desc }}
              </button>
            </div>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">
              Nội Dung Chi <span class="text-rose-500">*</span>
            </label>
            <input
              type="text"
              v-model="expenseForm.description"
              @input="delete errors.description"
              placeholder="VD: Đổ xăng xe máy Wave..."
              :class="[
                'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                errors.description ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
              ]"
            />
            <p v-if="errors.description" class="text-xs text-rose-600 mt-1 font-medium">
              {{ errors.description }}
            </p>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">
              Số Tiền (VNĐ) <span class="text-rose-500">*</span>
            </label>
            <input
              type="number"
              step="1000"
              v-model.number="expenseForm.amount"
              @input="delete errors.amount"
              placeholder="VD: 50000"
              :class="[
                'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                errors.amount ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
              ]"
            />
            <p v-if="errors.amount" class="text-xs text-rose-600 mt-1 font-medium">
              {{ errors.amount }}
            </p>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Ghi Chú Kèm Theo</label>
            <input
              type="text"
              v-model="expenseForm.notes"
              placeholder="VD: Có biên nhận cây xăng..."
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
              Lưu Chi Phí
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
import expenseService from '../services/expenseService.js'

const PRESETS = [
  { desc: 'Tiền xăng xe', amount: 50000 },
  { desc: 'Ăn trưa', amount: 40000 },
  { desc: 'Sửa xe / Vá lốp', amount: 30000 }
]

const selectedDate = ref(new Date().toISOString().substring(0, 10))
const expenses = ref([])
const loading = ref(false)
const submitting = ref(false)
const errors = ref({})

const modal = reactive({
  isOpen: false
})

const expenseForm = reactive({
  description: '',
  amount: null,
  notes: ''
})

const totalAmount = computed(() => {
  return expenses.value.reduce((sum, e) => sum + (e.amount || 0), 0)
})

function formatCurrency(val) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

function openModal() {
  modal.isOpen = true
  errors.value = {}
  expenseForm.description = ''
  expenseForm.amount = null
  expenseForm.notes = ''
}

function closeModal() {
  modal.isOpen = false
  errors.value = {}
}

function applyPreset(p) {
  expenseForm.description = p.desc
  expenseForm.amount = p.amount
  delete errors.value.description
  delete errors.value.amount
}

function validateExpenseForm() {
  const errs = {}
  if (!expenseForm.description || expenseForm.description.trim().length < 2) {
    errs.description = 'Nội dung chi phải có ít nhất 2 ký tự'
  }
  if (!expenseForm.amount || expenseForm.amount <= 0) {
    errs.amount = 'Số tiền chi phải lớn hơn 0 VNĐ'
  }
  errors.value = errs
  return Object.keys(errs).length === 0
}

async function loadExpenses() {
  loading.value = true
  try {
    const data = await expenseService.getExpensesByDate(selectedDate.value)
    expenses.value = data || []
  } catch (err) {
    console.error('Lỗi tải danh sách chi phí:', err)
  } finally {
    loading.value = false
  }
}

async function submitCreateExpense() {
  if (!validateExpenseForm()) return

  submitting.value = true
  errors.value = {}
  try {
    await expenseService.createExpense({
      description: expenseForm.description.trim(),
      amount: expenseForm.amount,
      expenseDate: selectedDate.value,
      notes: expenseForm.notes ? expenseForm.notes.trim() : null
    })

    closeModal()
    await loadExpenses()
  } catch (err) {
    if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
      errors.value = err.fieldErrors
    } else {
      errors.value = { general: err.message || 'Lưu chi phí thất bại' }
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadExpenses()
})
</script>
