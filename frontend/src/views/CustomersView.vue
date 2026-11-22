<template>
  <EmployeeLayout>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <h2 class="text-lg font-bold text-slate-900 tracking-tight">Danh Bạ Khách Hàng & Vị Trí GPS</h2>
        <p class="text-xs text-slate-500 mt-0.5">Tìm kiếm khách hàng, lưu tọa độ GPS để hỗ trợ nhân viên giao vận</p>
      </div>

      <button 
        @click="openCreateModal"
        class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors self-start sm:self-auto"
      >
        <span>➕ Thêm Khách Hàng Mới</span>
      </button>
    </div>

    <!-- Search Input Bar -->
    <div class="bg-white p-2.5 rounded-xl border border-slate-200 mb-4 shadow-2xs">
      <input
        v-model="searchKeyword"
        @input="onSearchInput"
        type="text"
        placeholder="Tìm kiếm theo tên khách hàng, số điện thoại hoặc địa chỉ..."
        class="w-full px-3 py-1.5 text-xs text-slate-900 bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:border-slate-800 transition-colors placeholder:text-slate-400 font-medium"
      />
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-16 bg-white rounded-xl border border-slate-200">
      <div class="w-6 h-6 border-2 border-slate-300 border-t-slate-800 rounded-full animate-spin mx-auto mb-2"></div>
      <p class="text-xs text-slate-500">Đang tìm danh bạ...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="customers.length === 0" class="text-center py-12 bg-white rounded-xl border border-dashed border-slate-300">
      <p class="text-sm font-semibold text-slate-700">Không tìm thấy khách hàng nào</p>
      <p class="text-xs text-slate-500 mt-1">Vui lòng thử tìm từ khóa khác hoặc bấm nút thêm khách hàng mới.</p>
    </div>

    <!-- Customers Table / List -->
    <div v-else class="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-2xs divide-y divide-slate-100">
      <div
        v-for="c in customers"
        :key="c.id"
        class="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-slate-50 transition-colors"
      >
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 mb-1">
            <h4 class="text-sm font-bold text-slate-900">{{ c.name }}</h4>
            <span v-if="c.phone" class="text-[11px] font-mono text-slate-600 bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
              {{ c.phone }}
            </span>
          </div>
          <p class="text-xs text-slate-600 truncate mb-1">
            Địa chỉ: {{ c.address || 'Chưa cập nhật địa chỉ' }}
          </p>
          <div v-if="c.notes" class="text-[11px] text-slate-500 italic truncate">
            "{{ c.notes }}"
          </div>
        </div>

        <div class="flex items-center justify-between sm:justify-end gap-2 pt-2.5 sm:pt-0 border-t border-slate-100 sm:border-0">
          <a
            v-if="c.latitude && c.longitude"
            :href="`https://www.google.com/maps/search/?api=1&query=${c.latitude},${c.longitude}`"
            target="_blank"
            class="px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 rounded text-xs font-semibold transition-colors"
          >
            Dẫn đường GPS
          </a>

          <button
            @click="openEditModal(c)"
            class="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-200 rounded transition-colors"
            title="Sửa thông tin"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Form -->
    <div
      v-if="modal.isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs transition-opacity"
      @click.self="closeModal"
    >
      <div class="bg-white w-full max-w-md rounded-xl shadow-lg border border-slate-200 overflow-hidden">
        <div class="px-5 py-3.5 border-b border-slate-200 flex items-center justify-between bg-slate-50">
          <h3 class="text-sm font-bold text-slate-900">
            {{ modal.isEdit ? 'Cập Nhật Hồ Sơ Khách Hàng' : 'Thêm Mới Khách Hàng' }}
          </h3>
          <button @click="closeModal" class="p-1 text-slate-400 hover:text-slate-600">✕</button>
        </div>

        <form @submit.prevent="submitSaveCustomer" class="p-5 space-y-4">
          <div v-if="errors.general" class="p-3 bg-rose-50 border border-rose-200 rounded-lg text-xs text-rose-700">
            {{ errors.general }}
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">
              Tên Khách Hàng <span class="text-rose-500">*</span>
            </label>
            <input
              type="text"
              v-model="customerForm.name"
              @input="delete errors.name"
              placeholder="VD: Quán Ăn Cô Ba, Bún Bò Huế..."
              :class="[
                'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                errors.name ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
              ]"
            />
            <p v-if="errors.name" class="text-xs text-rose-600 mt-1 font-medium">
              {{ errors.name }}
            </p>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Số Điện Thoại</label>
            <input
              type="tel"
              v-model="customerForm.phone"
              @input="delete errors.phone"
              placeholder="VD: 0901234567"
              :class="[
                'w-full px-3 py-2 text-xs text-slate-900 bg-white border rounded-lg focus:outline-none transition-colors font-medium',
                errors.phone ? 'border-rose-500 bg-rose-50/20' : 'border-slate-300 focus:border-slate-800'
              ]"
            />
            <p v-if="errors.phone" class="text-xs text-rose-600 mt-1 font-medium">
              {{ errors.phone }}
            </p>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Địa Chỉ</label>
            <input
              type="text"
              v-model="customerForm.address"
              placeholder="VD: 123 Nguyễn Văn Cừ, Quận 5..."
              class="w-full px-3 py-2 text-xs text-slate-900 bg-white border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800 font-medium"
            />
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1">Vĩ Độ (Latitude)</label>
              <input
                type="number"
                step="any"
                v-model.number="customerForm.latitude"
                class="w-full px-3 py-1.5 text-xs text-slate-900 bg-white border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800 font-mono"
              />
            </div>
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1">Kinh Độ (Longitude)</label>
              <input
                type="number"
                step="any"
                v-model.number="customerForm.longitude"
                class="w-full px-3 py-1.5 text-xs text-slate-900 bg-white border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800 font-mono"
              />
            </div>
          </div>

          <button
            type="button"
            @click="getCurrentLocation"
            class="w-full py-1.5 px-3 bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 text-xs font-semibold rounded-lg transition-colors"
          >
            📍 Lấy tọa độ GPS thiết bị hiện tại
          </button>

          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Ghi Chú</label>
            <textarea
              v-model="customerForm.notes"
              rows="2"
              class="w-full px-3 py-1.5 text-xs text-slate-900 bg-white border border-slate-300 rounded-lg focus:outline-none focus:border-slate-800 font-medium"
            ></textarea>
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
              {{ modal.isEdit ? 'Lưu Thay Đổi' : 'Thêm Khách Hàng' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import customerService from '../services/customerService.js'

const customers = ref([])
const loading = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const errors = ref({})

const modal = reactive({
  isOpen: false,
  isEdit: false,
  editingId: null
})

const customerForm = reactive({
  name: '',
  phone: '',
  address: '',
  latitude: null,
  longitude: null,
  notes: ''
})

let debounceTimer = null
function onSearchInput() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    fetchCustomers()
  }, 300)
}

async function fetchCustomers() {
  loading.value = true
  try {
    const data = await customerService.getCustomers(searchKeyword.value)
    customers.value = data || []
  } catch (err) {
    console.warn('Lỗi danh bạ khách:', err)
  } finally {
    loading.value = false
  }
}

function validateCustomerForm() {
  const errs = {}
  if (!customerForm.name || customerForm.name.trim().length < 2) {
    errs.name = 'Tên khách hàng phải có ít nhất 2 ký tự'
  }
  const phoneRegex = /^(0[35789][0-9]{8})$/
  if (customerForm.phone && !phoneRegex.test(customerForm.phone.trim())) {
    errs.phone = 'Số điện thoại không hợp lệ (VD: 0901234567)'
  }
  errors.value = errs
  return Object.keys(errs).length === 0
}

function openCreateModal() {
  modal.isOpen = true
  modal.isEdit = false
  modal.editingId = null
  errors.value = {}
  customerForm.name = ''
  customerForm.phone = ''
  customerForm.address = ''
  customerForm.latitude = null
  customerForm.longitude = null
  customerForm.notes = ''
}

function openEditModal(c) {
  modal.isOpen = true
  modal.isEdit = true
  modal.editingId = c.id
  errors.value = {}
  customerForm.name = c.name
  customerForm.phone = c.phone || ''
  customerForm.address = c.address || ''
  customerForm.latitude = c.latitude
  customerForm.longitude = c.longitude
  customerForm.notes = c.notes || ''
}

function closeModal() {
  modal.isOpen = false
  errors.value = {}
}

function getCurrentLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      pos => {
        customerForm.latitude = Number(pos.coords.latitude.toFixed(6))
        customerForm.longitude = Number(pos.coords.longitude.toFixed(6))
      },
      err => {
        alert('Không lấy được GPS: ' + err.message)
      }
    )
  } else {
    alert('Trình duyệt không hỗ trợ GPS')
  }
}

async function submitSaveCustomer() {
  if (!validateCustomerForm()) return

  submitting.value = true
  errors.value = {}
  try {
    const payload = {
      name: customerForm.name.trim(),
      phone: customerForm.phone ? customerForm.phone.trim() : null,
      address: customerForm.address ? customerForm.address.trim() : null,
      latitude: customerForm.latitude,
      longitude: customerForm.longitude,
      notes: customerForm.notes ? customerForm.notes.trim() : null
    }

    if (modal.isEdit) {
      await customerService.updateCustomer(modal.editingId, payload)
    } else {
      await customerService.createCustomer(payload)
    }

    closeModal()
    await fetchCustomers()
  } catch (err) {
    if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
      errors.value = err.fieldErrors
    } else {
      errors.value = { general: err.message || 'Lưu thông tin thất bại' }
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCustomers()
})
</script>
