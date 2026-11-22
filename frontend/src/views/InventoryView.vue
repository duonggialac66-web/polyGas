<template>
  <EmployeeLayout :wide="true" @refresh="fetchInventories">
    <!-- Header Section (Corporate Enterprise) -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-slate-900"></span>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">Quản Lý Kho Gas & Vỏ Rỗng</h2>
          <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-slate-100 text-slate-700 border border-slate-200">Kho Bãi</span>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Kiểm soát xuất nhập bình gas đầy, luân chuyển vỏ rỗng và cảnh báo ngưỡng an toàn chống đứt hàng</p>
      </div>

      <div class="flex items-center gap-2 flex-wrap">
        <button 
          @click="openStockModalWithMode('IMPORT', null)"
          class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
          </svg>
          <span>Nhập Kho Gas</span>
        </button>

        <button 
          @click="openStockModalWithMode('EXPORT', null)"
          class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 10l7-7m0 0l7 7m-7-7v18" />
          </svg>
          <span>Xuất Kho Gas</span>
        </button>
      </div>
    </div>

    <!-- 4 KPI Summary Cards (Clean Corporate) -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-3.5 mb-6">
      <!-- 1. Bình Đầy Sẵn Có -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-emerald-700 uppercase mb-1">Bình Đầy Sẵn Sàng</div>
        <div class="text-2xl font-bold font-mono text-emerald-700">
          {{ stats.totalFull }} <span class="text-xs font-normal text-slate-500">bình</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Đã kiểm định, sẵn sàng xuất giao</div>
      </div>

      <!-- 2. Vỏ Rỗng Tồn Bãi -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-600 uppercase mb-1">Vỏ Bình Rỗng</div>
        <div class="text-2xl font-bold font-mono text-slate-800">
          {{ stats.totalEmpty }} <span class="text-xs font-normal text-slate-500">vỏ</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Chờ xe tải chở đi nạp lại tại trạm</div>
      </div>

      <!-- 3. Chủng Loại Mặt Hàng -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-1">Chủng Loại Gas</div>
        <div class="text-2xl font-bold font-mono text-slate-900">
          {{ inventories.length }} <span class="text-xs font-normal text-slate-500">loại</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Bình 12kg, 45kg & Gas lon mini</div>
      </div>

      <!-- 4. Cảnh Báo Nguy Cấp -->
      <div 
        class="bg-white border p-4 rounded-xl shadow-2xs"
        :class="stats.warningCount > 0 ? 'border-rose-300 bg-rose-50/15' : 'border-slate-200'"
      >
        <div class="text-[11px] font-semibold tracking-wider uppercase mb-1" :class="stats.warningCount > 0 ? 'text-rose-600' : 'text-slate-500'">
          Cảnh Báo Thiếu Hàng
        </div>
        <div class="text-2xl font-bold font-mono" :class="stats.warningCount > 0 ? 'text-rose-600' : 'text-slate-900'">
          {{ stats.warningCount }} <span class="text-xs font-normal text-slate-500">mặt hàng</span>
        </div>
        <div class="text-[10px] mt-1" :class="stats.warningCount > 0 ? 'text-rose-600' : 'text-slate-400'">
          {{ stats.warningCount > 0 ? 'Dưới ngưỡng tồn kho an toàn!' : 'Tất cả đạt định mức an toàn' }}
        </div>
      </div>
    </div>

    <!-- Inventory Table (Corporate Grid) -->
    <div class="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-2xs mb-6">
      <div class="p-4 border-b border-slate-100 flex items-center justify-between">
        <div>
          <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight">Chi Tiết Tồn Kho Từng Loại Bình</h3>
          <p class="text-xs text-slate-500 mt-0.5">Số lượng bình đầy, vỏ rỗng và định mức cảnh báo</p>
        </div>
        <div class="text-xs text-slate-500 font-medium">
          Tổng số loại: <strong class="text-slate-800">{{ inventories.length }}</strong>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
              <th class="py-2.5 px-4">Loại Bình Gas</th>
              <th class="py-2.5 px-4 text-center">Bình Đầy Sẵn Có</th>
              <th class="py-2.5 px-4 text-center">Vỏ Rỗng</th>
              <th class="py-2.5 px-4 text-center">Tổng Vỏ & Bình</th>
              <th class="py-2.5 px-4 text-center">Ngưỡng Báo Động</th>
              <th class="py-2.5 px-4 text-center">Trạng Thái Kho</th>
              <th class="py-2.5 px-4 text-right">Thao Tác Kho</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="item in inventories" :key="item.id" class="hover:bg-slate-50/70 transition-colors">
              <td class="py-3.5 px-4 font-bold text-slate-900">
                {{ item.containerType }}
              </td>

              <td class="py-3.5 px-4 text-center font-mono font-bold text-emerald-700 text-sm">
                {{ item.fullQuantity }} <span class="text-[10px] font-normal text-slate-400">bình</span>
              </td>

              <td class="py-3.5 px-4 text-center font-mono font-bold text-slate-700 text-sm">
                {{ item.emptyQuantity }} <span class="text-[10px] font-normal text-slate-400">vỏ</span>
              </td>

              <td class="py-3.5 px-4 text-center font-mono text-slate-500">
                {{ item.fullQuantity + item.emptyQuantity }}
              </td>

              <td class="py-3.5 px-4 text-center font-mono text-slate-500">
                &le; {{ item.warningThreshold }}
              </td>

              <td class="py-3.5 px-4 text-center">
                <span 
                  v-if="item.fullQuantity <= item.warningThreshold"
                  class="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold bg-rose-50 text-rose-700 border border-rose-200"
                >
                  Cảnh báo: Sắp hết
                </span>
                <span 
                  v-else
                  class="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200"
                >
                  Đủ cung ứng
                </span>
              </td>

              <td class="py-3.5 px-4 text-right whitespace-nowrap">
                <button 
                  @click="openStockModalWithMode('IMPORT', item)"
                  class="px-2 py-1 text-[11px] font-semibold text-emerald-700 hover:bg-emerald-50 border border-emerald-200 rounded transition-colors mr-1.5"
                >
                  + Nhập
                </button>
                <button 
                  @click="openStockModalWithMode('EXPORT', item)"
                  class="px-2 py-1 text-[11px] font-semibold text-rose-700 hover:bg-rose-50 border border-rose-200 rounded transition-colors"
                >
                  - Xuất
                </button>
              </td>
            </tr>

            <tr v-if="inventories.length === 0">
              <td colspan="7" class="py-8 text-center text-slate-400 text-xs">
                Đang tải danh sách kho...
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Stock Modal Dialog (Corporate Clean) -->
    <div 
      v-if="stockModal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs"
    >
      <div class="bg-white rounded-xl shadow-xl border border-slate-200 max-w-md w-full p-6 overflow-hidden">
        <div class="flex items-center justify-between pb-3 border-b border-slate-100 mb-4">
          <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight">
            {{ stockModal.mode === 'IMPORT' ? 'Phiếu Nhập Kho Gas' : 'Phiếu Xuất Kho Gas' }}
          </h3>
          <button @click="closeStockModal" class="text-slate-400 hover:text-slate-600 text-lg leading-none">
            ✕
          </button>
        </div>

        <div v-if="stockModal.error" class="mb-4 p-2.5 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg">
          {{ stockModal.error }}
        </div>

        <form @submit.prevent="submitStockAdjustment" class="space-y-3.5 text-xs">
          <!-- Chọn Mặt Hàng -->
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Loại Bình Gas *</label>
            <select 
              v-model="stockModal.inventoryId" 
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              required
            >
              <option v-for="inv in inventories" :key="inv.id" :value="inv.id">
                {{ inv.containerType }} (Đầy: {{ inv.fullQuantity }} | Vỏ: {{ inv.emptyQuantity }})
              </option>
            </select>
          </div>

          <!-- Hình Thức Nghiệp Vụ -->
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Hình Thức Thao Tác *</label>
            <select 
              v-model="stockModal.operationType" 
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors font-medium"
              required
            >
              <template v-if="stockModal.mode === 'IMPORT'">
                <option value="IMPORT_FULL">Nhập bình gas đầy (Từ nhà máy / trạm chiết về kho)</option>
                <option value="IMPORT_EMPTY">Nhập vỏ rỗng (Thu gom từ khách hàng / đại lý)</option>
              </template>
              <template v-else>
                <option value="EXPORT_FULL">Xuất bình đầy (Giao khách hàng / xuất cho tài xế)</option>
                <option value="EXPORT_EMPTY">Xuất vỏ rỗng (Chuyển xe tải đi nạp lại tại trạm chiết)</option>
              </template>
            </select>
          </div>

          <!-- Số Lượng -->
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Số Lượng (Bình/Vỏ) *</label>
            <input 
              type="number" 
              v-model.number="stockModal.quantity" 
              min="1" 
              required
              placeholder="VD: 10, 20..."
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            />
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-end gap-2 pt-3 border-t border-slate-100">
            <button 
              type="button" 
              @click="closeStockModal"
              class="px-3.5 py-1.5 text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-lg font-semibold transition-colors"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="stockModal.submitting"
              class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 disabled:opacity-50 text-white rounded-lg font-semibold shadow-2xs transition-colors"
            >
              {{ stockModal.submitting ? 'Đang thực hiện...' : 'Xác Nhận' }}
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
import { inventoryService } from '../services/inventoryService.js'

const inventories = ref([])

const stockModal = reactive({
  isOpen: false,
  mode: 'IMPORT', // 'IMPORT' hoặc 'EXPORT'
  inventoryId: '',
  operationType: 'IMPORT_FULL',
  quantity: 1,
  submitting: false,
  error: ''
})

const stats = computed(() => {
  const totalFull = inventories.value.reduce((sum, i) => sum + (Number(i.fullQuantity) || 0), 0)
  const totalEmpty = inventories.value.reduce((sum, i) => sum + (Number(i.emptyQuantity) || 0), 0)
  const warningCount = inventories.value.filter(i => i.fullQuantity <= i.warningThreshold).length
  return { totalFull, totalEmpty, warningCount }
})

async function fetchInventories() {
  try {
    const res = await inventoryService.getInventories()
    inventories.value = res.data || []
  } catch (err) {
    console.error('Lỗi nạp kho gas:', err)
  }
}

function openStockModalWithMode(mode, item) {
  stockModal.error = ''
  stockModal.mode = mode
  stockModal.isOpen = true
  stockModal.quantity = 1
  stockModal.inventoryId = item ? item.id : (inventories.value[0]?.id || '')
  stockModal.operationType = mode === 'IMPORT' ? 'IMPORT_FULL' : 'EXPORT_FULL'
}

function closeStockModal() {
  stockModal.isOpen = false
  stockModal.error = ''
}

async function submitStockAdjustment() {
  stockModal.error = ''
  stockModal.submitting = true
  try {
    await inventoryService.updateStock(stockModal.inventoryId, {
      quantity: stockModal.quantity,
      type: stockModal.operationType
    })
    closeStockModal()
    await fetchInventories()
  } catch (err) {
    stockModal.error = err.message || 'Thao tác kho thất bại!'
  } finally {
    stockModal.submitting = false
  }
}

onMounted(() => {
  fetchInventories()
})
</script>
