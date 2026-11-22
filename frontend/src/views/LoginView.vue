<template>
  <div class="min-h-screen bg-slate-100 flex items-center justify-center p-4 font-sans text-slate-800">
    <div class="w-full max-w-sm">
      <!-- Enterprise Brand Header -->
      <div class="text-center mb-6">
        <div class="inline-flex items-center justify-center w-12 h-12 bg-slate-900 text-white rounded-xl mb-3 shadow-sm font-black text-lg tracking-wider">
          PG
        </div>
        <h1 class="text-xl font-bold tracking-tight text-slate-900">POLYGAS ENTERPRISE</h1>
        <p class="text-xs text-slate-500 mt-1 font-medium">Hệ Thống Quản Trị Phân Phối Gas Toàn Diện</p>
      </div>

      <!-- Authentication Card -->
      <div class="bg-white border border-slate-200 rounded-xl p-6 sm:p-7 shadow-sm">
        <div class="border-b border-slate-100 pb-4 mb-5">
          <h2 class="text-sm font-bold text-slate-900 uppercase tracking-wider">Đăng Nhập Tài Khoản</h2>
          <p class="text-xs text-slate-500 mt-0.5">Truy cập phân hệ quản lý và báo cáo giao vận</p>
        </div>

        <!-- General Error Banner -->
        <div v-if="generalError" class="mb-4 p-3 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-2.5">
          <span class="text-rose-600 font-bold text-xs mt-0.5">⚠️</span>
          <p class="text-xs text-rose-700 leading-normal">{{ generalError }}</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-4">
          <!-- Username Input -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">
              Tên đăng nhập <span class="text-rose-500">*</span>
            </label>
            <input
              v-model="form.username"
              type="text"
              placeholder="VD: admin hoặc shipper01"
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

          <!-- Password Input -->
          <div>
            <div class="flex items-center justify-between mb-1.5">
              <label class="block text-xs font-semibold text-slate-700">
                Mật khẩu <span class="text-rose-500">*</span>
              </label>
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="text-[11px] text-slate-500 hover:text-slate-800 font-medium transition-colors"
              >
                {{ showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu' }}
              </button>
            </div>
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Nhập mật khẩu truy cập..."
              autocomplete="current-password"
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

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full mt-2 py-2.5 px-4 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-400 text-white text-xs font-semibold rounded-lg shadow-xs transition-colors flex items-center justify-center gap-2 cursor-pointer"
          >
            <div v-if="loading" class="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
            <span>{{ loading ? 'Đang xác thực thông tin...' : 'Đăng Nhập Vào Hệ Thống' }}</span>
          </button>
        </form>

        <!-- Register Link -->
        <div class="mt-5 pt-4 border-t border-slate-100 text-center">
          <p class="text-slate-500 text-xs">
            Chưa có tài khoản nội bộ?
            <router-link to="/register" class="text-slate-800 hover:underline font-semibold ml-1">
              Đăng ký tài khoản
            </router-link>
          </p>
        </div>
      </div>

      <!-- Footer Info -->
      <p class="text-center text-slate-400 text-[11px] mt-6 font-mono">
        © 2026 PolyGas Enterprise System. All rights reserved.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient, { saveAuthData } from '../services/api.js'

const router = useRouter()

const form = reactive({ username: '', password: '' })
const errors = ref({})
const generalError = ref('')
const loading = ref(false)
const showPassword = ref(false)

function validateForm() {
  const e = {}
  if (!form.username.trim()) e.username = 'Tên đăng nhập không được để trống'
  if (!form.password.trim()) e.password = 'Mật khẩu không được để trống'
  else if (form.password.length < 6) e.password = 'Mật khẩu phải có ít nhất 6 ký tự'
  errors.value = e
  return Object.keys(e).length === 0
}

async function handleLogin() {
  generalError.value = ''
  if (!validateForm()) return

  loading.value = true
  try {
    const res = await apiClient.post('/auth/login', {
      username: form.username.trim(),
      password: form.password
    })

    saveAuthData(res.data)

    const role = res.data.role
    if (role === 'ADMIN') {
      await router.push('/dashboard')
    } else {
      await router.push('/employment')
    }
  } catch (err) {
    if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
      errors.value = err.fieldErrors
    } else {
      generalError.value = err.message || 'Đăng nhập không thành công. Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.'
    }
  } finally {
    loading.value = false
  }
}
</script>
