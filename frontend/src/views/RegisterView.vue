<template>
  <div class="min-h-screen bg-slate-100 flex items-center justify-center p-4 font-sans text-slate-800">
    <div class="w-full max-w-md my-8">
      <!-- Enterprise Brand Header -->
      <div class="text-center mb-6">
        <div class="inline-flex items-center justify-center w-12 h-12 bg-slate-900 text-white rounded-xl mb-3 shadow-sm font-black text-lg tracking-wider">
          OG
        </div>
        <h1 class="text-xl font-bold tracking-tight text-slate-900">ORIGINGAS ENTERPRISE</h1>
        <p class="text-xs text-slate-500 mt-1 font-medium">Đăng Ký Tài Khoản Vận Hành Doanh Nghiệp</p>
      </div>

      <!-- Registration Card -->
      <div class="bg-white border border-slate-200 rounded-xl p-6 sm:p-7 shadow-sm">
        <div class="border-b border-slate-100 pb-4 mb-5">
          <h2 class="text-sm font-bold text-slate-900 uppercase tracking-wider">Tạo Hồ Sơ Người Dùng</h2>
          <p class="text-xs text-slate-500 mt-0.5">Khai báo thông tin tài khoản nhân viên hoặc quản trị</p>
        </div>

        <!-- Success Message -->
        <div v-if="successMsg" class="mb-4 p-3 bg-emerald-50 border border-emerald-200 rounded-lg flex items-start gap-2.5">
          <span class="text-emerald-700 font-bold text-xs mt-0.5">✓</span>
          <p class="text-xs text-emerald-800 leading-normal">{{ successMsg }}</p>
        </div>

        <!-- General Error Banner -->
        <div v-if="generalError" class="mb-4 p-3 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-2.5">
          <span class="text-rose-600 font-bold text-xs mt-0.5">⚠️</span>
          <p class="text-xs text-rose-700 leading-normal">{{ generalError }}</p>
        </div>

        <form @submit.prevent="handleRegister" class="space-y-3.5">
          <!-- Full Name -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">
              Họ và tên nhân viên <span class="text-rose-500">*</span>
            </label>
            <input
              v-model="form.name"
              type="text"
              placeholder="VD: Nguyễn Văn Giao"
              :class="[
                'w-full px-3.5 py-2 text-xs text-slate-900 bg-white border rounded-lg outline-none transition-colors font-medium',
                errors.name
                  ? 'border-rose-500 focus:border-rose-600 bg-rose-50/20'
                  : 'border-slate-300 focus:border-slate-800'
              ]"
              @input="delete errors.name; generalError = ''"
            />
            <p v-if="errors.name" class="mt-1 text-xs text-rose-600 flex items-center gap-1 font-medium">
              <span>✕</span> {{ errors.name }}
            </p>
          </div>

          <!-- Username & Phone in 2 cols -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">
                Tên đăng nhập <span class="text-rose-500">*</span>
              </label>
              <input
                v-model="form.username"
                type="text"
                placeholder="VD: shipper02"
                autocomplete="username"
                :class="[
                  'w-full px-3.5 py-2 text-xs text-slate-900 bg-white border rounded-lg outline-none transition-colors font-medium',
                  errors.username
                    ? 'border-rose-500 focus:border-rose-600 bg-rose-50/20'
                    : 'border-slate-300 focus:border-slate-800'
                ]"
                @input="delete errors.username; generalError = ''"
              />
              <p v-if="errors.username" class="mt-1 text-xs text-rose-600 flex items-center gap-1 font-medium">
                <span>✕</span> {{ errors.username }}
              </p>
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">
                Số điện thoại liên hệ
              </label>
              <input
                v-model="form.phone"
                type="tel"
                placeholder="VD: 0901234567"
                :class="[
                  'w-full px-3.5 py-2 text-xs text-slate-900 bg-white border rounded-lg outline-none transition-colors font-medium',
                  errors.phone
                    ? 'border-rose-500 focus:border-rose-600 bg-rose-50/20'
                    : 'border-slate-300 focus:border-slate-800'
                ]"
                @input="delete errors.phone; generalError = ''"
              />
              <p v-if="errors.phone" class="mt-1 text-xs text-rose-600 flex items-center gap-1 font-medium">
                <span>✕</span> {{ errors.phone }}
              </p>
            </div>
          </div>

          <!-- Password & Confirm Password in 2 cols -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">
                Mật khẩu <span class="text-rose-500">*</span>
              </label>
              <input
                v-model="form.password"
                type="password"
                placeholder="Ít nhất 6 ký tự..."
                autocomplete="new-password"
                :class="[
                  'w-full px-3.5 py-2 text-xs text-slate-900 bg-white border rounded-lg outline-none transition-colors font-medium',
                  errors.password
                    ? 'border-rose-500 focus:border-rose-600 bg-rose-50/20'
                    : 'border-slate-300 focus:border-slate-800'
                ]"
                @input="delete errors.password; generalError = ''"
              />
              <p v-if="errors.password" class="mt-1 text-xs text-rose-600 flex items-center gap-1 font-medium">
                <span>✕</span> {{ errors.password }}
              </p>
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">
                Xác nhận lại mật khẩu <span class="text-rose-500">*</span>
              </label>
              <input
                v-model="form.confirmPassword"
                type="password"
                placeholder="Nhập lại mật khẩu..."
                autocomplete="new-password"
                :class="[
                  'w-full px-3.5 py-2 text-xs text-slate-900 bg-white border rounded-lg outline-none transition-colors font-medium',
                  errors.confirmPassword
                    ? 'border-rose-500 focus:border-rose-600 bg-rose-50/20'
                    : 'border-slate-300 focus:border-slate-800'
                ]"
                @input="delete errors.confirmPassword; generalError = ''"
              />
              <p v-if="errors.confirmPassword" class="mt-1 text-xs text-rose-600 flex items-center gap-1 font-medium">
                <span>✕</span> {{ errors.confirmPassword }}
              </p>
            </div>
          </div>

          <!-- Role Selection -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">Phân quyền chức vụ</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                type="button"
                @click="form.role = 'EMPLOYEE'"
                class="py-2 px-3 rounded-lg border text-xs font-semibold flex items-center justify-center gap-1.5 transition-colors"
                :class="form.role === 'EMPLOYEE'
                  ? 'border-slate-900 bg-slate-900 text-white'
                  : 'border-slate-300 bg-white text-slate-700 hover:bg-slate-50'"
              >
                <span>Nhân Viên Giao Gas</span>
              </button>
              <button
                type="button"
                @click="form.role = 'ADMIN'"
                class="py-2 px-3 rounded-lg border text-xs font-semibold flex items-center justify-center gap-1.5 transition-colors"
                :class="form.role === 'ADMIN'
                  ? 'border-slate-900 bg-slate-900 text-white'
                  : 'border-slate-300 bg-white text-slate-700 hover:bg-slate-50'"
              >
                <span>Quản Trị Viên</span>
              </button>
            </div>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full mt-3 py-2.5 px-4 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-400 text-white text-xs font-semibold rounded-lg shadow-xs transition-colors flex items-center justify-center gap-2 cursor-pointer"
          >
            <div v-if="loading" class="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
            <span>{{ loading ? 'Đang tạo hồ sơ...' : 'Đăng Ký Tài Khoản' }}</span>
          </button>
        </form>

        <!-- Back to Login -->
        <div class="mt-5 pt-4 border-t border-slate-100 text-center">
          <p class="text-slate-500 text-xs">
            Đã có tài khoản nội bộ?
            <router-link to="/login" class="text-slate-800 hover:underline font-semibold ml-1">
              Quay lại đăng nhập
            </router-link>
          </p>
        </div>
      </div>

      <!-- Footer Info -->
      <p class="text-center text-slate-400 text-[11px] mt-6 font-mono">
        © 2026 OriginGas Enterprise System. All rights reserved.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '../services/api.js'

const router = useRouter()

const form = reactive({
  name: '',
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  role: 'EMPLOYEE'
})

const errors = ref({})
const generalError = ref('')
const successMsg = ref('')
const loading = ref(false)

const phoneRegex = /^(0[35789][0-9]{8})$/

function validateForm() {
  const e = {}
  if (!form.name.trim() || form.name.trim().length < 2) {
    e.name = 'Họ tên phải có ít nhất 2 ký tự'
  }
  if (!form.username.trim() || form.username.trim().length < 3) {
    e.username = 'Tên đăng nhập phải có ít nhất 3 ký tự'
  } else if (/\s/.test(form.username)) {
    e.username = 'Tên đăng nhập không được chứa khoảng trắng'
  }
  if (form.phone && !phoneRegex.test(form.phone.trim())) {
    e.phone = 'Số điện thoại không hợp lệ (VD: 0901234567)'
  }
  if (!form.password || form.password.length < 6) {
    e.password = 'Mật khẩu phải có ít nhất 6 ký tự'
  }
  if (form.password !== form.confirmPassword) {
    e.confirmPassword = 'Mật khẩu xác nhận không khớp'
  }
  errors.value = e
  return Object.keys(e).length === 0
}

async function handleRegister() {
  generalError.value = ''
  successMsg.value = ''
  if (!validateForm()) return

  loading.value = true
  try {
    await apiClient.post('/auth/register', {
      name: form.name.trim(),
      username: form.username.trim(),
      phone: form.phone ? form.phone.trim() : null,
      password: form.password,
      role: form.role
    })

    successMsg.value = 'Tài khoản được tạo thành công! Đang chuyển hướng đến trang đăng nhập...'
    setTimeout(() => router.push('/login'), 1200)
  } catch (err) {
    if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
      errors.value = err.fieldErrors
    } else {
      generalError.value = err.message || 'Đăng ký tài khoản thất bại. Vui lòng kiểm tra lại thông tin.'
    }
  } finally {
    loading.value = false
  }
}
</script>
