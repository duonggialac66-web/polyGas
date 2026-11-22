<template>
  <nav class="fixed bottom-0 inset-x-0 bg-white border-t border-slate-200 z-40 py-1.5 px-4 shadow-sm md:hidden">
    <!-- MENU DÀNH CHO ADMIN -->
    <div v-if="isAdmin" class="max-w-md mx-auto grid grid-cols-5 gap-1 items-center">
      <!-- Admin Tab 1: Tổng Quan -->
      <router-link
        to="/dashboard"
        class="flex flex-col items-center py-1.5 px-1 rounded-lg transition-colors duration-150 group"
        :class="isActive('/dashboard') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
        <span class="text-[10px] tracking-tight">Tổng Quan</span>
      </router-link>

      <!-- Admin Tab 2: Kho Gas -->
      <router-link
        to="/inventory"
        class="flex flex-col items-center py-1.5 px-1 rounded-lg transition-colors duration-150 group"
        :class="isActive('/inventory') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
        </svg>
        <span class="text-[10px] tracking-tight">Kho Gas</span>
      </router-link>

      <!-- Admin Tab 3: Đối Soát Báo Cáo -->
      <router-link
        to="/admin/view_report"
        class="flex flex-col items-center py-1.5 px-1 rounded-lg transition-colors duration-150 group"
        :class="isActive('/admin/view_report') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <span class="text-[10px] tracking-tight">Đối Soát</span>
      </router-link>

      <!-- Admin Tab 4: Giao Hàng Ca Trực -->
      <router-link
        to="/employment"
        class="flex flex-col items-center py-1.5 px-1 rounded-lg transition-colors duration-150 group"
        :class="isActive('/employment') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
        </svg>
        <span class="text-[10px] tracking-tight">Giao Gas</span>
      </router-link>

      <!-- Admin Tab 5: Nhân Sự & Phân Quyền -->
      <router-link
        to="/users"
        class="flex flex-col items-center py-1.5 px-1 rounded-lg transition-colors duration-150 group"
        :class="isActive('/users') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
        <span class="text-[10px] tracking-tight">Nhân Sự</span>
      </router-link>
    </div>

    <!-- MENU DÀNH CHO NHÂN VIÊN (SHIPPER/EMPLOYEE) THEO ĐÚNG SRS -->
    <div v-else class="max-w-md mx-auto grid grid-cols-4 gap-1 items-center">
      <!-- Employee Tab 1: Giao Gas -->
      <router-link
        to="/employment"
        class="flex flex-col items-center py-1.5 px-2 rounded-lg transition-colors duration-150 group"
        :class="isActive('/employment') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
        </svg>
        <span class="text-[11px] tracking-tight">Giao Gas</span>
      </router-link>

      <!-- Employee Tab 2: Danh Bạ Khách Hàng -->
      <router-link
        to="/customers"
        class="flex flex-col items-center py-1.5 px-2 rounded-lg transition-colors duration-150 group"
        :class="isActive('/customers') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
        <span class="text-[11px] tracking-tight">Khách Hàng</span>
      </router-link>

      <!-- Employee Tab 3: Sổ Nợ Khách Hàng -->
      <router-link
        to="/debts"
        class="flex flex-col items-center py-1.5 px-2 rounded-lg transition-colors duration-150 group relative"
        :class="isActive('/debts') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <div class="relative">
          <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span v-if="debtCount > 0" class="absolute -top-1 -right-2 px-1.5 py-0.2 bg-rose-600 text-white text-[9px] font-bold rounded-full leading-tight">
            {{ debtCount }}
          </span>
        </div>
        <span class="text-[11px] tracking-tight">Sổ Nợ</span>
      </router-link>

      <!-- Employee Tab 4: Chi Phí Vận Hành -->
      <router-link
        to="/expenses"
        class="flex flex-col items-center py-1.5 px-2 rounded-lg transition-colors duration-150 group"
        :class="isActive('/expenses') 
          ? 'text-slate-900 font-bold bg-slate-100' 
          : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5 mb-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <span class="text-[11px] tracking-tight">Chi Phí</span>
      </router-link>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

defineProps({
  debtCount: {
    type: Number,
    default: 0
  }
})

const route = useRoute()
const userRole = computed(() => localStorage.getItem('role') || 'EMPLOYEE')
const isAdmin = computed(() => userRole.value === 'ADMIN' || userRole.value === 'ROLE_ADMIN')

function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>
