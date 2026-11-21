<template>
  <EmployeeLayout :is-backend-connected="!isMockData" @refresh="fetchInventories">
    <!-- Top Hero Section -->
    <div class="relative overflow-hidden rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white p-5 sm:p-7 mb-6 shadow-xl shadow-slate-950/10 border border-slate-800">
      <!-- Background Decorative Lights -->
      <div class="absolute -right-10 -top-10 w-60 h-60 bg-amber-500/20 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute -left-10 -bottom-10 w-60 h-60 bg-sky-500/15 rounded-full blur-3xl pointer-events-none"></div>

      <div class="relative z-10 flex flex-col lg:flex-row lg:items-center justify-between gap-5">
        <div>
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/20 border border-amber-400/30 text-amber-300 text-xs font-bold mb-2 backdrop-blur-md">
            <span class="w-2 h-2 rounded-full bg-amber-400 animate-ping"></span>
            <span>Phân Hệ Quản Lý Kho Vận & Tồn Trữ Gas</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white flex items-center gap-3">
            <span>Kho Gas Doanh Nghiệp</span>
            <span class="text-xl sm:text-2xl">🔥</span>
          </h2>
          <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-xl leading-relaxed">
            Kiểm soát trực quan số lượng bình gas đầy, luân chuyển vỏ rỗng, cảnh báo ngưỡng an toàn chống đứt hàng.
          </p>
        </div>

        <!-- CỘT NÚT THAO TÁC: HÀNG TRÊN (NÚT PHỤ) - HÀNG DƯỚI (NHẬP & XUẤT CHUNG HÀNG) -->
        <div class="flex flex-col sm:items-end gap-2 flex-shrink-0">
          <!-- Hàng trên: Nút phụ Khôi Phục Mẫu -->
          <div v-if="isMockData" class="flex items-center justify-end">
            <button 
              @click="handleResetMock" 
              class="px-3 py-1.5 rounded-xl bg-white/10 hover:bg-white/15 active:scale-95 text-[11px] font-semibold text-slate-300 border border-white/10 backdrop-blur-md transition-all flex items-center gap-1.5"
              title="Đặt lại số liệu kho mặc định ban đầu"
            >
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span>Khôi Phục Mẫu</span>
            </button>
          </div>

          <!-- Hàng dưới: 2 NÚT NHẬP & XUẤT LUÔN ĐI CHUNG 1 HÀNG -->
          <div class="flex items-center gap-2.5 flex-nowrap">
            <!-- Ô 1: NÚT NHẬP KHO GAS (Màu Xanh Ngọc Emerald - Thu nạp bình & vỏ) -->
            <button 
              @click="openStockModalWithMode('IMPORT', null)"
              class="px-4 py-2.5 rounded-2xl bg-gradient-to-r from-emerald-500 via-teal-500 to-emerald-600 hover:from-emerald-400 hover:to-teal-500 active:scale-95 text-white text-xs font-black shadow-lg shadow-emerald-500/25 transition-all flex items-center gap-2 flex-shrink-0 group"
            >
              <div class="w-5 h-5 rounded-lg bg-white/20 flex items-center justify-center text-xs transition-transform group-hover:-translate-y-0.5">
                📥
              </div>
              <span class="whitespace-nowrap">Nhập Kho Gas</span>
            </button>

            <!-- Ô 2: NÚT XUẤT KHO GAS (Màu Cam Đỏ Orange/Rose - Xuất giao hàng & trả vỏ) -->
            <button 
              @click="openStockModalWithMode('EXPORT', null)"
              class="px-4 py-2.5 rounded-2xl bg-gradient-to-r from-amber-500 via-orange-500 to-rose-500 hover:from-amber-400 hover:to-orange-500 active:scale-95 text-white text-xs font-black shadow-lg shadow-orange-500/25 transition-all flex items-center gap-2 flex-shrink-0 group"
            >
              <div class="w-5 h-5 rounded-lg bg-white/20 flex items-center justify-center text-xs transition-transform group-hover:translate-y-0.5">
                📤
              </div>
              <span class="whitespace-nowrap">Xuất Kho Gas</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Banner Demo Mode (Khi Backend chưa bật) -->
    <div 
      v-if="isMockData" 
      class="mb-6 p-4 bg-gradient-to-r from-amber-50/95 via-orange-50/90 to-amber-50/95 border border-amber-200/90 rounded-3xl shadow-sm flex items-center justify-between text-xs text-amber-950"
    >
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-xl bg-amber-500/15 text-amber-700 flex items-center justify-center text-base flex-shrink-0">
          💡
        </div>
        <div>
          <div class="font-bold text-amber-900">Chế độ trải nghiệm giao diện mượt mà (Mock Data)</div>
          <div class="text-[11px] text-amber-700 mt-0.5">
            Dữ liệu gồm 4 loại bình chuẩn theo Database. Mọi thay đổi nhập/xuất kho được tự động lưu trên trình duyệt của bạn!
          </div>
        </div>
      </div>
      <button 
        @click="fetchInventories" 
        class="px-3 py-1.5 rounded-xl bg-white text-amber-800 hover:bg-amber-100 font-bold border border-amber-200 shadow-xs transition-all flex-shrink-0 ml-3"
      >
        Đồng bộ lại
      </button>
    </div>

    <!-- 4 KPI Summary Cards (Thiết kế phong cách Glassmorphism SaaS) -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-3.5 mb-6">
      <!-- 1. Bình Đầy Sẵn Sàng -->
      <div class="relative overflow-hidden bg-white p-4 rounded-3xl border border-emerald-100 shadow-sm shadow-emerald-500/5 group hover:border-emerald-300 transition-all">
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-emerald-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-emerald-800">Bình Đầy Sẵn Sàng</span>
          <div class="w-8 h-8 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-sm border border-emerald-100">
            🟢
          </div>
        </div>
        <div class="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight flex items-baseline gap-1.5">
          <span>{{ stats.totalFull }}</span>
          <span class="text-xs font-bold text-slate-400">bình</span>
        </div>
        <div class="mt-2 text-[11px] text-emerald-600 font-semibold flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          </svg>
          <span>Sẵn sàng phục vụ giao hàng</span>
        </div>
      </div>

      <!-- 2. Vỏ Bình Rỗng -->
      <div class="relative overflow-hidden bg-white p-4 rounded-3xl border border-sky-100 shadow-sm shadow-sky-500/5 group hover:border-sky-300 transition-all">
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-sky-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-sky-800">Vỏ Bình Rỗng</span>
          <div class="w-8 h-8 rounded-xl bg-sky-50 text-sky-600 flex items-center justify-center text-sm border border-sky-100">
            🔵
          </div>
        </div>
        <div class="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight flex items-baseline gap-1.5">
          <span>{{ stats.totalEmpty }}</span>
          <span class="text-xs font-bold text-slate-400">vỏ</span>
        </div>
        <div class="mt-2 text-[11px] text-sky-600 font-semibold flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          <span>Đang chờ chuyển nạp chiết</span>
        </div>
      </div>

      <!-- 3. Cảnh Báo Ngưỡng Hết Hàng -->
      <div 
        class="relative overflow-hidden bg-white p-4 rounded-3xl border shadow-sm transition-all group"
        :class="stats.warningCount > 0 ? 'border-rose-300 bg-rose-50/20 shadow-rose-500/10' : 'border-slate-200/80'"
      >
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-rose-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-rose-800">Cảnh Báo Hết Hàng</span>
          <div class="w-8 h-8 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center text-sm border border-rose-100">
            ⚠️
          </div>
        </div>
        <div class="text-2xl sm:text-3xl font-black tracking-tight flex items-baseline gap-1.5" :class="stats.warningCount > 0 ? 'text-rose-600' : 'text-slate-900'">
          <span>{{ stats.warningCount }}</span>
          <span class="text-xs font-bold text-slate-400">loại bình</span>
        </div>
        <div class="mt-2 text-[11px] font-semibold flex items-center gap-1" :class="stats.warningCount > 0 ? 'text-rose-600 animate-pulse' : 'text-slate-400'">
          <span>{{ stats.warningCount > 0 ? 'Cần nhập bổ sung ngay lập tức' : 'Kho đang ở mức an toàn' }}</span>
        </div>
      </div>

      <!-- 4. Chủng Loại Sản Phẩm -->
      <div class="relative overflow-hidden bg-white p-4 rounded-3xl border border-indigo-100 shadow-sm shadow-indigo-500/5 group hover:border-indigo-300 transition-all">
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-indigo-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-indigo-800">Danh Mục Chủng Loại</span>
          <div class="w-8 h-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center text-sm border border-indigo-100">
            📦
          </div>
        </div>
        <div class="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight flex items-baseline gap-1.5">
          <span>{{ inventories.length }}</span>
          <span class="text-xs font-bold text-slate-400">sản phẩm</span>
        </div>
        <div class="mt-2 text-[11px] text-indigo-600 font-semibold flex items-center gap-1">
          <span>Quy chuẩn phân khúc gas</span>
        </div>
      </div>
    </div>

    <!-- Filter, Search & View Switcher Bar -->
    <div class="bg-white p-3 rounded-3xl border border-slate-200/80 shadow-sm mb-6 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between">
      <!-- Search Box -->
      <div class="relative flex-1">
        <svg class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="Tìm theo tên bình (12kg Xám, Đỏ, 45kg Công Nghiệp, Gas Lon Mini)..." 
          class="w-full pl-10 pr-4 py-2 text-xs text-slate-700 bg-slate-50 border border-slate-200/80 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all placeholder:text-slate-400 font-medium"
        />
      </div>

      <!-- Segmented Status Tabs -->
      <div class="flex items-center gap-1.5 p-1 bg-slate-100/80 rounded-2xl self-start sm:self-auto border border-slate-200/50">
        <button 
          @click="filterTab = 'ALL'" 
          class="px-3 py-1.5 text-xs font-bold rounded-xl transition-all"
          :class="filterTab === 'ALL' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-600 hover:text-slate-900'"
        >
          Tất Cả ({{ inventories.length }})
        </button>
        <button 
          @click="filterTab = 'OK'" 
          class="px-3 py-1.5 text-xs font-bold rounded-xl transition-all flex items-center gap-1.5"
          :class="filterTab === 'OK' ? 'bg-emerald-500 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'"
        >
          <span>Đủ Hàng</span>
        </button>
        <button 
          @click="filterTab = 'WARNING'" 
          class="px-3 py-1.5 text-xs font-bold rounded-xl transition-all flex items-center gap-1.5"
          :class="filterTab === 'WARNING' ? 'bg-rose-500 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'"
        >
          <span>Cảnh Báo ({{ stats.warningCount }})</span>
        </button>
      </div>

      <!-- View Switcher (Grid vs Table) -->
      <div class="hidden sm:flex items-center p-1 bg-slate-100 rounded-2xl border border-slate-200/50">
        <button 
          @click="viewMode = 'GRID'" 
          class="p-1.5 rounded-xl transition-all"
          :class="viewMode === 'GRID' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-400 hover:text-slate-700'"
          title="Chế độ thẻ trực quan"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
          </svg>
        </button>
        <button 
          @click="viewMode = 'TABLE'" 
          class="p-1.5 rounded-xl transition-all"
          :class="viewMode === 'TABLE' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-400 hover:text-slate-700'"
          title="Chế độ bảng chi tiết"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Loading Spinner -->
    <div v-if="loading" class="py-20 text-center text-slate-400">
      <div class="w-9 h-9 border-3 border-amber-500/30 border-t-amber-600 rounded-full animate-spin mx-auto mb-3"></div>
      <p class="text-xs font-bold text-slate-600">Đang cập nhật số liệu tồn kho...</p>
    </div>

    <!-- 1. GRID CARDS VIEW (Thiết Kế Thẻ Trực Quan Hiện Đại) -->
    <div v-else-if="viewMode === 'GRID'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div 
        v-for="item in filteredInventories" 
        :key="item.id"
        class="bg-white rounded-3xl p-5 border transition-all duration-200 hover:shadow-md flex flex-col justify-between group"
        :class="item.isWarning 
          ? 'border-rose-300 ring-2 ring-rose-500/10 bg-gradient-to-b from-rose-50/30 via-white to-white' 
          : 'border-slate-200/80 hover:border-slate-300 bg-white'"
      >
        <div>
          <!-- Top Row: Icon, Name & Status Badge -->
          <div class="flex items-start justify-between gap-3 mb-3">
            <div class="flex items-center gap-3">
              <div 
                class="w-13 h-13 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0 shadow-sm border transition-transform group-hover:scale-105"
                :class="getIconBgClass(item.containerType)"
              >
                {{ getGasIcon(item.containerType) }}
              </div>
              <div>
                <span class="text-[10px] font-extrabold uppercase tracking-widest text-slate-400">OriginGas Certified</span>
                <h3 class="text-base font-black text-slate-900 tracking-tight leading-snug">
                  {{ item.containerType }}
                </h3>
              </div>
            </div>

            <!-- Warning Badge -->
            <div class="flex-shrink-0">
              <span 
                v-if="item.isWarning" 
                class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-black bg-rose-100 text-rose-700 border border-rose-200 animate-pulse shadow-xs"
              >
                <span class="w-1.5 h-1.5 rounded-full bg-rose-600"></span>
                <span>SẮP HẾT (≤ {{ item.warningThreshold }})</span>
              </span>
              <span 
                v-else 
                class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-black bg-emerald-50 text-emerald-700 border border-emerald-200"
              >
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                <span>TỒN KHO AN TOÀN</span>
              </span>
            </div>
          </div>

          <!-- Description -->
          <p class="text-xs text-slate-500 line-clamp-2 mb-4 leading-relaxed">
            {{ getGasDescription(item.containerType) }}
          </p>

          <!-- Capacity Ratio & Progress Bar -->
          <div class="space-y-1.5 mb-4 p-3 bg-slate-50/80 rounded-2xl border border-slate-100">
            <div class="flex items-center justify-between text-xs font-bold text-slate-700">
              <span class="flex items-center gap-1.5">
                <span class="w-2 h-2 rounded-full" :class="item.isWarning ? 'bg-rose-500' : 'bg-emerald-500'"></span>
                <span>Bình Đầy Sẵn Có:</span>
              </span>
              <span :class="item.isWarning ? 'text-rose-600' : 'text-emerald-700'">
                {{ item.fullQuantity }} bình
              </span>
            </div>

            <!-- Dynamic Progress Bar -->
            <div class="w-full h-2.5 bg-slate-200 rounded-full overflow-hidden flex">
              <div 
                class="h-full rounded-full transition-all duration-500"
                :class="item.isWarning ? 'bg-gradient-to-r from-rose-500 to-amber-500' : 'bg-gradient-to-r from-emerald-500 to-teal-400'"
                :style="{ width: getStockPercentage(item) + '%' }"
              ></div>
            </div>

            <div class="flex items-center justify-between text-[11px] text-slate-400 pt-0.5">
              <span>Ngưỡng cảnh báo: <strong class="text-slate-600">{{ item.warningThreshold }} bình</strong></span>
              <span>Vỏ rỗng: <strong class="text-sky-600">{{ item.emptyQuantity }} vỏ</strong></span>
            </div>
          </div>
        </div>

        <!-- Bottom Actions Row: 2 Nút Riêng Biệt Cho Từng Loại Bình -->
        <div class="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
          <span class="text-[11px] text-slate-400">
            Cập nhật: <strong class="text-slate-600 font-semibold">{{ formatDate(item.updatedAt) }}</strong>
          </span>

          <div class="flex items-center gap-1.5">
            <!-- Nút 1: Nhập Kho (Xanh Emerald) -->
            <button 
              @click="openStockModalWithMode('IMPORT', item)"
              class="px-2.5 py-1.5 rounded-xl bg-emerald-50 hover:bg-emerald-100 active:scale-95 text-emerald-800 text-xs font-extrabold border border-emerald-200 transition-all flex items-center gap-1"
              title="Nhập thêm bình đầy hoặc thu vỏ"
            >
              <span>📥 Nhập</span>
            </button>

            <!-- Nút 2: Xuất Kho (Cam/Đỏ) -->
            <button 
              @click="openStockModalWithMode('EXPORT', item)"
              class="px-2.5 py-1.5 rounded-xl bg-rose-50 hover:bg-rose-100 active:scale-95 text-rose-800 text-xs font-extrabold border border-rose-200 transition-all flex items-center gap-1"
              title="Xuất bình đầy hoặc trả vỏ nhà máy"
            >
              <span>📤 Xuất</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 2. TABLE VIEW (Chế Độ Bảng Dữ Liệu Chuyên Nghiệp) -->
    <div v-else class="bg-white rounded-3xl border border-slate-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-50/70 text-[11px] font-extrabold text-slate-500 uppercase tracking-wider">
              <th class="py-3.5 px-4">Chủng Loại Bình</th>
              <th class="py-3.5 px-4 text-center">Bình Đầy</th>
              <th class="py-3.5 px-4 text-center">Vỏ Rỗng</th>
              <th class="py-3.5 px-4 text-center">Ngưỡng Báo Động</th>
              <th class="py-3.5 px-4">Trạng Thái Tồn</th>
              <th class="py-3.5 px-4">Cập Nhật Cuối</th>
              <th class="py-3.5 px-4 text-right">Thao Tác Kho</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 text-xs">
            <tr 
              v-for="item in filteredInventories" 
              :key="item.id"
              class="hover:bg-slate-50/80 transition-colors"
              :class="item.isWarning ? 'bg-rose-50/20' : ''"
            >
              <!-- Name & Icon -->
              <td class="py-3.5 px-4 font-bold text-slate-900">
                <div class="flex items-center gap-2.5">
                  <span class="text-xl">{{ getGasIcon(item.containerType) }}</span>
                  <div>
                    <div class="text-slate-900">{{ item.containerType }}</div>
                    <div class="text-[10px] text-slate-400 font-normal">OriginGas Official</div>
                  </div>
                </div>
              </td>

              <!-- Full Quantity -->
              <td class="py-3.5 px-4 text-center">
                <span 
                  class="inline-block px-2.5 py-1 rounded-xl text-xs font-black"
                  :class="item.isWarning ? 'bg-rose-100 text-rose-700' : 'bg-emerald-50 text-emerald-700'"
                >
                  {{ item.fullQuantity }}
                </span>
              </td>

              <!-- Empty Quantity -->
              <td class="py-3.5 px-4 text-center">
                <span class="inline-block px-2.5 py-1 rounded-xl text-xs font-black bg-sky-50 text-sky-700">
                  {{ item.emptyQuantity }}
                </span>
              </td>

              <!-- Threshold -->
              <td class="py-3.5 px-4 text-center font-bold text-slate-500">
                {{ item.warningThreshold }}
              </td>

              <!-- Status Badge -->
              <td class="py-3.5 px-4">
                <span 
                  v-if="item.isWarning" 
                  class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-black bg-rose-100 text-rose-700 border border-rose-200 animate-pulse"
                >
                  ⚠️ Sắp hết
                </span>
                <span 
                  v-else 
                  class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200"
                >
                  ✅ An toàn
                </span>
              </td>

              <!-- Date -->
              <td class="py-3.5 px-4 text-slate-400 font-medium text-[11px]">
                {{ formatDate(item.updatedAt) }}
              </td>

              <!-- Actions: 2 Nút Nhập / Xuất Riêng Biệt -->
              <td class="py-3.5 px-4 text-right">
                <div class="inline-flex items-center gap-1.5">
                  <button 
                    @click="openStockModalWithMode('IMPORT', item)"
                    class="px-2.5 py-1 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-[11px] font-extrabold transition-all shadow-xs"
                  >
                    + Nhập
                  </button>
                  <button 
                    @click="openStockModalWithMode('EXPORT', item)"
                    class="px-2.5 py-1 rounded-xl bg-orange-600 hover:bg-orange-700 text-white text-[11px] font-extrabold transition-all shadow-xs"
                  >
                    - Xuất
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Stock Adjustment Modal Dialog -->
    <div 
      v-if="modal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/60 backdrop-blur-sm transition-opacity"
      @click.self="closeStockModal"
    >
      <div class="bg-white w-full max-w-lg rounded-3xl shadow-2xl border border-slate-200 overflow-hidden transform transition-all animate-in fade-in zoom-in-95 duration-150">
        <!-- Modal Top Header -->
        <div 
          class="px-6 py-4 border-b flex items-center justify-between"
          :class="stockModalMode === 'IMPORT' 
            ? 'bg-gradient-to-r from-emerald-50 via-teal-50/50 to-white border-emerald-100' 
            : 'bg-gradient-to-r from-amber-50 via-rose-50/50 to-white border-rose-100'"
        >
          <div class="flex items-center gap-3">
            <div 
              class="w-10 h-10 rounded-2xl flex items-center justify-center text-xl flex-shrink-0"
              :class="stockModalMode === 'IMPORT' ? 'bg-emerald-500/15 text-emerald-700' : 'bg-rose-500/15 text-rose-700'"
            >
              {{ stockModalMode === 'IMPORT' ? '📥' : '📤' }}
            </div>
            <div>
              <h3 class="text-sm font-black text-slate-900">
                {{ stockModalMode === 'IMPORT' ? 'Lập Phiếu Nhập Kho Gas' : 'Lập Phiếu Xuất Kho Gas' }}
              </h3>
              <p class="text-[11px] text-slate-500 font-medium">
                {{ stockModalMode === 'IMPORT' 
                  ? 'Tăng số lượng bình đầy từ nhà máy hoặc thu vỏ rỗng vào kho (Cho phép thêm loại bình mới)' 
                  : 'Giảm số lượng bình giao hàng hoặc trả vỏ đi chiết nạp' }}
              </p>
            </div>
          </div>
          <button @click="closeStockModal" class="p-2 text-slate-400 hover:text-slate-700 hover:bg-white rounded-2xl transition-all">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Mode Switcher Tabs Inside Modal -->
        <div class="px-6 pt-4 pb-1">
          <div class="grid grid-cols-2 gap-2 p-1 bg-slate-100 rounded-2xl border border-slate-200/60">
            <button
              type="button"
              @click="switchModalMode('IMPORT')"
              class="py-2 text-xs font-black rounded-xl transition-all flex items-center justify-center gap-1.5"
              :class="stockModalMode === 'IMPORT' 
                ? 'bg-emerald-600 text-white shadow-sm' 
                : 'text-slate-600 hover:text-slate-900'"
            >
              <span>📥 Tab Nhập Kho</span>
            </button>
            <button
              type="button"
              @click="switchModalMode('EXPORT')"
              class="py-2 text-xs font-black rounded-xl transition-all flex items-center justify-center gap-1.5"
              :class="stockModalMode === 'EXPORT' 
                ? 'bg-orange-600 text-white shadow-sm' 
                : 'text-slate-600 hover:text-slate-900'"
            >
              <span>📤 Tab Xuất Kho</span>
            </button>
          </div>
        </div>

        <!-- Form Body -->
        <form @submit.prevent="handleStockSubmit" class="p-6 space-y-4 pt-3">
          <!-- 1. TRƯỜNG LOẠI BÌNH CHO NHẬP KHO: DÙNG INPUT ĐỂ TỰ DO NHẬP LOẠI MỚI HOẶC CHỌN LOẠI CŨ -->
          <div v-if="stockModalMode === 'IMPORT'">
            <div class="flex items-center justify-between mb-1.5">
              <label class="block text-xs font-extrabold text-slate-700">
                Loại Bình Gas <span class="text-rose-500">*</span>
              </label>
              <span v-if="isNewTypeDetected" class="text-[10px] font-black text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded-full border border-emerald-300 animate-pulse">
                ✨ Phát hiện loại bình mới
              </span>
            </div>

            <!-- Ô INPUT LINH HOẠT VỚI DATALIST GỢI Ý -->
            <div class="relative">
              <input 
                type="text" 
                v-model="stockForm.containerType"
                list="gasTypeSuggestions"
                placeholder="Chọn loại bình có sẵn hoặc gõ tên loại bình mới tùy ý..." 
                required
                class="w-full px-4 py-2.5 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 transition-all placeholder:text-slate-400"
              />
              <datalist id="gasTypeSuggestions">
                <option v-for="inv in inventories" :key="inv.id" :value="inv.containerType">
                  Tồn đầy: {{ inv.fullQuantity }} | Vỏ rỗng: {{ inv.emptyQuantity }}
                </option>
              </datalist>
            </div>

            <!-- Chip gợi ý bấm chọn nhanh các loại bình đang có trong kho -->
            <div class="flex items-center gap-1.5 flex-wrap mt-2">
              <span class="text-[10px] font-bold text-slate-400">Chọn nhanh:</span>
              <button 
                type="button"
                v-for="inv in inventories" 
                :key="inv.id"
                @click="selectExistingGasType(inv)"
                class="px-2.5 py-1 rounded-xl text-[10px] font-extrabold border transition-all flex items-center gap-1"
                :class="stockForm.containerType === inv.containerType 
                  ? 'bg-emerald-600 text-white border-emerald-700 shadow-xs' 
                  : 'bg-white hover:bg-slate-100 text-slate-700 border-slate-200'"
              >
                <span>{{ getGasIcon(inv.containerType) }}</span>
                <span>{{ inv.containerType }}</span>
              </button>
            </div>

            <!-- Khung cấu hình thêm khi nhập loại bình mới -->
            <div v-if="isNewTypeDetected" class="mt-3 p-3.5 bg-emerald-50/90 border border-emerald-200 rounded-2xl space-y-2 text-xs">
              <div class="flex items-center gap-1.5 text-emerald-900 font-extrabold">
                <span class="text-base">✨</span>
                <span>Thêm Chủng Loại Mới: <u>{{ stockForm.containerType }}</u></span>
              </div>
              <p class="text-[11px] text-emerald-800 font-medium leading-relaxed">
                Hệ thống sẽ tự động khởi tạo danh mục này vào kho gas của doanh nghiệp mà không cần trang CRUD cấu hình riêng!
              </p>
              <div class="pt-1">
                <label class="block text-[11px] font-extrabold text-slate-700 mb-1">
                  Cài đặt Ngưỡng Cảnh Báo Sắp Hết Hàng (bình):
                </label>
                <input 
                  type="number" 
                  min="1" 
                  max="1000" 
                  v-model.number="stockForm.warningThreshold"
                  class="w-full px-3 py-1.5 text-xs font-bold text-slate-800 bg-white border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                  placeholder="Mặc định: 10 bình"
                />
              </div>
            </div>
          </div>

          <!-- 1. TRƯỜNG LOẠI BÌNH CHO XUẤT KHO: CHỈ CHỌN TỪ CÁC LOẠI ĐANG CÓ SẴN -->
          <div v-else>
            <label class="block text-xs font-extrabold text-slate-700 mb-1.5">
              Chọn Loại Bình Gas Cần Xuất <span class="text-rose-500">*</span>
            </label>
            <div class="relative">
              <select 
                v-model="stockForm.inventoryId" 
                required
                class="w-full px-3.5 py-2.5 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all cursor-pointer appearance-none"
              >
                <option v-for="inv in inventories" :key="inv.id" :value="inv.id">
                  {{ inv.containerType }} — (Tồn đầy: {{ inv.fullQuantity }} | Vỏ rỗng: {{ inv.emptyQuantity }})
                </option>
              </select>
              <div class="absolute right-3.5 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400">
                ▼
              </div>
            </div>
          </div>

          <!-- 2. Action Type Cho Phân Hệ NHẬP KHO -->
          <div v-if="stockModalMode === 'IMPORT'">
            <label class="block text-xs font-extrabold text-slate-700 mb-2">
              Lựa Chọn Nghiệp Vụ Nhập Kho <span class="text-rose-500">*</span>
            </label>
            <div class="grid grid-cols-2 gap-2.5">
              <button
                type="button"
                @click="stockForm.type = 'IMPORT_FULL'"
                class="p-3.5 text-left rounded-2xl border transition-all text-xs flex flex-col justify-between"
                :class="stockForm.type === 'IMPORT_FULL' 
                  ? 'border-emerald-500 bg-emerald-50/90 text-emerald-950 font-black ring-2 ring-emerald-500/20 shadow-xs' 
                  : 'border-slate-200 bg-white text-slate-700 hover:bg-slate-50 font-semibold'"
              >
                <div class="flex items-center gap-1.5">
                  <span class="text-lg">📥</span>
                  <span>Nhập Bình Đầy</span>
                </div>
                <span class="text-[10px] text-slate-500 mt-1">Từ xưởng chiết nạp về kho</span>
              </button>

              <button
                type="button"
                @click="stockForm.type = 'IMPORT_EMPTY'"
                class="p-3.5 text-left rounded-2xl border transition-all text-xs flex flex-col justify-between"
                :class="stockForm.type === 'IMPORT_EMPTY' 
                  ? 'border-teal-500 bg-teal-50/90 text-teal-950 font-black ring-2 ring-teal-500/20 shadow-xs' 
                  : 'border-slate-200 bg-white text-slate-700 hover:bg-slate-50 font-semibold'"
              >
                <div class="flex items-center gap-1.5">
                  <span class="text-lg">🔄</span>
                  <span>Thu Hồi Vỏ Rỗng</span>
                </div>
                <span class="text-[10px] text-slate-500 mt-1">Nhận vỏ bình từ khách hàng</span>
              </button>
            </div>
          </div>

          <!-- 2. Action Type Cho Phân Hệ XUẤT KHO -->
          <div v-else>
            <label class="block text-xs font-extrabold text-slate-700 mb-2">
              Lựa Chọn Nghiệp Vụ Xuất Kho <span class="text-rose-500">*</span>
            </label>
            <div class="grid grid-cols-2 gap-2.5">
              <button
                type="button"
                @click="stockForm.type = 'EXPORT_FULL'"
                class="p-3.5 text-left rounded-2xl border transition-all text-xs flex flex-col justify-between"
                :class="stockForm.type === 'EXPORT_FULL' 
                  ? 'border-orange-500 bg-orange-50/90 text-orange-950 font-black ring-2 ring-orange-500/20 shadow-xs' 
                  : 'border-slate-200 bg-white text-slate-700 hover:bg-slate-50 font-semibold'"
              >
                <div class="flex items-center gap-1.5">
                  <span class="text-lg">📤</span>
                  <span>Xuất Bình Đầy</span>
                </div>
                <span class="text-[10px] text-slate-500 mt-1">Xuất kho đi giao / đại lý</span>
              </button>

              <button
                type="button"
                @click="stockForm.type = 'EXPORT_EMPTY'"
                class="p-3.5 text-left rounded-2xl border transition-all text-xs flex flex-col justify-between"
                :class="stockForm.type === 'EXPORT_EMPTY' 
                  ? 'border-purple-500 bg-purple-50/90 text-purple-950 font-black ring-2 ring-purple-500/20 shadow-xs' 
                  : 'border-slate-200 bg-white text-slate-700 hover:bg-slate-50 font-semibold'"
              >
                <div class="flex items-center gap-1.5">
                  <span class="text-lg">🚚</span>
                  <span>Trả Vỏ Nhà Máy</span>
                </div>
                <span class="text-[10px] text-slate-500 mt-1">Vận chuyển vỏ đi chiết nạp</span>
              </button>
            </div>
          </div>

          <!-- 3. Quantity Stepper + Quick Presets -->
          <div>
            <label class="block text-xs font-extrabold text-slate-700 mb-1.5">
              Số Lượng {{ stockModalMode === 'IMPORT' ? 'Nhập' : 'Xuất' }} (Bình/Vỏ) <span class="text-rose-500">*</span>
            </label>
            <div class="flex items-center border border-slate-200 rounded-2xl bg-slate-50 overflow-hidden mb-2">
              <button 
                type="button" 
                @click="stockForm.quantity = Math.max(1, stockForm.quantity - 1)"
                class="px-4 py-2.5 text-slate-600 hover:bg-slate-200 font-black text-sm transition-colors active:scale-95"
              >
                -
              </button>
              <input 
                type="number" 
                min="1" 
                max="10000" 
                v-model.number="stockForm.quantity" 
                required
                class="w-full text-center py-2.5 text-sm font-black text-slate-900 bg-transparent focus:outline-none"
              />
              <button 
                type="button" 
                @click="stockForm.quantity = stockForm.quantity + 1"
                class="px-4 py-2.5 text-slate-600 hover:bg-slate-200 font-black text-sm transition-colors active:scale-95"
              >
                +
              </button>
            </div>

            <!-- Quick Presets (+5, +10, +20, +50) -->
            <div class="flex items-center gap-1.5">
              <span class="text-[10px] font-bold text-slate-400">Chọn nhanh:</span>
              <button 
                type="button"
                v-for="preset in [5, 10, 20, 50, 100]" 
                :key="preset"
                @click="stockForm.quantity = preset"
                class="px-2 py-0.5 rounded-lg text-[10px] font-extrabold border transition-all"
                :class="stockForm.quantity === preset 
                  ? (stockModalMode === 'IMPORT' ? 'bg-emerald-600 text-white border-emerald-700' : 'bg-orange-600 text-white border-orange-700')
                  : 'bg-slate-100 hover:bg-slate-200 text-slate-700 border-slate-200'"
              >
                +{{ preset }}
              </button>
            </div>
          </div>

          <!-- 4. Real-time Simulation Preview -->
          <div v-if="selectedInventoryPreview || isNewTypeDetected" class="p-3.5 bg-slate-100/80 rounded-2xl border border-slate-200 space-y-2 text-xs">
            <div class="flex items-center justify-between text-slate-500">
              <span>Tồn hiện tại:</span>
              <span class="font-bold text-slate-800">
                {{ selectedInventoryPreview ? `${selectedInventoryPreview.fullQuantity} bình đầy | ${selectedInventoryPreview.emptyQuantity} vỏ rỗng` : 'Chưa có trong kho (Mới)' }}
              </span>
            </div>
            <div class="flex items-center justify-between text-slate-900 font-extrabold pt-1 border-t border-slate-200/60">
              <span>Sau khi {{ stockModalMode === 'IMPORT' ? 'nhập' : 'xuất' }}:</span>
              <span :class="previewAfterStock.valid ? 'text-emerald-700 font-black text-sm' : 'text-rose-600 font-black text-sm'">
                {{ previewAfterStock.full }} bình đầy | {{ previewAfterStock.empty }} vỏ rỗng
              </span>
            </div>
            <p v-if="!previewAfterStock.valid" class="text-rose-600 text-[11px] font-bold">
              ⚠️ Không thể xuất: Số lượng vượt quá số lượng hiện có trong kho!
            </p>
          </div>

          <!-- Error Alert -->
          <div v-if="modalError" class="p-3 bg-rose-50 border border-rose-200 text-rose-800 text-xs rounded-2xl font-bold flex items-center gap-2">
            <span>⚠️</span>
            <span>{{ modalError }}</span>
          </div>

          <!-- Buttons -->
          <div class="pt-3 flex items-center justify-end gap-2.5 border-t border-slate-100">
            <button 
              type="button" 
              @click="closeStockModal" 
              class="px-4 py-2.5 text-xs font-bold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-2xl transition-all"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="stockSubmitting || !previewAfterStock.valid"
              class="px-6 py-2.5 text-white text-xs font-black rounded-2xl shadow-md transition-all active:scale-95 disabled:opacity-50 disabled:pointer-events-none"
              :class="stockModalMode === 'IMPORT' 
                ? 'bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 shadow-emerald-500/20' 
                : 'bg-gradient-to-r from-amber-500 to-orange-600 hover:from-amber-600 hover:to-orange-700 shadow-orange-500/20'"
            >
              <span v-if="stockSubmitting">Đang lưu CSDL...</span>
              <span v-else>{{ stockModalMode === 'IMPORT' ? 'Xác Nhận Nhập Kho' : 'Xác Nhận Xuất Kho' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import { inventoryService } from '../services/inventoryService'

const inventories = ref([])
const loading = ref(false)
const isMockData = ref(false)
const searchKeyword = ref('')
const filterTab = ref('ALL') // ALL, OK, WARNING
const viewMode = ref('GRID') // GRID, TABLE

// Modal state
const modal = ref({
  isOpen: false
})
const stockModalMode = ref('IMPORT') // 'IMPORT' hoặc 'EXPORT'

const stockForm = ref({
  inventoryId: '',
  containerType: 'Bình 12kg Xám',
  warningThreshold: 10,
  quantity: 10,
  type: 'IMPORT_FULL'
})
const stockSubmitting = ref(false)
const modalError = ref('')

// Load danh sách kho
const fetchInventories = async () => {
  loading.value = true
  try {
    const res = await inventoryService.getInventories()
    inventories.value = res.data
    isMockData.value = res.isMock
  } catch (err) {
    console.error('Lỗi nạp tồn kho:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchInventories()
})

// KPI Statistics
const stats = computed(() => {
  const totalFull = inventories.value.reduce((sum, item) => sum + (Number(item.fullQuantity) || 0), 0)
  const totalEmpty = inventories.value.reduce((sum, item) => sum + (Number(item.emptyQuantity) || 0), 0)
  const warningCount = inventories.value.filter(item => item.isWarning).length
  return { totalFull, totalEmpty, warningCount }
})

// Filtered Inventories
const filteredInventories = computed(() => {
  return inventories.value.filter(item => {
    // 1. Keyword search
    const matchesKeyword = item.containerType.toLowerCase().includes(searchKeyword.value.toLowerCase().trim())
    if (!matchesKeyword) return false

    // 2. Tab filter
    if (filterTab.value === 'WARNING') {
      return item.isWarning
    }
    if (filterTab.value === 'OK') {
      return !item.isWarning
    }
    return true
  })
})

// Tự động phát hiện nếu người dùng gõ tên loại bình mới chưa có trong kho
const isNewTypeDetected = computed(() => {
  if (stockModalMode.value !== 'IMPORT') return false
  const name = (stockForm.value.containerType || '').trim().toLowerCase()
  if (!name) return false
  return !inventories.value.some(i => i.containerType.toLowerCase() === name)
})

// Preview selected item in modal
const selectedInventoryPreview = computed(() => {
  if (stockModalMode.value === 'IMPORT') {
    const name = (stockForm.value.containerType || '').trim().toLowerCase()
    return inventories.value.find(i => i.containerType.toLowerCase() === name)
  }
  return inventories.value.find(i => i.id === stockForm.value.inventoryId)
})

const previewAfterStock = computed(() => {
  const item = selectedInventoryPreview.value
  const qty = Number(stockForm.value.quantity) || 0

  if (!item) {
    // Trường hợp là loại bình mới hoàn toàn
    if (stockModalMode.value === 'IMPORT') {
      const full = stockForm.value.type === 'IMPORT_FULL' ? qty : 0
      const empty = stockForm.value.type === 'IMPORT_EMPTY' ? qty : 0
      return { full, empty, valid: true }
    }
    return { full: 0, empty: 0, valid: true }
  }

  let full = item.fullQuantity
  let empty = item.emptyQuantity
  let valid = true

  switch (stockForm.value.type) {
    case 'IMPORT_FULL':
      full += qty
      break
    case 'EXPORT_FULL':
      full -= qty
      if (full < 0) valid = false
      break
    case 'IMPORT_EMPTY':
      empty += qty
      break
    case 'EXPORT_EMPTY':
      empty -= qty
      if (empty < 0) valid = false
      break
  }

  return { full, empty, valid }
})

// Stock percentage calculation for progress bar
const getStockPercentage = (item) => {
  const target = Math.max(item.warningThreshold * 3, 100)
  const pct = Math.min(100, Math.round((item.fullQuantity / target) * 100))
  return Math.max(10, pct)
}

// Bấm chọn nhanh loại bình có sẵn
const selectExistingGasType = (inv) => {
  stockForm.value.containerType = inv.containerType
  stockForm.value.inventoryId = inv.id
}

// Modal Open/Close Tách Biệt Nhập và Xuất
const openStockModalWithMode = (mode, item) => {
  modalError.value = ''
  stockModalMode.value = mode
  if (item) {
    stockForm.value.inventoryId = item.id
    stockForm.value.containerType = item.containerType
  } else if (inventories.value.length > 0) {
    stockForm.value.inventoryId = inventories.value[0].id
    stockForm.value.containerType = inventories.value[0].containerType
  } else {
    stockForm.value.containerType = 'Bình 12kg Xám'
  }
  stockForm.value.quantity = 10
  stockForm.value.warningThreshold = 10

  // Đặt thao tác tương ứng
  if (mode === 'IMPORT') {
    stockForm.value.type = 'IMPORT_FULL'
  } else {
    stockForm.value.type = 'EXPORT_FULL'
  }

  modal.value.isOpen = true
}

// Đổi mode ngay trong modal
const switchModalMode = (mode) => {
  stockModalMode.value = mode
  if (mode === 'IMPORT') {
    stockForm.value.type = 'IMPORT_FULL'
  } else {
    stockForm.value.type = 'EXPORT_FULL'
    // Đảm bảo chọn đúng id có sẵn khi chuyển sang xuất
    if (!stockForm.value.inventoryId && inventories.value.length > 0) {
      stockForm.value.inventoryId = inventories.value[0].id
    }
  }
}

const closeStockModal = () => {
  modal.value.isOpen = false
  modalError.value = ''
}

// Submit Stock Adjustment
const handleStockSubmit = async () => {
  modalError.value = ''
  if (!previewAfterStock.value.valid) {
    modalError.value = 'Số lượng xuất kho vượt quá số lượng hiện có!'
    return
  }

  stockSubmitting.value = true
  try {
    if (stockModalMode.value === 'IMPORT') {
      // Dùng hàm nhập kho linh hoạt (hỗ trợ cả loại cũ và loại mới)
      await inventoryService.importStockFlexible({
        containerType: stockForm.value.containerType,
        quantity: stockForm.value.quantity,
        type: stockForm.value.type,
        warningThreshold: stockForm.value.warningThreshold || 10
      })
    } else {
      // Xuất kho cho loại bình đã chọn
      await inventoryService.updateStock(stockForm.value.inventoryId, {
        quantity: stockForm.value.quantity,
        type: stockForm.value.type
      })
    }
    closeStockModal()
    await fetchInventories()
  } catch (err) {
    modalError.value = err.message || 'Lỗi khi cập nhật kho gas'
  } finally {
    stockSubmitting.value = false
  }
}

// Reset Mock data
const handleResetMock = () => {
  if (confirm('Bạn có chắc muốn đặt lại dữ liệu mẫu của 4 loại bình gas?')) {
    inventoryService.resetMockData()
    fetchInventories()
  }
}

// Helper formats
const getGasIcon = (type) => {
  if (type.includes('45kg')) return '🏭'
  if (type.includes('Đỏ')) return '🔴'
  if (type.includes('Xám')) return '🔘'
  if (type.includes('Mini')) return '🥫'
  return '🛢️'
}

const getIconBgClass = (type) => {
  if (type.includes('45kg')) return 'bg-purple-50 border-purple-200 text-purple-700'
  if (type.includes('Đỏ')) return 'bg-rose-50 border-rose-200 text-rose-700'
  if (type.includes('Xám')) return 'bg-slate-100 border-slate-300 text-slate-700'
  if (type.includes('Mini')) return 'bg-amber-50 border-amber-200 text-amber-700'
  return 'bg-sky-50 border-sky-200 text-sky-700'
}

const getGasDescription = (type) => {
  if (type.includes('12kg Xám')) return 'Bình gas gia đình thông dụng, vỏ xám tiêu chuẩn, độ bền cao.'
  if (type.includes('12kg Đỏ')) return 'Bình gas cao cấp Petro ngọn lửa xanh, tối ưu nhiệt lượng và chống rò rỉ.'
  if (type.includes('45kg')) return 'Bình công nghiệp dung tích lớn cho nhà hàng, khách sạn và bếp ăn công nghiệp.'
  if (type.includes('Mini')) return 'Lon gas du lịch siêu nhẹ, tiện lợi dùng cho bếp mini ăn lẩu dã ngoại.'
  return 'Bình gas thương mại OriginGas'
}

const formatDate = (isoString) => {
  if (!isoString) return 'Vừa xong'
  try {
    const d = new Date(isoString)
    return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) + ' ' + d.toLocaleDateString('vi-VN')
  } catch {
    return 'Vừa xong'
  }
}
</script>
