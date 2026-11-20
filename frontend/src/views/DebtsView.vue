<template>
  <EmployeeLayout :debt-count="debtReports.length">
    <!-- Header -->
    <div class="flex items-center justify-between mb-5">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2 h-2 rounded-full bg-rose-500"></span>
          <h2 class="text-xl font-extrabold tracking-tight text-slate-900">Sổ Nợ Cần Thu</h2>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Tổng hợp toàn bộ các đơn gas khách hàng chưa thanh toán để tiện đi thu tiền</p>
      </div>

      <!-- Total Debt Badge -->
      <div class="px-3.5 py-1.5 bg-rose-50 border border-rose-200/80 rounded-2xl text-right">
        <div class="text-[10px] font-bold uppercase tracking-wider text-rose-600">Tổng Nợ Cần Thu</div>
        <div class="text-sm font-black text-rose-700 tracking-tight">{{ formatCurrency(totalDebtAmount) }}</div>
      </div>
    </div>

    <!-- Empty State if no debts -->
    <div v-if="debtReports.length === 0" class="bg-white rounded-2xl border border-dashed border-slate-200 p-12 text-center">
      <div class="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-xl mx-auto mb-3">
        🎉
      </div>
      <h3 class="text-sm font-bold text-slate-800">Không có hóa đơn nợ nào!</h3>
      <p class="text-xs text-slate-500 mt-1 max-w-xs mx-auto">
        Tất cả khách hàng đã thanh toán đầy đủ tiền gas.
      </p>
    </div>

    <!-- Debt Bills List -->
    <div v-else class="space-y-3">
      <div 
        v-for="item in debtReports" 
        :key="item.id"
        class="bg-white p-4 rounded-2xl border border-rose-200/70 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3"
      >
        <div>
          <div class="flex items-center gap-2">
            <h4 class="text-sm font-bold text-slate-900">{{ item.customerName }}</h4>
            <span class="px-2 py-0.5 bg-amber-50 text-amber-800 border border-amber-200 rounded text-[11px] font-semibold">
              {{ item.containerType }} (x{{ item.quantity }})
            </span>
          </div>
          <div class="text-xs text-slate-500 mt-1">
            Ngày giao: <span class="text-slate-700 font-medium">{{ item.reportDate }}</span>
            <span v-if="item.notes" class="italic ml-2">"{{ item.notes }}"</span>
          </div>
        </div>

        <div class="flex items-center justify-between sm:justify-end gap-3 pt-2 sm:pt-0 border-t border-slate-100 sm:border-0">
          <div class="text-left sm:text-right">
            <div class="text-sm sm:text-base font-black text-rose-600 tracking-tight">
              {{ formatCurrency(item.total) }}
            </div>
            <div class="text-[10px] text-slate-400">Tiền còn nợ</div>
          </div>

          <button 
            @click="markAsCollected(item)"
            class="px-3 py-2 bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white text-xs font-bold rounded-xl shadow-xs transition-all flex items-center gap-1.5"
          >
            <span>💰</span>
            <span>Đã Thu Tiền</span>
          </button>
        </div>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import { deliveryReportService } from '../services/deliveryReportService'

const reports = ref([])

const debtReports = computed(() => {
  return reports.value.filter(r => r.paymentStatus === 'DEBT')
})

const totalDebtAmount = computed(() => {
  return debtReports.value.reduce((sum, r) => sum + (Number(r.total) || 0), 0)
})

const loadReports = async () => {
  const res = await deliveryReportService.getTodayReports()
  reports.value = res.data || []
}

const markAsCollected = async (item) => {
  if (confirm(`Xác nhận đã thu đủ ${formatCurrency(item.total)} từ khách [${item.customerName}]?`)) {
    await deliveryReportService.togglePaymentStatus(item.id)
    loadReports()
  }
}

const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

onMounted(() => {
  loadReports()
})
</script>

