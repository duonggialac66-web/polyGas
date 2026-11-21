<template>
  <EmployeeLayout :is-backend-connected="true" wide>
    <!-- Top Hero Section (Giao diện Quản trị viên sang trọng) -->
    <div class="relative overflow-hidden rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white p-5 sm:p-7 mb-6 shadow-xl shadow-slate-950/10 border border-slate-800">
      <!-- Ambient decorative glow lights -->
      <div class="absolute -right-10 -top-10 w-72 h-72 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute -left-10 -bottom-10 w-72 h-72 bg-emerald-500/15 rounded-full blur-3xl pointer-events-none"></div>

      <div class="relative z-10 flex flex-col lg:flex-row lg:items-center justify-between gap-5">
        <div>
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/20 border border-indigo-400/30 text-indigo-300 text-xs font-bold mb-2 backdrop-blur-md">
            <span class="w-2 h-2 rounded-full bg-indigo-400 animate-ping"></span>
            <span>🛡️ Phân Hệ Quản Trị Viên (Admin Portal)</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white flex items-center gap-3">
            <span>Báo Cáo & Đối Soát Thu Chi Nhân Viên</span>
            <span class="text-xl sm:text-2xl">📑</span>
          </h2>
          <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-2xl leading-relaxed">
            Giám sát toàn diện doanh thu giao gas, đối soát tiền mặt thực thu, kiểm soát chi phí ca trực và công nợ của từng nhân viên giao hàng theo thời gian thực.
          </p>
        </div>

        <div class="flex items-center gap-2.5 flex-wrap">
          <!-- Nút Thêm Chi Phí Phát Sinh -->
          <button 
            @click="openExpenseModal"
            class="px-4 py-2.5 rounded-2xl bg-rose-500/20 hover:bg-rose-500/30 text-rose-300 border border-rose-400/30 text-xs font-bold backdrop-blur-md transition-all flex items-center gap-1.5 active:scale-95"
            title="Kê khai khoản chi phát sinh trong ca"
          >
            <span>➕ Kê Khai Chi Phí</span>
          </button>

          <!-- Nút In Báo Cáo -->
          <button 
            @click="handlePrintReport"
            class="px-4 py-2.5 rounded-2xl bg-white/10 hover:bg-white/15 active:scale-95 text-xs font-semibold text-slate-200 border border-white/10 backdrop-blur-md transition-all flex items-center gap-1.5"
            title="In bảng đối soát hoặc lưu file PDF"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
            </svg>
            <span>In Báo Cáo</span>
          </button>

          <!-- Nút Xuất Excel -->
          <button 
            @click="handleExportCSV"
            class="px-4.5 py-2.5 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-400 hover:to-teal-500 active:scale-95 text-white text-xs font-black shadow-lg shadow-emerald-500/25 transition-all flex items-center gap-1.5"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <span>Xuất Excel / CSV</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 6 KPI Financial Summary Cards (Bảng Đối Soát Toàn Diện Thu - Chi - Nộp Két) -->
    <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3.5 mb-6">
      <!-- 1. Sản Lượng Bình Giao -->
      <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm hover:border-sky-300 transition-all">
        <div class="flex items-center justify-between text-slate-500 mb-1">
          <span class="text-[10px] font-extrabold uppercase tracking-wider">Sản Lượng Giao</span>
          <span class="text-sm">🛢️</span>
        </div>
        <div class="text-xl sm:text-2xl font-black text-slate-900 tracking-tight flex items-baseline gap-1">
          <span>{{ stats.totalQuantity }}</span>
          <span class="text-xs font-bold text-slate-400 font-normal">bình</span>
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Tổng bình đã xuất</p>
      </div>

      <!-- 2. Tổng Doanh Thu Bán -->
      <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm hover:border-indigo-300 transition-all">
        <div class="flex items-center justify-between text-slate-500 mb-1">
          <span class="text-[10px] font-extrabold uppercase tracking-wider">Tổng Doanh Số</span>
          <span class="text-sm">💵</span>
        </div>
        <div class="text-base sm:text-lg font-black text-slate-900 tracking-tight">
          {{ formatCurrency(stats.totalRevenue) }}
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Giá trị hàng xuất</p>
      </div>

      <!-- 3. Thực Thu Tiền Mặt (Thu +) -->
      <div class="bg-white p-4 rounded-3xl border border-emerald-100 shadow-sm shadow-emerald-500/5 hover:border-emerald-300 transition-all">
        <div class="flex items-center justify-between text-emerald-700 mb-1">
          <span class="text-[10px] font-extrabold uppercase tracking-wider">Tiền Đã Thu (+)</span>
          <span class="text-sm">🟢</span>
        </div>
        <div class="text-base sm:text-lg font-black text-emerald-600 tracking-tight">
          {{ formatCurrency(stats.actualReceived) }}
        </div>
        <p class="text-[10px] text-emerald-700 mt-1">Tiền mặt shipper thu</p>
      </div>

      <!-- 4. Chi Phí Ca Trực (Chi -) -->
      <div class="bg-white p-4 rounded-3xl border border-rose-100 shadow-sm shadow-rose-500/5 hover:border-rose-300 transition-all">
        <div class="flex items-center justify-between text-rose-700 mb-1">
          <span class="text-[10px] font-extrabold uppercase tracking-wider">Chi Phí Ca (-)</span>
          <span class="text-sm">🔴</span>
        </div>
        <div class="text-base sm:text-lg font-black text-rose-600 tracking-tight">
          {{ formatCurrency(stats.totalExpenses) }}
        </div>
        <p class="text-[10px] text-rose-600 mt-1">Xăng, cơm, sửa xe</p>
      </div>

      <!-- 5. Thực Nộp Về Quỹ (= Thu - Chi) -->
      <div class="bg-gradient-to-br from-indigo-900 via-indigo-950 to-slate-900 text-white p-4 rounded-3xl shadow-md border border-indigo-800">
        <div class="flex items-center justify-between text-indigo-300 mb-1">
          <span class="text-[10px] font-black uppercase tracking-wider">Thực Nộp Quỹ (=)</span>
          <span class="text-sm">🏦</span>
        </div>
        <div class="text-base sm:text-lg font-black text-amber-300 tracking-tight">
          {{ formatCurrency(stats.netRemittance) }}
        </div>
        <p class="text-[10px] text-indigo-300 mt-1">Thực thu - Chi phí ca</p>
      </div>

      <!-- 6. Khách Nợ Tiền & Nợ Vỏ -->
      <div class="bg-white p-4 rounded-3xl border border-amber-200/80 shadow-sm hover:border-amber-400 transition-all">
        <div class="flex items-center justify-between text-amber-900 mb-1">
          <span class="text-[10px] font-extrabold uppercase tracking-wider">Khách Còn Nợ</span>
          <span class="text-sm">⚠️</span>
        </div>
        <div class="text-base sm:text-lg font-black text-rose-600 tracking-tight">
          {{ formatCurrency(stats.totalDebt) }}
        </div>
        <p class="text-[10px] text-amber-800 font-bold mt-1">
          Vỏ nợ: <strong class="text-rose-700">{{ stats.totalDebtShells }} vỏ</strong>
        </p>
      </div>
    </div>

    <!-- BỘ LỌC ĐA CHIỀU (BỘ LỌC TỪNG NHÂN VIÊN & THỜI GIAN & THU CHI) -->
    <div class="bg-white p-4.5 rounded-3xl border border-slate-200/80 shadow-sm mb-6 space-y-3.5">
      <div class="flex items-center justify-between flex-wrap gap-2">
        <div class="text-xs font-black text-slate-800 flex items-center gap-2">
          <span>🔍</span>
          <span>BỘ LỌC BÁO CÁO NHÂN VIÊN & ĐỐI SOÁT</span>
        </div>
        <div class="flex items-center gap-1.5 flex-wrap">
          <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Thời gian nhanh:</span>
          <button 
            type="button" 
            @click="setQuickDate('TODAY')"
            class="px-2.5 py-1 rounded-xl text-xs font-bold transition-all"
            :class="quickDatePreset === 'TODAY' ? 'bg-indigo-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Hôm Nay
          </button>
          <button 
            type="button" 
            @click="setQuickDate('YESTERDAY')"
            class="px-2.5 py-1 rounded-xl text-xs font-bold transition-all"
            :class="quickDatePreset === 'YESTERDAY' ? 'bg-indigo-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Hôm Qua
          </button>
          <button 
            type="button" 
            @click="setQuickDate('WEEK')"
            class="px-2.5 py-1 rounded-xl text-xs font-bold transition-all"
            :class="quickDatePreset === 'WEEK' ? 'bg-indigo-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            7 Ngày Qua
          </button>
          <button 
            type="button" 
            @click="setQuickDate('MONTH')"
            class="px-2.5 py-1 rounded-xl text-xs font-bold transition-all"
            :class="quickDatePreset === 'MONTH' ? 'bg-indigo-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Tháng Này
          </button>
          <button 
            type="button" 
            @click="setQuickDate('ALL')"
            class="px-2.5 py-1 rounded-xl text-xs font-bold transition-all"
            :class="quickDatePreset === 'ALL' ? 'bg-indigo-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Toàn Bộ
          </button>
        </div>
      </div>

      <!-- Grid các trường lọc -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
        <!-- 1. BỘ LỌC TỪNG NHÂN VIÊN GIAO HÀNG (Cốt lõi) -->
        <div>
          <label class="block text-[11px] font-black uppercase tracking-wider text-slate-600 mb-1">
            👤 Nhân Viên Giao Hàng
          </label>
          <div class="relative">
            <select 
              v-model="filters.employee"
              class="w-full px-3.5 py-2 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all cursor-pointer appearance-none"
            >
              <option value="ALL">Toàn Bộ Nhân Viên (Tất cả)</option>
              <option v-for="emp in employeeList" :key="emp.id" :value="emp.name">
                {{ emp.name }} ({{ emp.role }})
              </option>
            </select>
            <div class="absolute right-3.5 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400 text-xs">
              ▼
            </div>
          </div>
        </div>

        <!-- 2. Lọc Từ Ngày -->
        <div>
          <label class="block text-[11px] font-black uppercase tracking-wider text-slate-600 mb-1">
            📅 Từ Ngày
          </label>
          <input 
            type="date" 
            v-model="filters.startDate"
            class="w-full px-3.5 py-2 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all cursor-pointer"
          />
        </div>

        <!-- 3. Lọc Đến Ngày -->
        <div>
          <label class="block text-[11px] font-black uppercase tracking-wider text-slate-600 mb-1">
            📅 Đến Ngày
          </label>
          <input 
            type="date" 
            v-model="filters.endDate"
            class="w-full px-3.5 py-2 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all cursor-pointer"
          />
        </div>

        <!-- 4. Lọc Trạng Thái Thanh Toán -->
        <div>
          <label class="block text-[11px] font-black uppercase tracking-wider text-slate-600 mb-1">
            💵 Trạng Thái Tiền
          </label>
          <div class="relative">
            <select 
              v-model="filters.paymentStatus"
              class="w-full px-3.5 py-2 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all cursor-pointer appearance-none"
            >
              <option value="ALL">Tất Cả Trạng Thái Tiền</option>
              <option value="PAID">Đã Thu Đủ Tiền (PAID)</option>
              <option value="DEBT">Khách Còn Nợ Tiền (DEBT)</option>
            </select>
            <div class="absolute right-3.5 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400 text-xs">
              ▼
            </div>
          </div>
        </div>

        <!-- 5. Lọc Tình Trạng Trả Vỏ -->
        <div>
          <label class="block text-[11px] font-black uppercase tracking-wider text-slate-600 mb-1">
            🔄 Tình Trạng Trả Vỏ
          </label>
          <div class="relative">
            <select 
              v-model="filters.shellStatus"
              class="w-full px-3.5 py-2 text-xs font-bold text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all cursor-pointer appearance-none"
            >
              <option value="ALL">Tất Cả Tình Trạng Vỏ</option>
              <option value="RETURNED">Đã Trả Đủ Vỏ (Đủ)</option>
              <option value="OWING">Khách Còn Nợ Vỏ (Nợ)</option>
            </select>
            <div class="absolute right-3.5 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400 text-xs">
              ▼
            </div>
          </div>
        </div>
      </div>

      <!-- Ô tìm kiếm từ khóa -->
      <div class="pt-2 border-t border-slate-100 flex items-center justify-between gap-3 flex-wrap">
        <div class="relative flex-1 min-w-[260px]">
          <svg class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            type="text" 
            v-model="filters.keyword"
            placeholder="Tìm theo tên khách, mã phiếu, chủng loại bình gas hoặc ghi chú..." 
            class="w-full pl-10 pr-4 py-2 text-xs text-slate-700 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all font-medium placeholder:text-slate-400"
          />
        </div>

        <!-- Nút đặt lại bộ lọc -->
        <button 
          @click="resetFilters" 
          class="px-3 py-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-bold transition-all flex items-center gap-1"
        >
          <span>🔄 Đặt lại bộ lọc</span>
        </button>
      </div>
    </div>

    <!-- MAIN TABS: 1. GIAO GAS | 2. CHI PHÍ CA TRỰC | 3. BẢNG ĐỐI SOÁT TỪNG NHÂN SỰ -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-sm overflow-hidden mb-6">
      <!-- Tab Header Buttons -->
      <div class="px-6 py-3 border-b border-slate-200 bg-slate-50/70 flex items-center justify-between flex-wrap gap-2">
        <div class="flex items-center gap-2 flex-wrap">
          <!-- Tab 1: Phiếu Giao Gas -->
          <button 
            @click="activeTab = 'DELIVERY'"
            class="px-4 py-2 rounded-2xl text-xs font-black transition-all flex items-center gap-2"
            :class="activeTab === 'DELIVERY' 
              ? 'bg-sky-600 text-white shadow-sm' 
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/70'"
          >
            <span>🚚 Danh Sách Giao Gas ({{ filteredDeliveries.length }})</span>
          </button>

          <!-- Tab 2: Chi Phí Nhân Viên Kê Khai -->
          <button 
            @click="activeTab = 'EXPENSES'"
            class="px-4 py-2 rounded-2xl text-xs font-black transition-all flex items-center gap-2"
            :class="activeTab === 'EXPENSES' 
              ? 'bg-rose-600 text-white shadow-sm' 
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/70'"
          >
            <span>💸 Chi Phí Ca Trực ({{ filteredExpenses.length }})</span>
          </button>

          <!-- Tab 3: Bảng Đối Soát Tổng Hợp Theo Từng Nhân Sự -->
          <button 
            @click="activeTab = 'SUMMARY'"
            class="px-4 py-2 rounded-2xl text-xs font-black transition-all flex items-center gap-2"
            :class="activeTab === 'SUMMARY' 
              ? 'bg-indigo-600 text-white shadow-sm' 
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/70'"
          >
            <span>⚖️ Bảng Đối Soát Từng Nhân Sự</span>
          </button>
        </div>

        <div class="text-xs text-slate-500 font-semibold">
          Đang xem: <strong class="text-slate-900">{{ activeTab === 'DELIVERY' ? filteredDeliveries.length + ' chuyến giao' : (activeTab === 'EXPENSES' ? filteredExpenses.length + ' khoản chi' : employeeSummaryList.length + ' nhân sự') }}</strong>
        </div>
      </div>

      <!-- TAB 1: BẢNG BÁO CÁO GIAO GAS (CHI TIẾT TỪNG NHÂN VIÊN) -->
      <div v-if="activeTab === 'DELIVERY'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-100/70 text-[11px] font-black text-slate-600 uppercase tracking-wider">
              <th class="py-3.5 px-4">Ngày Giao</th>
              <th class="py-3.5 px-4">Nhân Viên Shipper</th>
              <th class="py-3.5 px-4">Khách Hàng</th>
              <th class="py-3.5 px-4">Chủng Loại Gas</th>
              <th class="py-3.5 px-4 text-center">SL</th>
              <th class="py-3.5 px-4 text-right">Đơn Giá</th>
              <th class="py-3.5 px-4 text-right">Thành Tiền</th>
              <th class="py-3.5 px-4 text-right">Thực Thu</th>
              <th class="py-3.5 px-4 text-center">Trạng Thái Tiền</th>
              <!-- CỘT TRẢ VỎ BÌNH GAS (QUẢN LÝ VỎ THAY THẾ THAO TÁC) -->
              <th class="py-3.5 px-4 text-center bg-amber-50/70 text-amber-900 border-l border-amber-200/60">
                🔄 Trả Vỏ Bình
              </th>
              <th class="py-3.5 px-4">Ghi Chú</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr v-if="filteredDeliveries.length === 0">
              <td colspan="11" class="py-14 text-center text-slate-400">
                Không tìm thấy phiếu giao gas nào phù hợp với bộ lọc hiện tại.
              </td>
            </tr>
            <tr 
              v-for="item in filteredDeliveries" 
              :key="item.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-3.5 px-4 font-bold text-slate-700 whitespace-nowrap">
                {{ item.reportDate }}
              </td>
              <td class="py-3.5 px-4 font-black text-slate-900 whitespace-nowrap">
                <span class="inline-flex items-center gap-1.5">
                  <span class="w-2 h-2 rounded-full bg-sky-500"></span>
                  {{ item.employeeName }}
                </span>
              </td>
              <td class="py-3.5 px-4 font-bold text-slate-800">
                {{ item.customerName }}
              </td>
              <td class="py-3.5 px-4 whitespace-nowrap">
                <span class="px-2.5 py-0.5 rounded-lg font-bold text-[11px]" :class="getGasBadgeClass(item.containerType)">
                  {{ item.containerType }}
                </span>
              </td>
              <td class="py-3.5 px-4 text-center font-black text-slate-900">
                {{ item.quantity }}
              </td>
              <td class="py-3.5 px-4 text-right text-slate-600 font-semibold">
                {{ formatCurrency(item.unitPrice) }}
              </td>
              <td class="py-3.5 px-4 text-right font-black text-slate-900">
                {{ formatCurrency(item.total) }}
              </td>
              <td class="py-3.5 px-4 text-right font-black text-emerald-600">
                {{ formatCurrency(item.actualReceived !== undefined ? item.actualReceived : item.total) }}
              </td>
              <td class="py-3.5 px-4 text-center whitespace-nowrap">
                <span 
                  class="inline-block px-2.5 py-1 rounded-full text-[10px] font-black"
                  :class="item.paymentStatus === 'PAID' ? 'bg-emerald-100 text-emerald-800 border border-emerald-200' : 'bg-rose-100 text-rose-800 border border-rose-200 animate-pulse'"
                >
                  {{ item.paymentStatus === 'PAID' ? '✅ ĐÃ THU' : '⚠️ CÒN NỢ' }}
                </span>
              </td>
              <!-- Cột Trả Vỏ Bình Gas -->
              <td class="py-3.5 px-4 text-center whitespace-nowrap bg-amber-50/20 border-l border-amber-100">
                <span 
                  v-if="(item.debtShells || 0) === 0" 
                  class="inline-block px-2.5 py-1 rounded-full text-[10px] font-black bg-emerald-100 text-emerald-800 border border-emerald-200"
                >
                  🟢 Đủ ({{ item.returnedShells !== undefined ? item.returnedShells : item.quantity }}/{{ item.quantity }} vỏ)
                </span>
                <span 
                  v-else 
                  class="inline-block px-2.5 py-1 rounded-full text-[10px] font-black bg-rose-100 text-rose-800 border border-rose-200 animate-pulse"
                >
                  ⚠️ Nợ {{ item.debtShells }} vỏ (Đã trả {{ item.returnedShells || 0 }}/{{ item.quantity }})
                </span>
              </td>
              <td class="py-3.5 px-4 text-slate-500 italic max-w-xs truncate">
                {{ item.notes || '-' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- TAB 2: BẢNG CHI PHÍ PHÁT SINH CỦA NHÂN VIÊN (KHOẢN CHI) -->
      <div v-else-if="activeTab === 'EXPENSES'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-100/70 text-[11px] font-black text-slate-600 uppercase tracking-wider">
              <th class="py-3.5 px-4">Ngày Chi</th>
              <th class="py-3.5 px-4">Nhân Viên Kê Khai</th>
              <th class="py-3.5 px-4">Khoản Mục Chi Tiêu</th>
              <th class="py-3.5 px-4 text-right">Số Tiền (VNĐ)</th>
              <th class="py-3.5 px-4 text-center">Trạng Thái Duyệt</th>
              <th class="py-3.5 px-4 text-center">Chứng Từ / Hóa Đơn</th>
              <th class="py-3.5 px-4">Ghi Chú Giải Trình</th>
              <th class="py-3.5 px-4 text-right">Thao Tác Admin</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr v-if="filteredExpenses.length === 0">
              <td colspan="8" class="py-14 text-center text-slate-400">
                Không có khoản chi phí phát sinh nào phù hợp với bộ lọc đã chọn.
              </td>
            </tr>
            <tr 
              v-for="exp in filteredExpenses" 
              :key="exp.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-3.5 px-4 font-bold text-slate-700 whitespace-nowrap">
                {{ exp.expenseDate }}
              </td>
              <td class="py-3.5 px-4 font-black text-slate-900 whitespace-nowrap">
                <span class="inline-flex items-center gap-1.5">
                  <span class="w-2 h-2 rounded-full bg-rose-500"></span>
                  {{ exp.employeeName }}
                </span>
              </td>
              <td class="py-3.5 px-4 font-bold text-slate-800">
                <span class="inline-flex items-center gap-1.5">
                  <span>{{ getExpenseIcon(exp.description) }}</span>
                  <span>{{ exp.description }}</span>
                </span>
              </td>
              <td class="py-3.5 px-4 text-right font-black text-rose-600 text-sm">
                - {{ formatCurrency(exp.amount) }}
              </td>
              <td class="py-3.5 px-4 text-center whitespace-nowrap">
                <span 
                  class="inline-block px-2.5 py-0.5 rounded-full text-[10px] font-black"
                  :class="exp.status === 'APPROVED' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'"
                >
                  {{ exp.status === 'APPROVED' ? '✅ ĐÃ DUYỆT' : '⏳ CHỜ DUYỆT' }}
                </span>
              </td>
              <td class="py-3.5 px-4 text-center">
                <span v-if="exp.receiptUrl" class="text-sky-600 hover:underline font-bold cursor-pointer">
                  📄 Xem hóa đơn
                </span>
                <span v-else class="text-slate-400">Không có</span>
              </td>
              <td class="py-3.5 px-4 text-slate-500 italic max-w-sm truncate">
                {{ exp.notes || 'Chi tiền mặt ca trực' }}
              </td>
              <td class="py-3.5 px-4 text-right">
                <button 
                  v-if="exp.status !== 'APPROVED'"
                  @click="approveExpense(exp)"
                  class="px-2.5 py-1 rounded-xl bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white text-[10px] font-black shadow-xs transition-all"
                  title="Duyệt chi khoản này"
                >
                  ✓ Duyệt Chi
                </button>
                <span v-else class="text-[11px] text-slate-400 font-semibold">Đã duyệt</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- TAB 3: BẢNG TỔNG HỢP ĐỐI SOÁT TỪNG NHÂN SỰ (THỰC NỘP = THỰC THU - CHI PHÍ) -->
      <div v-else-if="activeTab === 'SUMMARY'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-100/70 text-[11px] font-black text-slate-600 uppercase tracking-wider">
              <th class="py-3.5 px-4">Nhân Viên Giao Hàng</th>
              <th class="py-3.5 px-4 text-center">Tổng Đơn</th>
              <th class="py-3.5 px-4 text-center">SL Bình Giao</th>
              <th class="py-3.5 px-4 text-right">Tổng Doanh Số</th>
              <th class="py-3.5 px-4 text-right text-emerald-700 bg-emerald-50/50">Tiền Thực Thu (+)</th>
              <th class="py-3.5 px-4 text-right text-rose-700 bg-rose-50/50">Chi Phí Ca (-)</th>
              <th class="py-3.5 px-4 text-right text-rose-600">Khách Nợ Tiền</th>
              <th class="py-3.5 px-4 text-center text-amber-900 bg-amber-50/50">Khách Nợ Vỏ</th>
              <th class="py-3.5 px-4 text-right font-black text-slate-900 bg-indigo-50/70 border-l border-indigo-100">
                Thực Nộp Quỹ (=)
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr 
              v-for="row in employeeSummaryList" 
              :key="row.employeeName"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <!-- Tên nhân viên & vai trò -->
              <td class="py-3.5 px-4 font-black text-slate-900">
                <div class="flex items-center gap-2">
                  <div class="w-7 h-7 rounded-full bg-slate-900 text-white flex items-center justify-center text-[10px] font-bold">
                    {{ getInitials(row.employeeName) }}
                  </div>
                  <div>
                    <div>{{ row.employeeName }}</div>
                    <div class="text-[10px] text-slate-400 font-normal">{{ row.role || 'Shipper OriginGas' }}</div>
                  </div>
                </div>
              </td>

              <!-- Tổng đơn -->
              <td class="py-3.5 px-4 text-center font-bold text-slate-600">
                {{ row.orderCount }} đơn
              </td>

              <!-- SL bình -->
              <td class="py-3.5 px-4 text-center font-black text-slate-900">
                {{ row.totalQuantity }} bình
              </td>

              <!-- Tổng doanh số -->
              <td class="py-3.5 px-4 text-right font-bold text-slate-800">
                {{ formatCurrency(row.revenue) }}
              </td>

              <!-- Tiền thực thu -->
              <td class="py-3.5 px-4 text-right font-black text-emerald-700 bg-emerald-50/20">
                {{ formatCurrency(row.received) }}
              </td>

              <!-- Chi phí ca -->
              <td class="py-3.5 px-4 text-right font-bold text-rose-600 bg-rose-50/20">
                - {{ formatCurrency(row.expenses) }}
              </td>

              <!-- Khách nợ tiền -->
              <td class="py-3.5 px-4 text-right font-bold text-rose-600">
                {{ formatCurrency(row.debt) }}
              </td>

              <!-- Khách nợ vỏ -->
              <td class="py-3.5 px-4 text-center bg-amber-50/20">
                <span 
                  class="inline-block px-2 py-0.5 rounded-full text-[10px] font-black"
                  :class="row.debtShells > 0 ? 'bg-rose-100 text-rose-800 animate-pulse' : 'bg-emerald-100 text-emerald-800'"
                >
                  {{ row.debtShells > 0 ? 'Nợ ' + row.debtShells + ' vỏ' : 'Đủ vỏ' }}
                </span>
              </td>

              <!-- THỰC NỘP VỀ QUỸ (THỰC THU - CHI PHÍ) -->
              <td class="py-3.5 px-4 text-right font-black text-indigo-900 bg-indigo-50/50 text-sm border-l border-indigo-100">
                {{ formatCurrency(row.received - row.expenses) }}
              </td>
            </tr>
          </tbody>

          <!-- HÀNG TỔNG CỘNG TOÀN ĐỘI NGŨ -->
          <tfoot>
            <tr class="bg-slate-100 font-black text-slate-900 text-xs border-t-2 border-slate-300">
              <td class="py-4 px-4 uppercase tracking-wider">
                TỔNG CỘNG TOÀN BỘ ({{ employeeSummaryList.length }} NV):
              </td>
              <td class="py-4 px-4 text-center">
                {{ employeeSummaryList.reduce((s, r) => s + r.orderCount, 0) }} đơn
              </td>
              <td class="py-4 px-4 text-center text-sm">
                {{ stats.totalQuantity }} bình
              </td>
              <td class="py-4 px-4 text-right">
                {{ formatCurrency(stats.totalRevenue) }}
              </td>
              <td class="py-4 px-4 text-right text-emerald-700 bg-emerald-100/40">
                {{ formatCurrency(stats.actualReceived) }}
              </td>
              <td class="py-4 px-4 text-right text-rose-700 bg-rose-100/40">
                - {{ formatCurrency(stats.totalExpenses) }}
              </td>
              <td class="py-4 px-4 text-right text-rose-600">
                {{ formatCurrency(stats.totalDebt) }}
              </td>
              <td class="py-4 px-4 text-center text-amber-900 bg-amber-100/40">
                {{ stats.totalDebtShells }} vỏ
              </td>
              <td class="py-4 px-4 text-right text-base text-indigo-950 bg-indigo-100/60 border-l border-indigo-200">
                {{ formatCurrency(stats.netRemittance) }}
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>

    <!-- MODAL KÊ KHAI CHI PHÍ CA TRỰC MỚI -->
    <div 
      v-if="expenseModalOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/60 backdrop-blur-sm transition-opacity"
      @click.self="expenseModalOpen = false"
    >
      <div class="bg-white w-full max-w-md rounded-3xl shadow-2xl border border-slate-200 overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-gradient-to-r from-rose-50 to-amber-50">
          <div class="flex items-center gap-2.5">
            <span class="text-xl">💸</span>
            <h3 class="text-sm font-black text-slate-900">Kê Khai Chi Phí Ca Trực Mới</h3>
          </div>
          <button @click="expenseModalOpen = false" class="p-1.5 text-slate-400 hover:text-slate-700 rounded-xl">✕</button>
        </div>

        <form @submit.prevent="handleSaveExpense" class="p-6 space-y-3.5 text-xs font-bold">
          <div>
            <label class="block text-slate-700 mb-1">Nhân Viên Kê Khai</label>
            <select v-model="expenseForm.employeeName" required class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl">
              <option v-for="emp in employeeList" :key="emp.id" :value="emp.name">{{ emp.name }} ({{ emp.role }})</option>
            </select>
          </div>

          <div>
            <label class="block text-slate-700 mb-1">Khoản Mục Chi Tiêu</label>
            <input 
              type="text" 
              v-model="expenseForm.description" 
              placeholder="VD: Đổ xăng xe máy, Cơm trưa, Vá lốp xe..." 
              required
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl"
            />
          </div>

          <div>
            <label class="block text-slate-700 mb-1">Số Tiền (VNĐ)</label>
            <input 
              type="number" 
              step="5000" 
              min="1000" 
              v-model.number="expenseForm.amount" 
              required
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl font-black text-rose-600"
            />
          </div>

          <div>
            <label class="block text-slate-700 mb-1">Ghi Chú Giải Trình</label>
            <textarea v-model="expenseForm.notes" rows="2" placeholder="Ghi chú thêm về lý do chi..." class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl font-normal"></textarea>
          </div>

          <div class="pt-2 flex items-center justify-end gap-2">
            <button type="button" @click="expenseModalOpen = false" class="px-4 py-2 bg-slate-100 rounded-xl text-slate-600">Hủy</button>
            <button type="submit" class="px-5 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-black shadow-sm">Lưu Khoản Chi</button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'

// Danh sách nhân viên shipper OriginGas
const employeeList = [
  { id: 'emp-1', name: 'Nguyễn Văn Giao', role: 'Shipper 01' },
  { id: 'emp-2', name: 'Trần Văn Hưng', role: 'Shipper 02' },
  { id: 'emp-3', name: 'Lê Hoàng Nam', role: 'Shipper 03' }
]

// Tab đang chọn: 'DELIVERY', 'EXPENSES', 'SUMMARY'
const activeTab = ref('DELIVERY')
const quickDatePreset = ref('ALL')

// Bộ lọc nâng cao
const filters = reactive({
  employee: 'ALL',
  startDate: '',
  endDate: '',
  paymentStatus: 'ALL',
  shellStatus: 'ALL',
  keyword: ''
})

// Dữ liệu mẫu Báo cáo giao gas của từng nhân viên
const rawDeliveries = ref([
  {
    id: 'rep-101',
    reportDate: '2026-11-21',
    employeeName: 'Nguyễn Văn Giao',
    customerName: 'Quán Cơm Tấm 24',
    containerType: 'Bình 12kg Xám',
    quantity: 2,
    returnedShells: 2,
    debtShells: 0,
    unitPrice: 350000,
    total: 700000,
    actualReceived: 700000,
    paymentStatus: 'PAID',
    notes: 'Giao trực tiếp tầng trệt'
  },
  {
    id: 'rep-102',
    reportDate: '2026-11-21',
    employeeName: 'Nguyễn Văn Giao',
    customerName: 'Bún Bò Huế 36',
    containerType: 'Bình 45kg Công Nghiệp',
    quantity: 1,
    returnedShells: 0,
    debtShells: 1,
    unitPrice: 1250000,
    total: 1250000,
    actualReceived: 0,
    paymentStatus: 'DEBT',
    notes: 'Hẹn 18h quay lại lấy tiền, nợ 1 vỏ'
  },
  {
    id: 'rep-103',
    reportDate: '2026-11-20',
    employeeName: 'Trần Văn Hưng',
    customerName: 'Khách sạn Á Châu',
    containerType: 'Bình 45kg Công Nghiệp',
    quantity: 3,
    returnedShells: 3,
    debtShells: 0,
    unitPrice: 1250000,
    total: 3750000,
    actualReceived: 3750000,
    paymentStatus: 'PAID',
    notes: 'Chuyển khoản QR kho'
  },
  {
    id: 'rep-104',
    reportDate: '2026-11-19',
    employeeName: 'Lê Hoàng Nam',
    customerName: 'Chị Mai - Chung cư Flora',
    containerType: 'Bình 12kg Đỏ',
    quantity: 1,
    returnedShells: 1,
    debtShells: 0,
    unitPrice: 360000,
    total: 360000,
    actualReceived: 360000,
    paymentStatus: 'PAID',
    notes: 'Khách quen đổi vỏ cũ'
  },
  {
    id: 'rep-105',
    reportDate: '2026-11-18',
    employeeName: 'Trần Văn Hưng',
    customerName: 'Nhà hàng Lẩu Nướng Gió Biển',
    containerType: 'Gas Lon Mini',
    quantity: 24,
    returnedShells: 24,
    debtShells: 0,
    unitPrice: 15000,
    total: 360000,
    actualReceived: 360000,
    paymentStatus: 'PAID',
    notes: 'Giao 1 thùng lon gas'
  },
  {
    id: 'rep-106',
    reportDate: '2026-11-21',
    employeeName: 'Lê Hoàng Nam',
    customerName: 'Quán Phở Thìn 88',
    containerType: 'Bình 12kg Xám',
    quantity: 2,
    returnedShells: 1,
    debtShells: 1,
    unitPrice: 350000,
    total: 700000,
    actualReceived: 500000,
    paymentStatus: 'DEBT',
    notes: 'Khách trả trước 500k, nợ lại 200k và 1 vỏ bình'
  }
])

// Dữ liệu mẫu Chi phí phát sinh của nhân viên (Expenses)
const rawExpenses = ref([
  {
    id: 'exp-201',
    expenseDate: '2026-11-21',
    employeeName: 'Nguyễn Văn Giao',
    description: 'Xăng Xe Máy Giao Hàng',
    amount: 70000,
    status: 'APPROVED',
    notes: 'Đổ xăng Petrolimex ca sáng',
    receiptUrl: 'https://example.com/receipt1.jpg'
  },
  {
    id: 'exp-202',
    expenseDate: '2026-11-21',
    employeeName: 'Nguyễn Văn Giao',
    description: 'Cơm Trưa Ca Trực',
    amount: 35000,
    status: 'APPROVED',
    notes: 'Cơm trưa theo chế độ hỗ trợ',
    receiptUrl: ''
  },
  {
    id: 'exp-203',
    expenseDate: '2026-11-20',
    employeeName: 'Trần Văn Hưng',
    description: 'Vá Lốp Xe & Bơm Hơi',
    amount: 30000,
    status: 'APPROVED',
    notes: 'Xe bị cán đinh đường Nguyễn Văn Cừ',
    receiptUrl: ''
  },
  {
    id: 'exp-204',
    expenseDate: '2026-11-19',
    employeeName: 'Lê Hoàng Nam',
    description: 'Xăng Xe Máy Giao Hàng',
    amount: 60000,
    status: 'APPROVED',
    notes: 'Đổ xăng ca chiều',
    receiptUrl: ''
  },
  {
    id: 'exp-205',
    expenseDate: '2026-11-21',
    employeeName: 'Lê Hoàng Nam',
    description: 'Phí Gửi Xe Bến Xe Miền Tây',
    amount: 20000,
    status: 'PENDING',
    notes: 'Giao gas khu vực bến xe',
    receiptUrl: ''
  }
])

// Modal thêm chi phí
const expenseModalOpen = ref(false)
const expenseForm = reactive({
  employeeName: 'Nguyễn Văn Giao',
  description: '',
  amount: 50000,
  notes: ''
})

const openExpenseModal = () => {
  expenseForm.description = ''
  expenseForm.amount = 50000
  expenseForm.notes = ''
  expenseModalOpen.value = true
}

const handleSaveExpense = () => {
  const newExp = {
    id: 'exp-' + Date.now(),
    expenseDate: new Date().toISOString().split('T')[0],
    employeeName: expenseForm.employeeName,
    description: expenseForm.description,
    amount: Number(expenseForm.amount) || 0,
    status: 'APPROVED',
    notes: expenseForm.notes,
    receiptUrl: ''
  }
  rawExpenses.value.unshift(newExp)
  expenseModalOpen.value = false
}

const approveExpense = (exp) => {
  exp.status = 'APPROVED'
}

// Lọc danh sách giao gas
const filteredDeliveries = computed(() => {
  return rawDeliveries.value.filter(item => {
    // 1. Employee filter
    if (filters.employee !== 'ALL' && item.employeeName !== filters.employee) {
      return false
    }

    // 2. Date range filter
    if (filters.startDate && item.reportDate < filters.startDate) return false
    if (filters.endDate && item.reportDate > filters.endDate) return false

    // 3. Payment status filter
    if (filters.paymentStatus !== 'ALL' && item.paymentStatus !== filters.paymentStatus) {
      return false
    }

    // 4. Shell status filter
    if (filters.shellStatus === 'RETURNED' && (item.debtShells || 0) > 0) return false
    if (filters.shellStatus === 'OWING' && (item.debtShells || 0) === 0) return false

    // 5. Keyword search
    if (filters.keyword.trim()) {
      const kw = filters.keyword.toLowerCase().trim()
      const matchCust = item.customerName.toLowerCase().includes(kw)
      const matchEmp = item.employeeName.toLowerCase().includes(kw)
      const matchGas = item.containerType.toLowerCase().includes(kw)
      const matchNote = item.notes && item.notes.toLowerCase().includes(kw)
      if (!matchCust && !matchEmp && !matchGas && !matchNote) return false
    }

    return true
  })
})

// Lọc danh sách chi phí
const filteredExpenses = computed(() => {
  return rawExpenses.value.filter(exp => {
    // 1. Employee filter
    if (filters.employee !== 'ALL' && exp.employeeName !== filters.employee) {
      return false
    }

    // 2. Date range filter
    if (filters.startDate && exp.expenseDate < filters.startDate) return false
    if (filters.endDate && exp.expenseDate > filters.endDate) return false

    // 3. Keyword search
    if (filters.keyword.trim()) {
      const kw = filters.keyword.toLowerCase().trim()
      const matchDesc = exp.description.toLowerCase().includes(kw)
      const matchEmp = exp.employeeName.toLowerCase().includes(kw)
      const matchNote = exp.notes && exp.notes.toLowerCase().includes(kw)
      if (!matchDesc && !matchEmp && !matchNote) return false
    }

    return true
  })
})

// Thống kê đối soát tổng hợp (KPI)
const stats = computed(() => {
  const totalQuantity = filteredDeliveries.value.reduce((sum, d) => sum + (Number(d.quantity) || 0), 0)
  const totalRevenue = filteredDeliveries.value.reduce((sum, d) => sum + (Number(d.total) || 0), 0)
  const actualReceived = filteredDeliveries.value.reduce((sum, d) => sum + (Number(d.actualReceived !== undefined ? d.actualReceived : d.total) || 0), 0)
  const totalExpenses = filteredExpenses.value.reduce((sum, e) => sum + (Number(e.amount) || 0), 0)
  const totalDebt = Math.max(0, totalRevenue - actualReceived)
  const totalDebtShells = filteredDeliveries.value.reduce((sum, d) => sum + (Number(d.debtShells) || 0), 0)
  const netRemittance = Math.max(0, actualReceived - totalExpenses)

  return { totalQuantity, totalRevenue, actualReceived, totalExpenses, totalDebt, totalDebtShells, netRemittance }
})

// Bảng gom nhóm tổng hợp theo từng nhân sự (Summary Group By Employee)
const employeeSummaryList = computed(() => {
  const map = {}

  employeeList.forEach(emp => {
    map[emp.name] = {
      employeeName: emp.name,
      role: emp.role,
      orderCount: 0,
      totalQuantity: 0,
      revenue: 0,
      received: 0,
      expenses: 0,
      debt: 0,
      debtShells: 0
    }
  })

  // Cộng dồn giao gas
  filteredDeliveries.value.forEach(d => {
    if (!map[d.employeeName]) {
      map[d.employeeName] = {
        employeeName: d.employeeName,
        role: 'Shipper',
        orderCount: 0,
        totalQuantity: 0,
        revenue: 0,
        received: 0,
        expenses: 0,
        debt: 0,
        debtShells: 0
      }
    }
    const row = map[d.employeeName]
    row.orderCount += 1
    row.totalQuantity += d.quantity
    row.revenue += d.total
    const recv = d.actualReceived !== undefined ? d.actualReceived : d.total
    row.received += recv
    row.debt += Math.max(0, d.total - recv)
    row.debtShells += (Number(d.debtShells) || 0)
  })

  // Cộng dồn chi phí
  filteredExpenses.value.forEach(e => {
    if (map[e.employeeName]) {
      map[e.employeeName].expenses += e.amount
    }
  })

  // Nếu người dùng chọn lọc 1 nhân viên cụ thể, chỉ hiển thị nhân viên đó
  if (filters.employee !== 'ALL') {
    return Object.values(map).filter(r => r.employeeName === filters.employee)
  }

  return Object.values(map)
})

// Reset filters
const resetFilters = () => {
  filters.employee = 'ALL'
  filters.startDate = ''
  filters.endDate = ''
  filters.paymentStatus = 'ALL'
  filters.shellStatus = 'ALL'
  filters.keyword = ''
  quickDatePreset.value = 'ALL'
}

// Quick date preset
const setQuickDate = (preset) => {
  quickDatePreset.value = preset
  const today = new Date().toISOString().split('T')[0]

  switch (preset) {
    case 'TODAY':
      filters.startDate = today
      filters.endDate = today
      break
    case 'YESTERDAY': {
      const y = new Date()
      y.setDate(y.getDate() - 1)
      const yStr = y.toISOString().split('T')[0]
      filters.startDate = yStr
      filters.endDate = yStr
      break
    }
    case 'WEEK': {
      const w = new Date()
      w.setDate(w.getDate() - 7)
      filters.startDate = w.toISOString().split('T')[0]
      filters.endDate = today
      break
    }
    case 'MONTH': {
      const now = new Date()
      const mStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`
      filters.startDate = mStr
      filters.endDate = today
      break
    }
    case 'ALL':
      filters.startDate = ''
      filters.endDate = ''
      break
  }
}

// Helpers
const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

const getInitials = (name) => {
  if (!name) return 'NV'
  const words = name.trim().split(' ')
  if (words.length === 1) return words[0].substring(0, 2).toUpperCase()
  return (words[0][0] + words[words.length - 1][0]).toUpperCase()
}

const getGasBadgeClass = (type) => {
  if (type.includes('45kg')) return 'bg-amber-100 text-amber-900 border border-amber-200'
  if (type.includes('Đỏ')) return 'bg-rose-100 text-rose-900 border border-rose-200'
  if (type.includes('Mini')) return 'bg-purple-100 text-purple-900 border border-purple-200'
  return 'bg-slate-200 text-slate-800 border border-slate-300'
}

const getExpenseIcon = (desc) => {
  if (desc.includes('Xăng')) return '⛽'
  if (desc.includes('Cơm')) return '🍱'
  if (desc.includes('Lốp') || desc.includes('Sửa')) return '🔧'
  if (desc.includes('Gửi xe') || desc.includes('Cầu') || desc.includes('Bến')) return '🅿️'
  return '💸'
}

const handlePrintReport = () => {
  window.print()
}

const handleExportCSV = () => {
  alert('Đang kết xuất bảng dữ liệu báo cáo giao gas và chi phí ra file Excel/CSV!')
}
</script>
