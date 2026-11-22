<template>
  <EmployeeLayout :wide="true" @refresh="loadAllData">
    <!-- Header Section (Executive Corporate Command Center) -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-slate-900"></span>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">Trung Tâm Điều Hành Doanh Nghiệp</h2>
          <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-slate-900 text-white">Admin Hub</span>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Giám sát tổng quan tình hình xuất nhập tồn kho gas, doanh thu ca trực và đối soát dòng tiền</p>
      </div>

      <div class="flex items-center gap-2">
        <span class="text-xs font-semibold text-slate-500">Ngày làm việc:</span>
        <input 
          type="date" 
          v-model="currentDate" 
          @change="loadAllData"
          class="text-xs font-semibold text-slate-800 bg-white border border-slate-300 rounded-lg px-3 py-1.5 shadow-2xs focus:outline-none focus:border-slate-800 transition-colors cursor-pointer"
        />
      </div>
    </div>

    <!-- Executive KPI Row: 4 Metric Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <!-- 1. Doanh Thu Hôm Nay -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="flex items-center justify-between mb-1">
          <span class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase">Doanh Thu Ca Trực</span>
          <span class="text-[10px] px-1.5 py-0.5 rounded font-mono font-semibold bg-slate-100 text-slate-600">Hôm nay</span>
        </div>
        <div class="text-2xl font-bold font-mono text-slate-900 tracking-tight">
          {{ formatCurrency(salesMetrics.totalAmount) }}
        </div>
        <div class="text-xs text-slate-500 mt-1 flex items-center justify-between">
          <span>Tổng số phiếu: <strong class="text-slate-800">{{ salesMetrics.totalBills }}</strong></span>
          <span>Bình đã giao: <strong class="text-slate-800">{{ salesMetrics.totalQuantity }}</strong></span>
        </div>
      </div>

      <!-- 2. Thực Thu Tiền Mặt (Shipper đang giữ) -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="flex items-center justify-between mb-1">
          <span class="text-[11px] font-semibold tracking-wider text-emerald-600 uppercase">Thực Thu Tiền Mặt</span>
          <span class="text-[10px] px-1.5 py-0.5 rounded font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">Đã thu</span>
        </div>
        <div class="text-2xl font-bold font-mono text-emerald-600 tracking-tight">
          {{ formatCurrency(salesMetrics.paidAmount) }}
        </div>
        <div class="text-xs text-slate-500 mt-1">
          Tiền mặt shipper giữ cần nộp két đối soát cuối ca
        </div>
      </div>

      <!-- 3. Công Nợ Khách Hàng Hôm Nay -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs" :class="salesMetrics.debtAmount > 0 ? 'border-rose-300 bg-rose-50/15' : ''">
        <div class="flex items-center justify-between mb-1">
          <span class="text-[11px] font-semibold tracking-wider text-rose-600 uppercase">Công Nợ Khách Hàng</span>
          <span class="text-[10px] px-1.5 py-0.5 rounded font-bold bg-rose-50 text-rose-700 border border-rose-200">Chưa thu</span>
        </div>
        <div class="text-2xl font-bold font-mono text-rose-600 tracking-tight">
          {{ formatCurrency(salesMetrics.debtAmount) }}
        </div>
        <div class="text-xs text-slate-500 mt-1">
          {{ salesMetrics.debtBillsCount }} phiếu giao ghi nợ cần đôn đốc
        </div>
      </div>

      <!-- 4. Nợ Vỏ Rỗng Khách Hàng -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs" :class="salesMetrics.debtShells > 0 ? 'border-amber-300 bg-amber-50/15' : ''">
        <div class="flex items-center justify-between mb-1">
          <span class="text-[11px] font-semibold tracking-wider text-amber-700 uppercase">Vỏ Bình Khách Nợ</span>
          <span class="text-[10px] px-1.5 py-0.5 rounded font-bold bg-amber-50 text-amber-700 border border-amber-200">Kho nợ</span>
        </div>
        <div class="text-2xl font-bold font-mono" :class="salesMetrics.debtShells > 0 ? 'text-amber-700' : 'text-slate-900'">
          {{ salesMetrics.debtShells }} <span class="text-xs font-normal text-slate-500">vỏ</span>
        </div>
        <div class="text-xs text-slate-500 mt-1">
          {{ salesMetrics.debtShells > 0 ? 'Cần thu hồi về bãi vỏ công ty' : 'Đã đối soát trả đủ 100% vỏ' }}
        </div>
      </div>
    </div>

    <!-- Main Workspace Split: Tồn Kho & Cảnh Báo (Trái) + Đối Soát Ca & Lối Tắt (Phải) -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
      <!-- Cột Trái (2 cols): Bảng Tồn Kho Gas & Vỏ Rỗng Thời Gian Thực -->
      <div class="lg:col-span-2 bg-white border border-slate-200 rounded-xl overflow-hidden shadow-2xs flex flex-col justify-between">
        <div class="p-4 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-slate-800"></span>
              Tình Trạng Tồn Kho Gas Thực Tế
            </h3>
            <p class="text-xs text-slate-500 mt-0.5">Giám sát số lượng bình gas đầy sẵn sàng xuất kho và vỏ rỗng tồn bãi</p>
          </div>
          <router-link 
            to="/inventory" 
            class="text-xs font-semibold text-slate-700 hover:text-slate-900 hover:underline"
          >
            Vào Quản Lý Kho &rarr;
          </router-link>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-left border-collapse text-xs">
            <thead>
              <tr class="bg-slate-50 border-b border-slate-200 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
                <th class="py-2.5 px-4">Loại Bình Gas</th>
                <th class="py-2.5 px-4 text-center">Bình Đầy Sẵn Có</th>
                <th class="py-2.5 px-4 text-center">Vỏ Rỗng Tồn Bãi</th>
                <th class="py-2.5 px-4 text-center">Ngưỡng An Toàn</th>
                <th class="py-2.5 px-4 text-center">Trạng Thái Kho</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              <tr v-for="inv in inventoryList" :key="inv.id" class="hover:bg-slate-50/70 transition-colors">
                <td class="py-3 px-4 font-bold text-slate-800">
                  {{ inv.containerType }}
                </td>
                <td class="py-3 px-4 text-center font-mono font-bold text-slate-900 text-sm">
                  {{ inv.fullQuantity }} <span class="text-[10px] font-normal text-slate-500">bình</span>
                </td>
                <td class="py-3 px-4 text-center font-mono text-slate-600">
                  {{ inv.emptyQuantity }} <span class="text-[10px] font-normal text-slate-400">vỏ</span>
                </td>
                <td class="py-3 px-4 text-center font-mono text-slate-500">
                  {{ inv.warningThreshold }}
                </td>
                <td class="py-3 px-4 text-center">
                  <span 
                    v-if="inv.fullQuantity <= inv.warningThreshold"
                    class="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold bg-rose-50 text-rose-700 border border-rose-200"
                  >
                    Báo động: Sắp hết
                  </span>
                  <span 
                    v-else
                    class="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200"
                  >
                    Đủ lượng cung ứng
                  </span>
                </td>
              </tr>
              <tr v-if="inventoryList.length === 0">
                <td colspan="5" class="py-6 text-center text-slate-400 text-xs">
                  Đang tải dữ liệu tồn kho...
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="p-3 bg-slate-50 border-t border-slate-100 flex items-center justify-between text-xs text-slate-600">
          <span>Tổng bình đầy toàn kho: <strong class="text-slate-900 font-mono">{{ totalFullStock }}</strong> bình</span>
          <span>Tổng vỏ rỗng sẵn sàng nạp: <strong class="text-slate-900 font-mono">{{ totalEmptyStock }}</strong> vỏ</span>
        </div>
      </div>

      <!-- Cột Phải (1 col): Đối Soát Quyết Toán Ca & Chi Phí -->
      <div class="bg-white border border-slate-200 rounded-xl p-5 shadow-2xs flex flex-col justify-between">
        <div>
          <div class="flex items-center justify-between pb-3 border-b border-slate-100 mb-3">
            <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight">Đối Soát Nộp Két Ca Trực</h3>
            <span class="text-[10px] font-semibold text-slate-400">Quyết toán ca</span>
          </div>

          <div class="space-y-3 text-xs mb-4">
            <div class="flex justify-between py-1.5 border-b border-slate-100">
              <span class="text-slate-500">Tổng tiền thực thu từ khách:</span>
              <span class="font-mono font-bold text-slate-800">{{ formatCurrency(salesMetrics.paidAmount) }}</span>
            </div>
            <div class="flex justify-between py-1.5 border-b border-slate-100">
              <span class="text-slate-500">Chi phí vận hành ca (xăng xe/ăn trưa):</span>
              <span class="font-mono font-bold text-rose-600">- {{ formatCurrency(expensesMetrics.totalAmount) }}</span>
            </div>
            <div class="flex justify-between py-2 bg-slate-50 px-2.5 rounded-lg border border-slate-200">
              <span class="font-bold text-slate-900">Tiền mặt thực tế nộp két:</span>
              <span class="font-mono font-extrabold text-emerald-700 text-sm">
                {{ formatCurrency(Math.max(0, salesMetrics.paidAmount - expensesMetrics.totalAmount)) }}
              </span>
            </div>
          </div>
        </div>

        <div>
          <router-link 
            to="/admin/view_report" 
            class="w-full inline-flex items-center justify-center gap-1.5 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg transition-colors shadow-2xs"
          >
            <span>Xuất Báo Cáo & In Phiếu Đối Soát</span>
            <span>&rarr;</span>
          </router-link>
        </div>
      </div>
    </div>

    <!-- Phân Hệ Quản Trị Trung Tâm (Executive Quick Launchpad) -->
    <div>
      <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight mb-3">Phân Hệ Nghiệp Vụ Doanh Nghiệp</h3>
      
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4">
        <!-- 1. Quản Lý Kho Gas -->
        <router-link 
          to="/inventory" 
          class="bg-white border border-slate-200 rounded-xl p-4 shadow-2xs hover:border-slate-400 hover:shadow-xs transition-all flex flex-col justify-between group"
        >
          <div>
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold text-slate-900 group-hover:text-slate-700">1. Kho Gas & Vỏ Rỗng</span>
              <span class="text-slate-400 group-hover:translate-x-0.5 transition-transform">&rarr;</span>
            </div>
            <p class="text-xs text-slate-500 leading-relaxed">
              Nhập hàng từ nhà máy, kiểm soát xuất kho giao hàng, quản lý vỏ rỗng và điều chỉnh ngưỡng an toàn.
            </p>
          </div>
          <div class="mt-4 pt-2 border-t border-slate-100 text-[11px] font-semibold text-slate-600">
            Truy cập phân hệ &raquo;
          </div>
        </router-link>

        <!-- 2. Đối Soát Thu Chi Ca Trực -->
        <router-link 
          to="/admin/view_report" 
          class="bg-white border border-slate-200 rounded-xl p-4 shadow-2xs hover:border-slate-400 hover:shadow-xs transition-all flex flex-col justify-between group"
        >
          <div>
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold text-slate-900 group-hover:text-slate-700">2. Đối Soát & Báo Cáo</span>
              <span class="text-slate-400 group-hover:translate-x-0.5 transition-transform">&rarr;</span>
            </div>
            <p class="text-xs text-slate-500 leading-relaxed">
              Kiểm tra doanh thu nhân viên theo ngày, tổng kết tiền nộp két, xuất file Excel và in phiếu quyết toán.
            </p>
          </div>
          <div class="mt-4 pt-2 border-t border-slate-100 text-[11px] font-semibold text-slate-600">
            Truy cập phân hệ &raquo;
          </div>
        </router-link>

        <!-- 3. Quản Lý Nhân Sự & Phân Quyền -->
        <router-link 
          to="/users" 
          class="bg-white border border-slate-200 rounded-xl p-4 shadow-2xs hover:border-slate-400 hover:shadow-xs transition-all flex flex-col justify-between group"
        >
          <div>
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold text-slate-900 group-hover:text-slate-700">3. Nhân Sự & Phân Quyền</span>
              <span class="text-slate-400 group-hover:translate-x-0.5 transition-transform">&rarr;</span>
            </div>
            <p class="text-xs text-slate-500 leading-relaxed">
              Cấp tài khoản nhân viên, phân quyền ADMIN / SHIPPER, quản lý lương cơ bản và khóa tài khoản khi cần.
            </p>
          </div>
          <div class="mt-4 pt-2 border-t border-slate-100 text-[11px] font-semibold text-slate-600">
            Truy cập phân hệ &raquo;
          </div>
        </router-link>

        <!-- 4. Danh Bạ Khách Hàng -->
        <router-link 
          to="/customers" 
          class="bg-white border border-slate-200 rounded-xl p-4 shadow-2xs hover:border-slate-400 hover:shadow-xs transition-all flex flex-col justify-between group"
        >
          <div>
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold text-slate-900 group-hover:text-slate-700">4. Danh Bạ Khách Hàng</span>
              <span class="text-slate-400 group-hover:translate-x-0.5 transition-transform">&rarr;</span>
            </div>
            <p class="text-xs text-slate-500 leading-relaxed">
              Quản lý hồ sơ khách hàng, phân loại đại lý / nhà hàng, định vị tọa độ GPS dẫn đường giao nhận.
            </p>
          </div>
          <div class="mt-4 pt-2 border-t border-slate-100 text-[11px] font-semibold text-slate-600">
            Truy cập phân hệ &raquo;
          </div>
        </router-link>

        <!-- 5. Chi Phí Vận Hành -->
        <router-link 
          to="/expenses" 
          class="bg-white border border-slate-200 rounded-xl p-4 shadow-2xs hover:border-slate-400 hover:shadow-xs transition-all flex flex-col justify-between group"
        >
          <div>
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold text-slate-900 group-hover:text-slate-700">5. Chi Phí Vận Hành</span>
              <span class="text-slate-400 group-hover:translate-x-0.5 transition-transform">&rarr;</span>
            </div>
            <p class="text-xs text-slate-500 leading-relaxed">
              Kiểm soát các khoản tạm ứng tiền xăng xe, bảo trì xe máy và ăn uống ca trực của đội ngũ shipper.
            </p>
          </div>
          <div class="mt-4 pt-2 border-t border-slate-100 text-[11px] font-semibold text-slate-600">
            Truy cập phân hệ &raquo;
          </div>
        </router-link>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import { deliveryReportService } from '../services/deliveryReportService.js'
import { inventoryService } from '../services/inventoryService.js'
import { expenseService } from '../services/expenseService.js'

const currentDate = ref(new Date().toISOString().split('T')[0])
const reports = ref([])
const inventoryList = ref([])
const expensesList = ref([])

const salesMetrics = computed(() => {
  const totalBills = reports.value.length
  const totalQuantity = reports.value.reduce((sum, r) => sum + (Number(r.quantity) || 0), 0)
  const totalAmount = reports.value.reduce((sum, r) => sum + (Number(r.total) || 0), 0)
  const paidAmount = reports.value
    .filter(r => r.paymentStatus === 'PAID')
    .reduce((sum, r) => sum + (Number(r.actualReceived !== undefined ? r.actualReceived : r.total) || 0), 0)
  const debtAmount = Math.max(0, totalAmount - paidAmount)
  const debtBillsCount = reports.value.filter(r => r.paymentStatus === 'DEBT').length
  const debtShells = reports.value.reduce((sum, r) => sum + (Number(r.debtShells) || 0), 0)

  return { totalBills, totalQuantity, totalAmount, paidAmount, debtAmount, debtBillsCount, debtShells }
})

const expensesMetrics = computed(() => {
  const totalAmount = expensesList.value.reduce((sum, e) => sum + (Number(e.amount) || 0), 0)
  return { totalAmount }
})

const totalFullStock = computed(() => {
  return inventoryList.value.reduce((sum, i) => sum + (Number(i.fullQuantity) || 0), 0)
})

const totalEmptyStock = computed(() => {
  return inventoryList.value.reduce((sum, i) => sum + (Number(i.emptyQuantity) || 0), 0)
})

function formatCurrency(amount) {
  if (amount === undefined || amount === null) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

async function loadAllData() {
  try {
    const [repRes, invRes, expRes] = await Promise.all([
      deliveryReportService.getTodayReports(currentDate.value),
      inventoryService.getInventories().catch(() => ({ data: [] })),
      expenseService.getTodayExpenses(currentDate.value).catch(() => ({ data: [] }))
    ])

    reports.value = repRes.data || []
    inventoryList.value = invRes.data || []
    expensesList.value = expRes.data || []
  } catch (err) {
    console.error('Lỗi nạp dữ liệu Dashboard Admin:', err)
  }
}

onMounted(() => {
  loadAllData()
})
</script>
