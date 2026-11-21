<template>
  <EmployeeLayout :is-backend-connected="!isMockData" :debt-count="debtBillsCount" @refresh="fetchReports">
    <!-- Top Hero Section -->
    <div class="relative overflow-hidden rounded-3xl bg-gradient-to-r from-slate-900 via-sky-950 to-slate-900 text-white p-5 sm:p-7 mb-6 shadow-xl shadow-slate-950/10 border border-slate-800">
      <!-- Decorative Lighting Glows -->
      <div class="absolute -right-10 -top-10 w-60 h-60 bg-sky-500/20 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute -left-10 -bottom-10 w-60 h-60 bg-teal-500/15 rounded-full blur-3xl pointer-events-none"></div>

      <div class="relative z-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-sky-500/20 border border-sky-400/30 text-sky-300 text-xs font-bold mb-2 backdrop-blur-md">
            <span class="w-2 h-2 rounded-full bg-sky-400 animate-ping"></span>
            <span>Cổng Giao Hàng & Báo Cáo Doanh Thu Ca Trực</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white flex items-center gap-3">
            <span>Báo Cáo Giao Gas Hôm Nay</span>
            <span class="text-xl sm:text-2xl">🚚</span>
          </h2>
          <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-xl leading-relaxed">
            Lập phiếu giao gas tức thì, tự động kiểm tra trừ tồn kho, đối soát tiền mặt và ghi nhận nợ khách hàng.
          </p>
        </div>

        <div class="flex items-center gap-2.5 flex-wrap">
          <!-- Date Selector -->
          <div class="relative">
            <input 
              type="date" 
              v-model="selectedDate" 
              @change="fetchReports"
              class="px-3.5 py-2.5 text-xs font-extrabold text-white bg-white/10 hover:bg-white/15 border border-white/10 rounded-2xl shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-400/30 transition-all cursor-pointer backdrop-blur-md"
            />
          </div>

          <!-- Main CTA Button -->
          <button 
            @click="openCreateModal"
            class="px-5 py-2.5 rounded-2xl bg-gradient-to-r from-sky-500 via-cyan-500 to-sky-600 hover:from-sky-400 hover:to-cyan-500 active:scale-95 text-white text-xs font-black shadow-lg shadow-sky-500/30 transition-all flex items-center gap-2"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            <span>Nhập Phiếu Giao Gas</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Alert Banner when Backend is Offline (Mock Mode) -->
    <div 
      v-if="isMockData" 
      class="mb-6 p-4 bg-gradient-to-r from-amber-50/95 via-orange-50/90 to-amber-50/95 border border-amber-200/90 rounded-3xl shadow-sm flex items-center justify-between text-xs text-amber-950"
    >
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-xl bg-amber-500/15 text-amber-700 flex items-center justify-center text-base flex-shrink-0">
          ⚡
        </div>
        <div>
          <div class="font-bold text-amber-900">Chế độ trải nghiệm ngoại tuyến (Mock Data)</div>
          <div class="text-[11px] text-amber-700 mt-0.5">
            Bạn có thể thử tạo đơn, đổi tiền thu và kiểm tra trừ kho. Khi Backend khởi động, ứng dụng sẽ tự chuyển sang API thực!
          </div>
        </div>
      </div>
      <button 
        @click="fetchReports" 
        class="px-3 py-1.5 rounded-xl bg-white text-amber-800 hover:bg-amber-100 font-bold border border-amber-200 shadow-xs transition-all flex-shrink-0 ml-3"
      >
        Đồng bộ lại
      </button>
    </div>

    <!-- 5 Summary Metric KPI Cards (Bổ sung thống kê Nợ Vỏ Bình Gas) -->
    <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3.5 mb-6">
      <!-- 1. Total Cylinders Delivered -->
      <div class="relative overflow-hidden bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm group hover:border-sky-300 transition-all">
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-sky-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-slate-500">Bình Đã Giao</span>
          <div class="w-8 h-8 rounded-xl bg-sky-50 text-sky-600 flex items-center justify-center text-sm border border-sky-100">
            🛢️
          </div>
        </div>
        <div class="text-xl sm:text-2xl font-black text-slate-900 tracking-tight flex items-baseline gap-1.5">
          <span>{{ stats.totalQuantity }}</span>
          <span class="text-xs font-bold text-slate-400">bình</span>
        </div>
        <div class="mt-2 text-[11px] text-sky-600 font-semibold flex items-center gap-1">
          <span>Sản lượng xuất giao hôm nay</span>
        </div>
      </div>

      <!-- 2. Total Turnover -->
      <div class="relative overflow-hidden bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm group hover:border-indigo-300 transition-all">
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-indigo-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-slate-500">Tổng Doanh Thu</span>
          <div class="w-8 h-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center text-sm border border-indigo-100">
            💵
          </div>
        </div>
        <div class="text-lg sm:text-xl font-black text-slate-900 tracking-tight">
          {{ formatCurrency(stats.totalAmount) }}
        </div>
        <div class="mt-2 text-[11px] text-indigo-600 font-semibold flex items-center gap-1">
          <span>Tổng giá trị các phiếu giao</span>
        </div>
      </div>

      <!-- 3. Paid Amount -->
      <div class="relative overflow-hidden bg-white p-4 rounded-3xl border border-emerald-100 shadow-sm shadow-emerald-500/5 group hover:border-emerald-300 transition-all">
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-emerald-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-emerald-800">Thực Thu Tiền</span>
          <div class="w-8 h-8 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-sm border border-emerald-100">
            ✅
          </div>
        </div>
        <div class="text-lg sm:text-xl font-black text-emerald-600 tracking-tight">
          {{ formatCurrency(stats.paidAmount) }}
        </div>
        <div class="mt-2 text-[11px] text-emerald-600 font-semibold flex items-center gap-1">
          <span>Tiền mặt shipper đã thu</span>
        </div>
      </div>

      <!-- 4. Debt Amount -->
      <div 
        class="relative overflow-hidden bg-white p-4 rounded-3xl border shadow-sm transition-all group"
        :class="stats.debtAmount > 0 ? 'border-rose-300 bg-rose-50/20 shadow-rose-500/5' : 'border-slate-200/80'"
      >
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-rose-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-rose-800">Khách Còn Nợ</span>
          <div class="w-8 h-8 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center text-sm border border-rose-100">
            ⚠️
          </div>
        </div>
        <div class="text-lg sm:text-xl font-black tracking-tight" :class="stats.debtAmount > 0 ? 'text-rose-600' : 'text-slate-900'">
          {{ formatCurrency(stats.debtAmount) }}
        </div>
        <div class="mt-2 text-[11px] font-semibold flex items-center gap-1" :class="stats.debtAmount > 0 ? 'text-rose-600' : 'text-slate-400'">
          <span>{{ debtBillsCount }} đơn chưa thanh toán đủ</span>
        </div>
      </div>

      <!-- 5. Debt Shells (Vỏ Bình Khách Còn Nợ - Cần Hoàn Trả Sau) -->
      <div 
        class="col-span-2 sm:col-span-1 relative overflow-hidden bg-white p-4 rounded-3xl border shadow-sm transition-all group"
        :class="stats.debtShells > 0 ? 'border-amber-300 bg-amber-50/25 shadow-amber-500/5' : 'border-slate-200/80'"
      >
        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-amber-500/15 rounded-full blur-xl group-hover:scale-125 transition-transform"></div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-[11px] font-extrabold uppercase tracking-wider text-amber-900">Vỏ Khách Còn Nợ</span>
          <div class="w-8 h-8 rounded-xl bg-amber-50 text-amber-700 flex items-center justify-center text-sm border border-amber-200">
            🔄
          </div>
        </div>
        <div class="text-xl sm:text-2xl font-black tracking-tight flex items-baseline gap-1.5" :class="stats.debtShells > 0 ? 'text-amber-800' : 'text-slate-900'">
          <span>{{ stats.debtShells }}</span>
          <span class="text-xs font-bold text-slate-400">vỏ</span>
        </div>
        <div class="mt-2 text-[11px] font-semibold flex items-center gap-1" :class="stats.debtShells > 0 ? 'text-amber-800' : 'text-slate-400'">
          <span>{{ stats.debtShells > 0 ? 'Khách trả thiếu, cần thu sau' : 'Khách đã trả đủ 100% vỏ' }}</span>
        </div>
      </div>
    </div>

    <!-- Filter & Search Toolbar (Bổ sung bộ lọc Trả Vỏ & chuyển đổi Bảng / Thẻ) -->
    <div class="bg-white p-3.5 rounded-3xl border border-slate-200/80 shadow-sm mb-6 flex flex-col md:flex-row gap-3 items-stretch md:items-center justify-between">
      <!-- Search Input -->
      <div class="relative flex-1">
        <svg class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="Tìm theo tên khách, loại gas, ghi chú..." 
          class="w-full pl-10 pr-4 py-2 text-xs text-slate-700 bg-slate-50 border border-slate-200/80 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all placeholder:text-slate-400 font-medium"
        />
      </div>

      <!-- Segmented Status Tabs & View Mode Switcher -->
      <div class="flex items-center gap-2 flex-wrap justify-between md:justify-end">
        <div class="flex items-center gap-1 p-1 bg-slate-100/80 rounded-2xl border border-slate-200/50 text-xs">
          <button 
            @click="filterStatus = ''" 
            class="px-2.5 py-1.5 font-bold rounded-xl transition-all"
            :class="filterStatus === '' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-600 hover:text-slate-900'"
          >
            Tất Cả ({{ reports.length }})
          </button>
          <button 
            @click="filterStatus = 'PAID'" 
            class="px-2.5 py-1.5 font-bold rounded-xl transition-all flex items-center gap-1"
            :class="filterStatus === 'PAID' ? 'bg-emerald-500 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'"
          >
            <span>Đã Thu Tiền</span>
          </button>
          <button 
            @click="filterStatus = 'DEBT'" 
            class="px-2.5 py-1.5 font-bold rounded-xl transition-all flex items-center gap-1"
            :class="filterStatus === 'DEBT' ? 'bg-rose-500 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'"
          >
            <span>Nợ Tiền ({{ debtBillsCount }})</span>
          </button>
          <button 
            @click="filterStatus = 'SHELL_DEBT'" 
            class="px-2.5 py-1.5 font-bold rounded-xl transition-all flex items-center gap-1"
            :class="filterStatus === 'SHELL_DEBT' ? 'bg-amber-500 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'"
          >
            <span>Nợ Vỏ ({{ debtShellsCount }})</span>
          </button>
        </div>

        <!-- Chuyển đổi giao diện Thẻ / Bảng -->
        <div class="flex items-center p-1 bg-slate-100/80 rounded-2xl border border-slate-200/50">
          <button 
            @click="viewMode = 'CARDS'" 
            class="p-1.5 rounded-xl transition-all"
            :class="viewMode === 'CARDS' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-400 hover:text-slate-700'"
            title="Xem dạng thẻ trực quan"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
            </svg>
          </button>
          <button 
            @click="viewMode = 'TABLE'" 
            class="p-1.5 rounded-xl transition-all"
            :class="viewMode === 'TABLE' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-400 hover:text-slate-700'"
            title="Xem dạng bảng chi tiết"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="py-20 text-center text-slate-400">
      <div class="w-9 h-9 border-3 border-sky-500/30 border-t-sky-600 rounded-full animate-spin mx-auto mb-3"></div>
      <p class="text-xs font-bold text-slate-600">Đang đồng bộ danh sách đơn giao...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredReports.length === 0" class="bg-white rounded-3xl border border-dashed border-slate-200 p-12 text-center">
      <div class="w-16 h-16 rounded-3xl bg-slate-100 text-slate-400 flex items-center justify-center text-3xl mx-auto mb-4">
        🚚
      </div>
      <h3 class="text-base font-black text-slate-900">Chưa có phiếu giao gas nào hôm nay</h3>
      <p class="text-xs text-slate-500 mt-1 max-w-sm mx-auto leading-relaxed">
        Nhân viên bắt đầu ca làm việc bấm nút bên dưới để tạo phiếu giao gas cho khách đầu tiên.
      </p>
      <button 
        @click="openCreateModal"
        class="mt-5 inline-flex items-center gap-2 px-6 py-2.5 bg-gradient-to-r from-sky-500 to-cyan-500 hover:from-sky-600 hover:to-cyan-600 active:scale-95 text-white text-xs font-black rounded-2xl shadow-md shadow-sky-500/20 transition-all"
      >
        <span>➕ Nhập Phiếu Đầu Tiên</span>
      </button>
    </div>

    <!-- Delivery Reports Section (Hỗ trợ 2 Chế Độ: THẺ hoặc BẢNG) -->
    <!-- 1. CHẾ ĐỘ BẢNG DỮ LIỆU (Đã xóa cột Thao Tác, thay bằng cột Trả Vỏ) -->
    <div v-else-if="viewMode === 'TABLE'" class="bg-white rounded-3xl border border-slate-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-100/70 text-[11px] font-black text-slate-600 uppercase tracking-wider">
              <th class="py-3.5 px-4">Khách Hàng</th>
              <th class="py-3.5 px-4">Chủng Loại Bình Gas</th>
              <th class="py-3.5 px-4 text-center">SL Giao</th>
              <th class="py-3.5 px-4 text-right">Đơn Giá</th>
              <th class="py-3.5 px-4 text-right">Tổng Tiền</th>
              <th class="py-3.5 px-4 text-right">Thực Thu</th>
              <th class="py-3.5 px-4 text-center">Thanh Toán</th>
              <!-- CỘT TRẢ VỎ THAY THẾ HOÀN TOÀN CỘT THAO TÁC CŨ -->
              <th class="py-3.5 px-4 text-center bg-amber-50/60 text-amber-900 border-l border-amber-200/60">
                🔄 Tình Trạng Trả Vỏ
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr 
              v-for="item in filteredReports" 
              :key="item.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <!-- Customer Name & Notes -->
              <td class="py-3.5 px-4 font-bold text-slate-900">
                <div>{{ item.customerName }}</div>
                <div v-if="item.notes" class="text-[10px] text-slate-400 font-normal italic truncate max-w-xs">
                  "{{ item.notes }}"
                </div>
              </td>

              <!-- Container Type -->
              <td class="py-3.5 px-4">
                <span 
                  class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-lg text-[11px] font-bold border"
                  :class="getContainerPillClass(item.containerType)"
                >
                  <span class="w-1.5 h-1.5 rounded-full" :class="getContainerDotClass(item.containerType)"></span>
                  {{ item.containerType }}
                </span>
              </td>

              <!-- Quantity -->
              <td class="py-3.5 px-4 text-center font-black text-slate-900">
                {{ item.quantity }}
              </td>

              <!-- Unit Price -->
              <td class="py-3.5 px-4 text-right text-slate-600">
                {{ formatCurrency(item.unitPrice) }}
              </td>

              <!-- Total -->
              <td class="py-3.5 px-4 text-right font-black text-slate-900">
                {{ formatCurrency(item.total) }}
              </td>

              <!-- Actual Received -->
              <td class="py-3.5 px-4 text-right font-black text-emerald-600">
                {{ formatCurrency(item.actualReceived !== undefined ? item.actualReceived : item.total) }}
              </td>

              <!-- Payment Status Toggle Button -->
              <td class="py-3.5 px-4 text-center">
                <button 
                  @click="handleTogglePayment(item)"
                  class="px-2.5 py-1 rounded-xl text-[11px] font-black border transition-all active:scale-95"
                  :class="item.paymentStatus === 'PAID' 
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100' 
                    : 'bg-rose-50 text-rose-700 border-rose-200 hover:bg-rose-100 animate-pulse'"
                  :title="item.paymentStatus === 'PAID' ? 'Bấm để đổi sang Ghi Nợ' : 'Bấm để xác nhận Đã Thu'"
                >
                  {{ item.paymentStatus === 'PAID' ? '✅ ĐÃ THU' : '⚠️ CÒN NỢ' }}
                </button>
              </td>

              <!-- CỘT TRẢ VỎ (HIỂN THỊ ĐỦ VỎ HOẶC NỢ VỎ KÈM NÚT HOÀN TRẢ) -->
              <td class="py-3.5 px-4 text-center bg-amber-50/20 border-l border-amber-100">
                <!-- TH1: Khách đã trả đủ vỏ -->
                <span 
                  v-if="(item.debtShells || 0) === 0" 
                  class="inline-flex items-center gap-1 px-2.5 py-1 rounded-xl text-[11px] font-black bg-emerald-100 text-emerald-800 border border-emerald-200"
                  title="Khách hàng đã trả đủ toàn bộ vỏ bình gas"
                >
                  <span>🟢</span>
                  <span>ĐỦ VỎ ({{ item.returnedShells !== undefined ? item.returnedShells : item.quantity }}/{{ item.quantity }})</span>
                </span>

                <!-- TH2: Khách trả thiếu, còn nợ vỏ bình gas -->
                <div v-else class="inline-flex items-center gap-1.5">
                  <span 
                    class="px-2.5 py-1 rounded-xl text-[11px] font-black bg-rose-100 text-rose-800 border border-rose-200 animate-pulse"
                    title="Khách nợ vỏ gas, hệ thống ghi nhận để hoàn trả sau"
                  >
                    <span>⚠️ NỢ {{ item.debtShells }} VỎ</span>
                    <span class="text-[10px] text-rose-600 font-normal">({{ item.returnedShells || 0 }}/{{ item.quantity }})</span>
                  </span>

                  <!-- Nút hoàn trả vỏ nợ -->
                  <button 
                    @click="handleReturnShellsQuick(item)"
                    class="px-2.5 py-1 rounded-xl bg-amber-500 hover:bg-amber-600 active:scale-95 text-white text-[10px] font-black shadow-xs transition-all flex items-center gap-1"
                    title="Bấm để xác nhận khách hoàn trả số vỏ còn thiếu"
                  >
                    <span>🔄</span>
                    <span>Hoàn Vỏ</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 2. CHẾ ĐỘ THẺ BIÊN LAI (Đã xóa cụm nút Sửa/Xóa, thay bằng khu vực Trả Vỏ) -->
    <div v-else class="space-y-3">
      <div 
        v-for="item in filteredReports" 
        :key="item.id"
        class="bg-white p-4 sm:p-5 rounded-3xl border border-slate-200/80 shadow-xs hover:border-slate-300 hover:shadow-md transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-4 group"
      >
        <!-- Left: Customer Info, Gas Type, Notes -->
        <div class="flex items-start gap-3.5 flex-1 min-w-0">
          <div class="w-11 h-11 rounded-2xl bg-slate-900 text-white flex items-center justify-center text-sm font-black flex-shrink-0 shadow-sm">
            {{ getCustomerInitials(item.customerName) }}
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 flex-wrap mb-1">
              <h4 class="text-sm sm:text-base font-black text-slate-900 tracking-tight">
                {{ item.customerName }}
              </h4>

              <!-- Gas Type Pill with distinct color mark -->
              <span 
                class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-lg text-[11px] font-bold border"
                :class="getContainerPillClass(item.containerType)"
              >
                <span class="w-1.5 h-1.5 rounded-full" :class="getContainerDotClass(item.containerType)"></span>
                {{ item.containerType }}
              </span>

              <!-- Quantity Multiplier -->
              <span class="text-xs font-black text-sky-800 bg-sky-50 px-2.5 py-0.5 rounded-lg border border-sky-100">
                SL: {{ item.quantity }} bình
              </span>

              <!-- Icon sửa đơn nhỏ kín đáo -->
              <button 
                @click="openEditModal(item)"
                class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 p-1 rounded-lg transition-all"
                title="Chỉnh sửa nội dung phiếu"
              >
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                </svg>
              </button>
            </div>

            <!-- Unit price, Actual Received & Note snippet -->
            <div class="text-xs text-slate-500 flex items-center gap-2 flex-wrap">
              <span>Đơn giá: <strong class="text-slate-800 font-semibold">{{ formatCurrency(item.unitPrice) }}</strong></span>
              <span>•</span>
              <span>Đã thu: <strong class="text-emerald-700 font-bold">{{ formatCurrency(item.actualReceived !== undefined ? item.actualReceived : item.total) }}</strong></span>
              <span v-if="item.notes" class="text-slate-400">•</span>
              <span v-if="item.notes" class="text-slate-600 italic truncate max-w-xs font-medium">
                "{{ item.notes }}"
              </span>
            </div>
          </div>
        </div>

        <!-- Right: Total Amount, Payment Toggle Pill & KHU VỰC TRẢ VỎ (THAY THẾ THAO TÁC CŨ) -->
        <div class="flex items-center justify-between sm:justify-end gap-3.5 pt-3 sm:pt-0 border-t border-slate-100 sm:border-0 flex-wrap sm:flex-nowrap">
          <!-- Total Price -->
          <div class="text-left sm:text-right min-w-[90px]">
            <div class="text-base sm:text-lg font-black text-slate-900 tracking-tight">
              {{ formatCurrency(item.total) }}
            </div>
            <div class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Tổng tiền</div>
          </div>

          <!-- One-Tap Payment Status Toggle -->
          <button 
            @click="handleTogglePayment(item)"
            class="px-3 py-2 rounded-2xl text-xs font-black border transition-all active:scale-95 flex items-center gap-1.5 shadow-xs"
            :class="item.paymentStatus === 'PAID' 
              ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100' 
              : 'bg-rose-50 text-rose-700 border-rose-200 hover:bg-rose-100 animate-pulse'"
            :title="item.paymentStatus === 'PAID' ? 'Bấm để đổi sang Ghi Nợ' : 'Bấm để xác nhận Đã Thu Tiền'"
          >
            <span v-if="item.paymentStatus === 'PAID'">✅ ĐÃ THU</span>
            <span v-else>⚠️ GHI NỢ</span>
          </button>

          <!-- KHU VỰC TRẢ VỎ (THAY THẾ HOÀN TOÀN NÚT THAO TÁC SỬA/XÓA CŨ) -->
          <div class="flex items-center gap-1.5">
            <!-- TH1: Đã trả đủ vỏ bình -->
            <div 
              v-if="(item.debtShells || 0) === 0" 
              class="px-3 py-2 rounded-2xl bg-emerald-50 text-emerald-700 border border-emerald-200 text-xs font-black flex items-center gap-1.5 shadow-xs whitespace-nowrap"
              title="Khách hàng đã trả đủ toàn bộ vỏ bình gas"
            >
              <span>🟢</span>
              <span>ĐỦ VỎ ({{ item.returnedShells !== undefined ? item.returnedShells : item.quantity }}/{{ item.quantity }})</span>
            </div>

            <!-- TH2: Khách trả thiếu, nợ vỏ bình gas -->
            <div v-else class="flex items-center gap-1.5">
              <span 
                class="px-3 py-2 rounded-2xl bg-rose-50 text-rose-800 border border-rose-200 text-xs font-black flex items-center gap-1.5 animate-pulse shadow-xs whitespace-nowrap"
                title="Khách nợ vỏ gas, hệ thống đã ghi nhận để hoàn trả sau"
              >
                <span>⚠️ NỢ {{ item.debtShells }} VỎ</span>
              </span>

              <!-- Nút Hoàn Trả Vỏ Nợ Về Sau -->
              <button 
                @click="handleReturnShellsQuick(item)"
                class="px-3 py-2 rounded-2xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 active:scale-95 text-white text-xs font-black shadow-sm transition-all flex items-center gap-1 whitespace-nowrap"
                title="Bấm để ghi nhận khách hoàn trả số vỏ còn nợ"
              >
                <span>🔄</span>
                <span>Hoàn Vỏ</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create / Edit Modal Dialog (Thiết Kế Modal Sang Trọng) -->
    <div 
      v-if="modal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/60 backdrop-blur-sm transition-opacity"
      @click.self="closeModal"
    >
      <div class="bg-white w-full max-w-lg rounded-3xl shadow-2xl border border-slate-200 overflow-hidden transform transition-all animate-in fade-in zoom-in-95 duration-150">
        <!-- Modal Top Header -->
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-gradient-to-r from-slate-50 to-sky-50/40">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-2xl bg-sky-500/15 text-sky-700 flex items-center justify-center text-xl flex-shrink-0">
              📋
            </div>
            <div>
              <h3 class="text-sm font-black text-slate-900">
                {{ modal.isEdit ? 'Chỉnh Sửa Phiếu Giao Gas' : 'Nhập Phiếu Báo Cáo Giao Gas' }}
              </h3>
              <p class="text-[11px] text-slate-500 font-medium">Tự động kiểm tra trừ tồn kho gas và xác định công nợ</p>
            </div>
          </div>
          <button @click="closeModal" class="p-2 text-slate-400 hover:text-slate-700 hover:bg-white rounded-2xl transition-all">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Form Body -->
        <form @submit.prevent="handleSubmitForm" class="p-6 space-y-4">
          <!-- Error Alert inside Modal (Ví dụ: Thiếu bình gas trong kho) -->
          <div v-if="formError" class="p-3.5 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-xs flex items-center gap-2.5 font-bold animate-shake">
            <span class="text-lg flex-shrink-0">⚠️</span>
            <span>{{ formError }}</span>
          </div>

          <!-- 1. Customer Name -->
          <div>
            <label class="block text-xs font-extrabold text-slate-700 mb-1.5">
              Tên Khách Hàng <span class="text-rose-500">*</span>
            </label>
            <input 
              type="text" 
              v-model="form.customerName" 
              required
              placeholder="VD: Quán Cơm Tấm 24, Cô Lan, Bún Bò Huế 36..."
              class="w-full px-4 py-2.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all placeholder:text-slate-400 font-bold"
            />
          </div>

          <!-- 2. Gas Container Selector Chips with Live Stock Badge -->
          <div>
            <label class="block text-xs font-extrabold text-slate-700 mb-1.5">
              Chọn Loại Bình Gas <span class="text-rose-500">*</span>
            </label>
            <div class="grid grid-cols-2 gap-2">
              <button
                type="button"
                v-for="g in gasTypesWithInventory"
                :key="g.type"
                @click="selectGasType(g)"
                class="p-3 text-left rounded-2xl border transition-all text-xs flex flex-col justify-between"
                :class="form.containerType === g.type 
                  ? 'border-sky-500 bg-sky-50/90 ring-2 ring-sky-500/20 text-sky-950 font-black shadow-xs' 
                  : 'border-slate-200 hover:border-slate-300 bg-white text-slate-700 font-semibold'"
              >
                <span>🛢️ {{ g.type }}</span>
                <div class="flex items-center justify-between text-[11px] text-slate-500 mt-1.5">
                  <span>{{ formatCurrency(g.defaultPrice) }}</span>
                  <span v-if="g.stock !== undefined" class="text-[10px] font-black" :class="g.stock > 0 ? 'text-emerald-700' : 'text-rose-600'">
                    Kho: {{ g.stock }}
                  </span>
                </div>
              </button>
            </div>
          </div>

          <!-- 3. Quantity & Unit Price Stepper -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-extrabold text-slate-700 mb-1.5">
                Số Lượng Giao <span class="text-rose-500">*</span>
              </label>
              <div class="flex items-center border border-slate-200 rounded-2xl bg-slate-50 overflow-hidden">
                <button 
                  type="button" 
                  @click="changeQuantity(-1)"
                  class="px-3.5 py-2.5 text-slate-600 hover:bg-slate-200 font-black text-sm transition-colors active:scale-95"
                >
                  -
                </button>
                <input 
                  type="number" 
                  min="1" 
                  max="500" 
                  v-model.number="form.quantity" 
                  @input="handleQuantityChange"
                  class="w-full text-center py-2.5 text-xs font-black text-slate-900 bg-transparent focus:outline-none"
                />
                <button 
                  type="button" 
                  @click="changeQuantity(1)"
                  class="px-3.5 py-2.5 text-slate-600 hover:bg-slate-200 font-black text-sm transition-colors active:scale-95"
                >
                  +
                </button>
              </div>
            </div>

            <div>
              <label class="block text-xs font-extrabold text-slate-700 mb-1.5">
                Đơn Giá (VNĐ) <span class="text-rose-500">*</span>
              </label>
              <input 
                type="number" 
                step="5000"
                v-model.number="form.unitPrice" 
                @input="handleUnitPriceChange"
                required
                class="w-full px-3.5 py-2.5 text-xs font-black text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all"
              />
            </div>
          </div>

          <!-- 4. Total Calculation & Actual Received Money (Nghiệp vụ cốt lõi) -->
          <div class="p-4 bg-slate-100/90 rounded-2xl space-y-2.5 border border-slate-200/80">
            <div class="flex items-center justify-between">
              <span class="text-xs font-extrabold text-slate-700">Tổng Tiền Đơn Hàng:</span>
              <span class="text-lg font-black text-slate-900 tracking-tight">
                {{ formatCurrency(computedTotal) }}
              </span>
            </div>

            <!-- Input Tiền thực nhận từ khách -->
            <div>
              <div class="flex items-center justify-between mb-1">
                <label class="text-xs font-extrabold text-slate-700">
                  Số Tiền Thực Thu Từ Khách (VNĐ)
                </label>
                <button 
                  type="button"
                  @click="form.actualReceived = computedTotal"
                  class="text-[11px] text-sky-600 hover:underline font-black"
                >
                  Thu đủ 100%
                </button>
              </div>
              <input 
                type="number" 
                step="5000"
                min="0"
                v-model.number="form.actualReceived" 
                class="w-full px-3.5 py-2.5 text-xs font-black text-emerald-900 bg-white border border-slate-300 rounded-2xl focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 transition-all"
                placeholder="Nhập số tiền khách trả thực tế..."
              />
            </div>

            <!-- Dynamic Payment Status Badge -->
            <div class="pt-1 flex items-center justify-between text-xs">
              <span class="font-bold text-slate-500">Tự động phân loại:</span>
              <span 
                v-if="isPaidInFull" 
                class="inline-flex items-center gap-1.5 px-3 py-1 rounded-xl text-xs font-black bg-emerald-100 text-emerald-800 border border-emerald-200 shadow-xs"
              >
                ✅ ĐÃ THANH TOÁN ĐỦ (PAID)
              </span>
              <span 
                v-else 
                class="inline-flex items-center gap-1.5 px-3 py-1 rounded-xl text-xs font-black bg-rose-100 text-rose-800 border border-rose-200 shadow-xs"
              >
                ⚠️ GHI NỢ (DEBT) — Còn thiếu: {{ formatCurrency(computedTotal - (form.actualReceived || 0)) }}
              </span>
            </div>
          </div>

          <!-- 5. QUẢN LÝ TRẢ VỎ BÌNH GAS (Nếu bỏ trống = Khách trả đủ vỏ; Trả thiếu -> Ghi nhận nợ vỏ để về sau hoàn trả) -->
          <div class="p-4 bg-amber-50/80 rounded-2xl border border-amber-200/80 space-y-2.5">
            <div class="flex items-center justify-between">
              <label class="text-xs font-black text-amber-950 flex items-center gap-1.5">
                <span>🔄 Số Lượng Vỏ Bình Khách Trả</span>
                <span class="text-[10px] text-amber-700 font-bold">(Bỏ trống = Trả đủ vỏ)</span>
              </label>
              <button 
                type="button" 
                @click="form.returnedShells = form.quantity"
                class="text-[11px] text-amber-900 hover:underline font-black"
              >
                Trả đủ ({{ form.quantity }} vỏ)
              </button>
            </div>

            <!-- Input Số Vỏ Khách Trả -->
            <div class="relative">
              <input 
                type="number" 
                min="0"
                :max="form.quantity"
                v-model.number="form.returnedShells" 
                :placeholder="`Mặc định: Khách trả đủ ${form.quantity} vỏ rỗng`"
                class="w-full px-3.5 py-2.5 text-xs font-black text-amber-950 bg-white border border-amber-300 rounded-2xl focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all font-bold placeholder:text-slate-400"
              />
            </div>

            <!-- Trực quan trạng thái Trả Vỏ / Nợ Vỏ -->
            <div class="pt-1 flex items-center justify-between text-xs">
              <span class="font-bold text-slate-500">Tình trạng vỏ bình:</span>
              <span 
                v-if="computedDebtShells === 0" 
                class="inline-flex items-center gap-1.5 px-3 py-1 rounded-xl text-xs font-black bg-emerald-100 text-emerald-800 border border-emerald-200 shadow-xs"
              >
                ✅ ĐÃ TRẢ ĐỦ VỎ ({{ computedReturnedShells }}/{{ form.quantity }} vỏ)
              </span>
              <span 
                v-else 
                class="inline-flex items-center gap-1.5 px-3 py-1 rounded-xl text-xs font-black bg-rose-100 text-rose-800 border border-rose-200 shadow-xs animate-pulse"
              >
                ⚠️ NỢ {{ computedDebtShells }} VỎ — Hệ thống ghi nhận để hoàn trả về sau!
              </span>
            </div>
          </div>

          <!-- 6. Notes -->
          <div>
            <label class="block text-xs font-extrabold text-slate-700 mb-1">Ghi Chú Giao Hàng</label>
            <textarea 
              v-model="form.notes" 
              rows="2"
              placeholder="VD: Giao lên lầu 2, đổi bình gas cũ rỉ sét, hẹn 18h quay lại lấy tiền..."
              class="w-full px-4 py-2 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-500 transition-all placeholder:text-slate-400 font-medium"
            ></textarea>
          </div>

          <!-- Form Buttons -->
          <div class="pt-3 flex items-center justify-end gap-2.5 border-t border-slate-100">
            <button 
              type="button" 
              @click="closeModal" 
              class="px-4 py-2.5 text-xs font-bold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-2xl transition-all"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="formSubmitting"
              class="px-6 py-2.5 text-xs font-black text-white bg-gradient-to-r from-sky-500 to-cyan-500 hover:from-sky-600 hover:to-cyan-600 active:scale-95 disabled:opacity-50 rounded-2xl shadow-md shadow-sky-500/20 transition-all"
            >
              {{ formSubmitting ? 'Đang kiểm tra & trừ kho...' : (modal.isEdit ? 'Lưu Thay Đổi' : 'Xác Nhận Tạo Phiếu Giao') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import { deliveryReportService, GAS_TYPES } from '../services/deliveryReportService'
import { inventoryService } from '../services/inventoryService'

// State
const reports = ref([])
const inventories = ref([])
const loading = ref(false)
const isMockData = ref(false)
const formSubmitting = ref(false)
const formError = ref('')
const selectedDate = ref(new Date().toISOString().split('T')[0])
const viewMode = ref('CARDS') // 'CARDS' (Thẻ) hoặc 'TABLE' (Bảng)

// Filters
const searchKeyword = ref('')
const filterStatus = ref('')

// Modal state
const modal = reactive({
  isOpen: false,
  isEdit: false,
  currentId: null
})

// Form state (Đúng chuẩn CreateDeliveryReportRequest)
const form = reactive({
  employeeId: null,
  customerId: null,
  customerName: '',
  inventoryId: null,
  containerType: 'Bình 12kg Xám',
  quantity: 1,
  returnedShells: null, // Nếu để trống = Khách trả đủ vỏ
  unitPrice: 350000,
  actualReceived: 350000,
  paymentStatus: 'PAID',
  notes: '',
  receiptUrl: ''
})

// Computed Total
const computedTotal = computed(() => {
  return (Number(form.quantity) || 0) * (Number(form.unitPrice) || 0)
})

// Auto Payment Status Determination: actualReceived >= total -> PAID, else DEBT
const isPaidInFull = computed(() => {
  const received = Number(form.actualReceived) || 0
  return received >= computedTotal.value
})

// Computed Trả Vỏ: Nếu bỏ trống -> Mặc định trả đủ 100% vỏ giao
const computedReturnedShells = computed(() => {
  if (form.returnedShells === null || form.returnedShells === undefined || form.returnedShells === '') {
    return Number(form.quantity) || 1
  }
  return Math.max(0, Math.min(Number(form.quantity) || 1, Number(form.returnedShells)))
})

// Computed Nợ Vỏ: SL giao - SL trả
const computedDebtShells = computed(() => {
  return Math.max(0, (Number(form.quantity) || 1) - computedReturnedShells.value)
})

// Gas types mapped with inventory
const gasTypesWithInventory = computed(() => {
  return GAS_TYPES.map(g => {
    const inv = inventories.value.find(i => i.containerType === g.type)
    return {
      ...g,
      inventoryId: inv ? inv.id : null,
      stock: inv ? inv.fullQuantity : undefined
    }
  })
})

// Filtered reports
const filteredReports = computed(() => {
  return reports.value.filter(item => {
    if (searchKeyword.value.trim()) {
      const kw = searchKeyword.value.toLowerCase().trim()
      const matchName = item.customerName && item.customerName.toLowerCase().includes(kw)
      const matchType = item.containerType && item.containerType.toLowerCase().includes(kw)
      const matchNotes = item.notes && item.notes.toLowerCase().includes(kw)
      if (!matchName && !matchType && !matchNotes) return false
    }

    if (filterStatus.value === 'SHELL_DEBT') {
      if ((item.debtShells || 0) <= 0) return false
    } else if (filterStatus.value && item.paymentStatus !== filterStatus.value) {
      return false
    }

    return true
  })
})

// Debt Bills Count
const debtBillsCount = computed(() => {
  return reports.value.filter(r => r.paymentStatus === 'DEBT').length
})

// Debt Shells Count (Số đơn khách nợ vỏ)
const debtShellsCount = computed(() => {
  return reports.value.filter(r => (r.debtShells || 0) > 0).length
})

// Summary Stats
const stats = computed(() => {
  const totalQuantity = reports.value.reduce((sum, r) => sum + (Number(r.quantity) || 0), 0)
  const totalAmount = reports.value.reduce((sum, r) => sum + (Number(r.total) || 0), 0)
  const paidAmount = reports.value
    .filter(r => r.paymentStatus === 'PAID')
    .reduce((sum, r) => sum + (Number(r.actualReceived !== undefined ? r.actualReceived : r.total) || 0), 0)
  const debtAmount = Math.max(0, totalAmount - paidAmount)
  const debtShells = reports.value.reduce((sum, r) => sum + (Number(r.debtShells) || 0), 0)

  return { totalQuantity, totalAmount, paidAmount, debtAmount, debtShells }
})

// Methods
const fetchReports = async () => {
  loading.value = true
  try {
    const [repRes, invRes] = await Promise.all([
      deliveryReportService.getTodayReports(selectedDate.value),
      inventoryService.getInventories()
    ])
    reports.value = repRes.data || []
    isMockData.value = !!repRes.isMock
    inventories.value = invRes.data || []
  } catch (err) {
    console.error('Lỗi khi tải dữ liệu giao gas:', err)
  } finally {
    loading.value = false
  }
}

const selectGasType = (g) => {
  form.containerType = g.type
  form.inventoryId = g.inventoryId
  form.unitPrice = g.defaultPrice
  form.actualReceived = form.quantity * g.defaultPrice
}

const handleQuantityChange = () => {
  form.actualReceived = computedTotal.value
}

const handleUnitPriceChange = () => {
  form.actualReceived = computedTotal.value
}

const changeQuantity = (delta) => {
  form.quantity = Math.max(1, (Number(form.quantity) || 1) + delta)
  form.actualReceived = computedTotal.value
}

const openCreateModal = () => {
  formError.value = ''
  modal.isOpen = true
  modal.isEdit = false
  modal.currentId = null
  const defaultGas = gasTypesWithInventory.value[0] || GAS_TYPES[0]
  Object.assign(form, {
    employeeId: null,
    customerId: null,
    customerName: '',
    inventoryId: defaultGas.inventoryId || null,
    containerType: defaultGas.type,
    quantity: 1,
    returnedShells: null, // Bỏ trống = Mặc định khách trả đủ vỏ
    unitPrice: defaultGas.defaultPrice,
    actualReceived: defaultGas.defaultPrice,
    paymentStatus: 'PAID',
    notes: '',
    receiptUrl: ''
  })
}

const openEditModal = (item) => {
  formError.value = ''
  modal.isOpen = true
  modal.isEdit = true
  modal.currentId = item.id
  Object.assign(form, {
    employeeId: item.employeeId || null,
    customerId: item.customerId || null,
    customerName: item.customerName,
    inventoryId: item.inventoryId || null,
    containerType: item.containerType,
    quantity: item.quantity,
    returnedShells: item.returnedShells !== undefined ? item.returnedShells : item.quantity,
    unitPrice: item.unitPrice,
    actualReceived: item.actualReceived !== undefined ? item.actualReceived : item.total,
    paymentStatus: item.paymentStatus,
    notes: item.notes || '',
    receiptUrl: item.receiptUrl || ''
  })
}

const closeModal = () => {
  modal.isOpen = false
  formError.value = ''
}

const handleSubmitForm = async () => {
  formError.value = ''
  formSubmitting.value = true

  // Tự động xác định trạng thái thanh toán theo nghiệp vụ cốt lõi
  const determinedPaymentStatus = isPaidInFull.value ? 'PAID' : 'DEBT'
  
  // Xử lý trả vỏ: Nếu bỏ trống -> Mặc định bằng số lượng giao
  let finalReturnedShells = form.returnedShells
  if (finalReturnedShells === null || finalReturnedShells === undefined || finalReturnedShells === '') {
    finalReturnedShells = Number(form.quantity) || 1
  } else {
    finalReturnedShells = Math.max(0, Math.min(Number(form.quantity) || 1, Number(finalReturnedShells)))
  }
  const finalDebtShells = Math.max(0, (Number(form.quantity) || 1) - finalReturnedShells)
  const finalShellStatus = finalDebtShells === 0 ? 'RETURNED' : 'OWING'

  // Tìm inventoryId tương ứng nếu chưa có
  let currentInvId = form.inventoryId
  if (!currentInvId) {
    const matched = inventories.value.find(i => i.containerType === form.containerType)
    if (matched) currentInvId = matched.id
  }

  const payload = {
    employeeId: form.employeeId,
    customerId: form.customerId,
    customerName: form.customerName,
    inventoryId: currentInvId,
    containerType: form.containerType,
    quantity: form.quantity,
    returnedShells: finalReturnedShells,
    debtShells: finalDebtShells,
    shellStatus: finalShellStatus,
    unitPrice: form.unitPrice,
    total: computedTotal.value,
    actualReceived: Number(form.actualReceived) || 0,
    paymentStatus: determinedPaymentStatus,
    notes: form.notes,
    receiptUrl: form.receiptUrl,
    reportDate: selectedDate.value
  }

  try {
    if (modal.isEdit) {
      await deliveryReportService.updateReport(modal.currentId, payload)
    } else {
      await deliveryReportService.createReport(payload)
    }
    closeModal()
    fetchReports()
  } catch (err) {
    // Hiển thị lỗi từ backend (ví dụ: Số lượng bình đầy trong kho không đủ để giao!)
    formError.value = err.message || 'Thao tác tạo đơn giao gas thất bại!'
  } finally {
    formSubmitting.value = false
  }
}

const handleTogglePayment = async (item) => {
  const nextStatus = item.paymentStatus === 'PAID' ? 'Ghi Nợ' : 'Đã Thu Tiền'
  if (confirm(`Chuyển trạng thái đơn của [${item.customerName}] sang "${nextStatus}"?`)) {
    try {
      await deliveryReportService.togglePaymentStatus(item.id)
      fetchReports()
    } catch (err) {
      alert('Không thể cập nhật trạng thái: ' + err.message)
    }
  }
}

// Thao tác nhanh: Ghi nhận khách hoàn trả số vỏ còn nợ về sau
const handleReturnShellsQuick = async (item) => {
  if (confirm(`Xác nhận khách hàng [${item.customerName}] đã hoàn trả đủ ${item.debtShells} vỏ bình gas còn thiếu? Hệ thống sẽ ghi nhận hoàn tất và cộng vỏ rỗng vào kho.`)) {
    try {
      await deliveryReportService.returnShells(item.id, item.debtShells)
      fetchReports()
    } catch (err) {
      alert('Không thể cập nhật hoàn trả vỏ: ' + err.message)
    }
  }
}

const handleDelete = async (item) => {
  if (confirm(`Xác nhận xóa phiếu giao cho khách [${item.customerName}]?`)) {
    try {
      await deliveryReportService.deleteReport(item.id)
      fetchReports()
    } catch (err) {
      alert('Không thể xóa phiếu: ' + err.message)
    }
  }
}

// Formatters & UI Helpers
const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

const getCustomerInitials = (name) => {
  if (!name) return 'KH'
  const words = name.trim().split(' ')
  if (words.length === 1) return words[0].substring(0, 2).toUpperCase()
  return (words[0][0] + words[words.length - 1][0]).toUpperCase()
}

const getContainerPillClass = (type) => {
  if (type.includes('45kg')) return 'bg-amber-50 text-amber-900 border-amber-200'
  if (type.includes('Đỏ')) return 'bg-rose-50 text-rose-900 border-rose-200'
  if (type.includes('Mini')) return 'bg-purple-50 text-purple-900 border-purple-200'
  return 'bg-slate-100 text-slate-800 border-slate-200'
}

const getContainerDotClass = (type) => {
  if (type.includes('45kg')) return 'bg-amber-500'
  if (type.includes('Đỏ')) return 'bg-rose-500'
  if (type.includes('Mini')) return 'bg-purple-500'
  return 'bg-slate-400'
}

onMounted(() => {
  fetchReports()
})
</script>
