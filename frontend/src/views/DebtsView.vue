<template>
  <EmployeeLayout :debt-count="debtReports.length">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <h2 class="text-lg font-bold text-slate-900 tracking-tight">Sổ Theo Dõi Công Nợ Khách Hàng</h2>
        <p class="text-xs text-slate-500 mt-0.5">Danh sách các hóa đơn giao gas chưa thanh toán cần thu hồi tiền mặt</p>
      </div>

      <div class="px-4 py-2 bg-rose-50 border border-rose-200 rounded-lg text-right self-start sm:self-auto">
        <div class="text-[10px] font-bold uppercase tracking-wider text-rose-600">Tổng Nợ Cần Thu</div>
        <div class="text-base font-extrabold text-rose-700">{{ formatCurrency(totalDebtAmount) }}</div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="debtReports.length === 0" class="bg-white rounded-xl border border-dashed border-slate-300 p-12 text-center">
      <h3 class="text-sm font-semibold text-slate-700">Hiện tại không có hóa đơn nợ nào</h3>
      <p class="text-xs text-slate-500 mt-1">Toàn bộ khách hàng trong ca làm việc đã hoàn tất thanh toán.</p>
    </div>

    <!-- Debt List -->
    <div v-else class="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-2xs divide-y divide-slate-100">
      <div 
        v-for="item in debtReports" 
        :key="item.id"
        class="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-slate-50 transition-colors"
      >
        <div>
          <div class="flex items-center gap-2 mb-1">
            <h4 class="text-sm font-bold text-slate-900">{{ item.customerName }}</h4>
            <span class="px-2 py-0.5 bg-slate-100 text-slate-700 border border-slate-200 rounded text-[11px] font-semibold">
              {{ item.containerType }} (x{{ item.quantity }})
            </span>
          </div>
          <div class="text-xs text-slate-500">
            Ngày xuất phiếu: <span class="text-slate-700 font-medium">{{ item.reportDate }}</span>
            <span v-if="item.notes" class="italic ml-2">"{{ item.notes }}"</span>
          </div>
        </div>

        <div class="flex items-center justify-between sm:justify-end gap-3 pt-2 sm:pt-0 border-t border-slate-100 sm:border-0">
          <div class="text-left sm:text-right">
            <div class="text-sm sm:text-base font-bold text-rose-600">
              {{ formatCurrency(item.total) }}
            </div>
            <div class="text-[10px] text-slate-400">Tiền còn nợ</div>
          </div>

          <button 
            @click="markAsCollected(item)"
            class="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
          >
            Đã Thu Tiền
          </button>
        </div>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import deliveryReportService from '../services/deliveryReportService.js'

const allReports = ref([])

const debtReports = computed(() => {
  return allReports.value.filter(r => r.paymentStatus === 'DEBT')
})

const totalDebtAmount = computed(() => {
  return debtReports.value.reduce((sum, r) => sum + (r.total || 0), 0)
})

function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0)
}

async function loadDebts() {
  try {
    const res = await deliveryReportService.getTodayReports()
    if (res && res.data) {
      allReports.value = res.data
    } else {
      allReports.value = []
    }
  } catch (err) {
    console.error('Lỗi tải sổ nợ:', err)
  }
}

async function markAsCollected(item) {
  if (!confirm(`Xác nhận đã thu đủ tiền (${formatCurrency(item.total)}) từ khách "${item.customerName}"?`)) return
  try {
    await deliveryReportService.togglePaymentStatus(item.id)
    await loadDebts()
  } catch (err) {
    alert('Không thể cập nhật trạng thái thu tiền: ' + err.message)
  }
}

onMounted(() => {
  loadDebts()
})
</script>
