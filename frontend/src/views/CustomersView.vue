<template>
  <EmployeeLayout>
    <!-- Header -->
    <div class="flex items-center justify-between mb-4">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-sky-500 animate-pulse"></span>
          <h2 class="text-xl font-extrabold tracking-tight text-slate-900">Danh Bạ Khách Hàng (GPS)</h2>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Tìm kiếm siêu tốc không dấu, ghim tọa độ bản đồ dẫn đường cho shipper</p>
      </div>

      <button 
        @click="openCreateModal"
        class="inline-flex items-center gap-1.5 px-3.5 py-2 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white text-xs font-bold rounded-xl shadow-sm transition-all"
      >
        <span>➕ Thêm Khách Mới</span>
      </button>
    </div>

    <!-- Search Input Bar -->
    <div class="bg-white p-3.5 rounded-2xl border border-slate-200/80 shadow-xs mb-4">
      <div class="relative">
        <span class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </span>
        <input
          v-model="searchKeyword"
          @input="onSearchInput"
          type="text"
          placeholder="Tìm tên khách (gõ không dấu), SĐT hoặc địa chỉ..."
          class="w-full pl-10 pr-10 py-2.5 bg-slate-50 hover:bg-slate-100/80 focus:bg-white text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-sky-500/30 focus:border-sky-500 transition-all placeholder:text-slate-400"
        />
        <button
          v-if="searchKeyword"
          @click="clearSearch"
          class="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600"
        >
          ✕
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-10 bg-white rounded-2xl border border-slate-200/80">
      <div class="inline-block animate-spin w-6 h-6 border-2 border-sky-600 border-t-transparent rounded-full mb-2"></div>
      <p class="text-xs text-slate-500">Đang tải danh bạ khách hàng...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="customers.length === 0" class="text-center py-12 bg-white rounded-2xl border border-slate-200/80">
      <div class="text-3xl mb-2">📍</div>
      <p class="text-sm font-bold text-slate-700">Chưa tìm thấy khách hàng nào</p>
      <p class="text-xs text-slate-400 mt-1">Hãy thử tìm với từ khóa khác hoặc bấm "Thêm Khách Mới" ở trên.</p>
    </div>

    <!-- Customers List Cards -->
    <div v-else class="space-y-3 pb-20">
      <div 
        v-for="c in customers" 
        :key="c.id" 
        class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs hover:border-sky-300 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3"
      >
        <div class="space-y-1">
          <div class="flex items-center gap-2">
            <span class="text-sm font-bold text-slate-900">{{ c.name }}</span>
            <span v-if="c.nameHash" class="text-[10px] px-1.5 py-0.5 bg-slate-100 text-slate-600 rounded font-mono">
              #{{ c.nameHash }}
            </span>
          </div>
          <div class="text-xs text-slate-600 flex items-center gap-2">
            <span>📞 {{ c.phone || 'Chưa có SĐT' }}</span>
            <span class="text-slate-300">•</span>
            <span>🏠 {{ c.address || 'Chưa cập nhật địa chỉ' }}</span>
          </div>
          <div v-if="c.notes" class="text-[11px] text-slate-500 italic">
            📝 Ghi chú: {{ c.notes }}
          </div>
          <div v-if="c.latitude && c.longitude" class="text-[11px] text-emerald-600 font-semibold flex items-center gap-1">
            <span>🌐 Tọa độ GPS: {{ c.latitude.toFixed(6) }}, {{ c.longitude.toFixed(6) }}</span>
          </div>
        </div>

        <div class="flex items-center gap-2 self-end sm:self-center">
          <a
            v-if="c.latitude && c.longitude"
            :href="`https://www.google.com/maps/dir/?api=1&destination=${c.latitude},${c.longitude}`"
            target="_blank"
            class="px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 border border-emerald-200"
          >
            <span>🗺️ Chỉ Đường</span>
          </a>
          <a
            v-else-if="c.address"
            :href="`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(c.address)}`"
            target="_blank"
            class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition-all flex items-center gap-1"
          >
            <span>📍 Tìm Bản Đồ</span>
          </a>
          <a
            v-if="c.phone"
            :href="`tel:${c.phone}`"
            class="px-3 py-1.5 bg-sky-50 hover:bg-sky-100 text-sky-700 text-xs font-bold rounded-xl transition-all flex items-center gap-1 border border-sky-200"
          >
            <span>📞 Gọi</span>
          </a>
        </div>
      </div>
    </div>

    <!-- Modal Thêm Khách Hàng Mới & Ghim GPS -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white w-full max-w-md rounded-2xl border border-slate-200 shadow-xl overflow-hidden animate-in fade-in zoom-in duration-150">
        <div class="p-4 border-b border-slate-100 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-base">📍</span>
            <h3 class="text-sm font-bold text-slate-900">Thêm Khách Hàng & Ghim GPS</h3>
          </div>
          <button @click="showModal = false" class="text-slate-400 hover:text-slate-600 text-sm">✕</button>
        </div>

        <form @submit.prevent="submitCreateCustomer" class="p-4 space-y-3">
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Tên khách hàng / Quán ăn *</label>
            <input 
              v-model="form.name" 
              type="text" 
              required
              placeholder="VD: Quán Cơm Bình Dân Cô Ba"
              class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-sky-500/30 focus:border-sky-500 focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Số điện thoại</label>
            <input 
              v-model="form.phone" 
              type="text" 
              placeholder="VD: 0908 123 456"
              class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-sky-500/30 focus:border-sky-500 focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Địa chỉ chi tiết</label>
            <input 
              v-model="form.address" 
              type="text" 
              placeholder="VD: 128 Nguyễn Trãi, P.3, Q.5, TP.HCM"
              class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-sky-500/30 focus:border-sky-500 focus:outline-none"
            />
          </div>

          <!-- GPS Section -->
          <div class="p-3 bg-sky-50/60 rounded-xl border border-sky-100 space-y-2">
            <div class="flex items-center justify-between">
              <span class="text-xs font-bold text-sky-900">Tọa độ định vị GPS</span>
              <button
                type="button"
                @click="getCurrentLocation"
                :disabled="gettingGps"
                class="px-2.5 py-1 bg-sky-600 hover:bg-sky-700 text-white text-[11px] font-bold rounded-lg transition-all active:scale-95 disabled:opacity-50"
              >
                {{ gettingGps ? 'Đang lấy...' : '🎯 Lấy Tọa Độ Hiện Tại' }}
              </button>
            </div>
            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="block text-[10px] text-slate-500">Vĩ độ (Latitude)</label>
                <input 
                  v-model.number="form.latitude" 
                  type="number" 
                  step="any"
                  placeholder="10.762622"
                  class="w-full px-2 py-1.5 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label class="block text-[10px] text-slate-500">Kinh độ (Longitude)</label>
                <input 
                  v-model.number="form.longitude" 
                  type="number" 
                  step="any"
                  placeholder="106.660172"
                  class="w-full px-2 py-1.5 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
            </div>
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1">Ghi chú giao hàng</label>
            <textarea 
              v-model="form.notes" 
              rows="2"
              placeholder="VD: Giao vào hẻm bên trái, gọi trước 10 phút..."
              class="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl focus:ring-2 focus:ring-sky-500/30 focus:border-sky-500 focus:outline-none"
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
              class="px-4 py-2 bg-sky-600 hover:bg-sky-700 text-white text-xs font-bold rounded-xl shadow-sm transition-all active:scale-95 disabled:opacity-50"
            >
              {{ submitting ? 'Đang lưu...' : 'Lưu Khách Hàng' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import customerService from '../services/customerService'

const customers = ref([])
const loading = ref(false)
const searchKeyword = ref('')
let searchTimeout = null

const showModal = ref(false)
const submitting = ref(false)
const gettingGps = ref(false)

const form = ref({
  name: '',
  phone: '',
  address: '',
  latitude: null,
  longitude: null,
  notes: ''
})

const loadCustomers = async () => {
  loading.value = true
  try {
    const res = await customerService.getAll()
    customers.value = res.data || []
  } catch (err) {
    console.error('Lỗi tải khách hàng:', err)
  } finally {
    loading.value = false
  }
}

const onSearchInput = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(async () => {
    if (!searchKeyword.value.trim()) {
      return loadCustomers()
    }
    loading.value = true
    try {
      const res = await customerService.search(searchKeyword.value.trim())
      customers.value = res.data || []
    } catch (err) {
      console.error('Lỗi tìm kiếm khách hàng:', err)
    } finally {
      loading.value = false
    }
  }, 300)
}

const clearSearch = () => {
  searchKeyword.value = ''
  loadCustomers()
}

const openCreateModal = () => {
  form.value = {
    name: '',
    phone: '',
    address: '',
    latitude: null,
    longitude: null,
    notes: ''
  }
  showModal.value = true
}

const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    alert('Trình duyệt không hỗ trợ Geolocation GPS')
    return
  }
  gettingGps.value = true
  navigator.geolocation.getCurrentPosition(
    (position) => {
      form.value.latitude = position.coords.latitude
      form.value.longitude = position.coords.longitude
      gettingGps.value = false
    },
    (error) => {
      console.warn('Không lấy được GPS:', error.message)
      alert('Không thể lấy tọa độ hiện tại. Hãy cấp quyền truy cập vị trí trên trình duyệt.')
      gettingGps.value = false
    },
    { enableHighAccuracy: true, timeout: 10000 }
  )
}

const submitCreateCustomer = async () => {
  submitting.value = true
  try {
    const res = await customerService.create(form.value)
    alert(res.message || 'Thêm khách hàng thành công!')
    showModal.value = false
    loadCustomers()
  } catch (err) {
    alert(err.message || 'Lỗi khi tạo khách hàng')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCustomers()
})
</script>
