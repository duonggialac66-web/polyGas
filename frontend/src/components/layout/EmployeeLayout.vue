<template>
  <div class="min-h-screen bg-slate-50 text-slate-800 flex flex-col font-sans selection:bg-slate-800 selection:text-white">
    <!-- Corporate Top Navigation Bar -->
    <header class="sticky top-0 z-30 bg-white border-b border-slate-200 px-4 sm:px-8 py-3">
      <div class="max-w-7xl mx-auto flex items-center justify-between">
        <!-- Logo & Enterprise Brand -->
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 rounded-lg bg-slate-900 flex items-center justify-center text-white font-black text-sm tracking-wider">
            OG
          </div>
          <div>
            <div class="flex items-center gap-2">
              <span class="text-sm font-bold tracking-tight text-slate-900">PolyGAS</span>
              <span class="px-1.5 py-0.5 rounded text-[10px] font-semibold tracking-wider uppercase bg-slate-100 text-slate-600 border border-slate-200">Enterprise</span>
            </div>
            <p class="text-[11px] text-slate-500 font-medium">Hệ Thống Quản Trị Vận Hành Gas</p>
          </div>
        </div>

        <!-- Center: Quick Navigation for Desktop/Admin -->
        <nav class="hidden md:flex items-center gap-1 text-xs font-semibold text-slate-600">
          <template v-if="isAdmin">
            <router-link to="/dashboard" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Tổng Quan
            </router-link>
            <router-link to="/inventory" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Kho Gas
            </router-link>
            <router-link to="/admin/view_report" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Đối Soát Thu Chi
            </router-link>
            <router-link to="/employment" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Giao Hàng
            </router-link>
            <router-link to="/customers" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Khách Hàng
            </router-link>
            <router-link to="/expenses" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Chi Phí
            </router-link>
          </template>
          <template v-else>
            <router-link to="/employment" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Báo Cáo Giao Gas
            </router-link>
            <router-link to="/customers" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Danh Bạ Khách
            </router-link>
            <router-link to="/debts" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Sổ Nợ Cần Thu
            </router-link>
            <router-link to="/expenses" class="px-3 py-1.5 rounded-md hover:text-slate-900 hover:bg-slate-100 transition-colors" active-class="text-slate-900 bg-slate-100 font-bold">
              Chi Phí Vận Hành
            </router-link>
          </template>
        </nav>

        <!-- Right: Status & User Info -->
        <div class="flex items-center gap-4">
          <!-- Backend Connection Badge -->
          <div 
            class="hidden sm:inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium border"
            :class="isBackendConnected 
              ? 'bg-emerald-50 text-emerald-700 border-emerald-200' 
              : 'bg-amber-50 text-amber-700 border-amber-200'"
          >
            <span class="w-1.5 h-1.5 rounded-full" :class="isBackendConnected ? 'bg-emerald-600' : 'bg-amber-500'"></span>
            <span>{{ isBackendConnected ? 'Máy chủ: Kết nối' : 'Máy chủ: Ngoại tuyến (Demo)' }}</span>
          </div>

          <!-- Refresh Button -->
          <button 
            @click="$emit('refresh')" 
            class="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors border border-slate-200" 
            title="Tải lại dữ liệu"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </button>

          <!-- User Profile & Logout -->
          <div class="flex items-center gap-2.5 pl-3 border-l border-slate-200">
            <div class="w-7 h-7 rounded-md bg-slate-900 text-white flex items-center justify-center font-bold text-xs">
              {{ userInitial }}
            </div>
            <div class="hidden sm:block text-left">
              <div class="text-xs font-semibold text-slate-800 leading-tight">{{ userName }}</div>
              <div class="text-[10px] text-slate-500 font-mono">{{ userRole }}</div>
            </div>
            <button 
              @click="handleLogout"
              class="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors ml-1"
              title="Đăng xuất khỏi hệ thống"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Workspace -->
    <main class="flex-1 w-full mx-auto px-4 sm:px-8 py-6 pb-24" :class="wide ? 'max-w-7xl' : 'max-w-6xl'">
      <slot />
    </main>

    <!-- Professional Bottom Navigation for Mobile / Quick Access -->
    <BottomNav :debt-count="debtCount" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import BottomNav from './BottomNav.vue'
import { clearAuthStorage } from '../../services/api.js'

defineProps({
  isBackendConnected: {
    type: Boolean,
    default: true
  },
  debtCount: {
    type: Number,
    default: 0
  },
  wide: {
    type: Boolean,
    default: false
  }
})

defineEmits(['refresh'])

const router = useRouter()

const userName = computed(() => localStorage.getItem('name') || 'Nhân viên')
const userRole = computed(() => localStorage.getItem('role') || 'EMPLOYEE')
const isAdmin = computed(() => userRole.value === 'ADMIN' || userRole.value === 'ROLE_ADMIN')

const userInitial = computed(() => {
  const name = userName.value.trim()
  if (!name) return 'NV'
  const parts = name.split(' ')
  return parts.length > 1 ? parts[parts.length - 1].substring(0, 2).toUpperCase() : name.substring(0, 2).toUpperCase()
})

function handleLogout() {
  clearAuthStorage()
  router.push('/login')
}
</script>
