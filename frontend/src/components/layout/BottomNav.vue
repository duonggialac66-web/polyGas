<template>
  <nav class="fixed bottom-0 inset-x-0 bg-white/95 backdrop-blur-md border-t border-slate-200/80 z-40 py-1.5 px-3 shadow-[0_-4px_20px_rgba(0,0,0,0.04)]">
    <div class="max-w-md mx-auto grid grid-cols-4 items-center">
      <!-- Tab 1: Báo Cáo Giao Gas -->
      <router-link
        to="/employment"
        class="flex flex-col items-center py-1 px-2 rounded-xl transition-all duration-150 group"
        :class="isActive('/employment') ? 'text-sky-600 font-semibold' : 'text-slate-500 hover:text-slate-700'"
      >
        <div class="relative">
          <svg class="w-5 h-5 transition-transform group-active:scale-90" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
          </svg>
          <span v-if="isActive('/employment')" class="absolute -bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 bg-sky-600 rounded-full"></span>
        </div>
        <span class="text-[10px] mt-1 tracking-tight">Giao Gas</span>
      </router-link>

      <!-- Tab 2: Danh Bạ Khách Hàng (GPS) -->
      <router-link
        to="/customers"
        class="flex flex-col items-center py-1 px-2 rounded-xl transition-all duration-150 group"
        :class="isActive('/customers') ? 'text-sky-600 font-semibold' : 'text-slate-500 hover:text-slate-700'"
      >
        <div class="relative">
          <svg class="w-5 h-5 transition-transform group-active:scale-90" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <span v-if="isActive('/customers')" class="absolute -bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 bg-sky-600 rounded-full"></span>
        </div>
        <span class="text-[10px] mt-1 tracking-tight">Khách Hàng</span>
      </router-link>

      <!-- Tab 3: Sổ Nợ -->
      <router-link
        to="/debts"
        class="flex flex-col items-center py-1 px-2 rounded-xl transition-all duration-150 group"
        :class="isActive('/debts') ? 'text-sky-600 font-semibold' : 'text-slate-500 hover:text-slate-700'"
      >
        <div class="relative">
          <svg class="w-5 h-5 transition-transform group-active:scale-90" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span v-if="debtCount > 0" class="absolute -top-1 -right-2 px-1.5 py-0.2 bg-rose-500 text-white text-[9px] font-bold rounded-full leading-tight">
            {{ debtCount }}
          </span>
          <span v-if="isActive('/debts')" class="absolute -bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 bg-sky-600 rounded-full"></span>
        </div>
        <span class="text-[10px] mt-1 tracking-tight">Sổ Nợ</span>
      </router-link>

      <!-- Tab 4: Chi Phí Vận Hành -->
      <router-link
        to="/expenses"
        class="flex flex-col items-center py-1 px-2 rounded-xl transition-all duration-150 group"
        :class="isActive('/expenses') ? 'text-amber-600 font-semibold' : 'text-slate-500 hover:text-slate-700'"
      >
        <div class="relative">
          <svg class="w-5 h-5 transition-transform group-active:scale-90" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <span v-if="isActive('/expenses')" class="absolute -bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 bg-amber-600 rounded-full"></span>
        </div>
        <span class="text-[10px] mt-1 tracking-tight">Chi Phí</span>
      </router-link>
    </div>
  </nav>
</template>

<script setup>
import { useRoute } from 'vue-router'

defineProps({
  debtCount: {
    type: Number,
    default: 0
  }
})

const route = useRoute()
const isActive = (path) => route.path.startsWith(path)
</script>
