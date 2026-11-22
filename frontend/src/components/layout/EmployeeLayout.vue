<template>
  <div class="min-h-screen bg-slate-50 flex flex-col antialiased selection:bg-sky-500 selection:text-white">
    <!-- Top Header (Gọn gàng, tinh tế, không chiếm chỗ) -->
    <header class="sticky top-0 z-30 bg-white/90 backdrop-blur-md border-b border-slate-200/80 px-4 sm:px-6 py-3">
      <div class="max-w-4xl mx-auto flex items-center justify-between">
        <!-- Logo & Brand -->
        <div class="flex items-center gap-2.5">
          <div class="w-9 h-9 rounded-xl bg-gradient-to-tr from-sky-600 to-cyan-500 flex items-center justify-center text-white shadow-sm shadow-sky-500/20">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
            </svg>
          </div>
          <div>
            <h1 class="text-sm font-bold tracking-tight text-slate-900 leading-tight">OriginGas</h1>
            <p class="text-[11px] font-medium text-slate-500">Cổng Nhân Viên Giao Hàng</p>
          </div>
        </div>

        <!-- Right Side: Shipper Profile & Backend Status -->
        <div class="flex items-center gap-2.5">
          <!-- Backend Status Indicator -->
          <div 
            class="hidden sm:inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold"
            :class="isBackendConnected ? 'bg-emerald-50 text-emerald-700 border border-emerald-200/60' : 'bg-amber-50 text-amber-800 border border-amber-200/60'"
          >
            <span class="w-1.5 h-1.5 rounded-full" :class="isBackendConnected ? 'bg-emerald-500 animate-pulse' : 'bg-amber-500'"></span>
            <span>{{ isBackendConnected ? 'Online (Cổng 8080)' : 'Chế độ Demo (Mock)' }}</span>
          </div>

          <!-- Refresh Button -->
          <button 
            @click="$emit('refresh')" 
            class="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-xl transition-colors active:scale-95" 
            title="Làm mới dữ liệu"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </button>

          <!-- Avatar / User Profile -->
          <div class="flex items-center gap-2 pl-1 sm:border-l sm:border-slate-200 sm:pl-3">
            <div class="w-8 h-8 rounded-full bg-slate-800 text-white flex items-center justify-center font-bold text-xs ring-2 ring-slate-100">
              NV
            </div>
            <div class="hidden sm:block text-left">
              <div class="text-xs font-semibold text-slate-800 leading-tight">Nguyễn Văn Giao</div>
              <div class="text-[10px] text-slate-500">Shipper 01</div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Workspace (Chiếm trọn bề ngang màn hình, có max-w để dễ nhìn) -->
    <main class="flex-1 max-w-4xl w-full mx-auto px-4 sm:px-6 pt-5 pb-24">
      <slot />
    </main>

    <!-- Bottom Navigation Bar (Menu điều hướng nhỏ gọn dưới đáy màn hình) -->
    <BottomNav :debt-count="debtCount" />
  </div>
</template>

<script setup>
import BottomNav from './BottomNav.vue'

defineProps({
  isBackendConnected: {
    type: Boolean,
    default: true
  },
  debtCount: {
    type: Number,
    default: 0
  }
})

defineEmits(['refresh'])
</script>

