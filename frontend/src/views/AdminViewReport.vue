<template>
  <EmployeeLayout :is-backend-connected="true" wide>
    <!-- Header Section (Corporate Enterprise) -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-slate-900"></span>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">Đối Soát Thu Chi & Báo Cáo Ca Trực</h2>
          <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-slate-900 text-white">Quản Trị</span>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Giám sát doanh thu giao gas, đối soát tiền mặt thực thu, chi phí ca trực và quyết toán nộp két từng nhân viên</p>
      </div>

      <div class="flex items-center gap-2 flex-wrap">
        <!-- Nút Kê Khai Chi Phí -->
        <button 
          @click="openExpenseModal"
          class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-rose-50 text-rose-700 hover:bg-rose-100 border border-rose-200 text-xs font-semibold rounded-lg transition-colors"
          title="Kê khai khoản chi phát sinh trong ca"
        >
          <span>+ Kê Khai Chi Phí</span>
        </button>

        <!-- Nút In Báo Cáo -->
        <button 
          @click="handlePrintReport"
          class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-white text-slate-700 hover:bg-slate-50 border border-slate-300 text-xs font-semibold rounded-lg shadow-2xs transition-colors"
          title="In bảng đối soát hoặc lưu file PDF"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
          </svg>
          <span>In Báo Cáo</span>
        </button>

        <!-- Nút Xuất Excel -->
        <button 
          @click="handleExportCSV"
          class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <span>Xuất Excel / CSV</span>
        </button>
      </div>
    </div>

    <!-- 6 KPI Financial Summary Cards (Corporate Clean) -->
    <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3.5 mb-6">
      <!-- 1. Sản Lượng Bình Giao -->
      <div class="bg-white p-3.5 rounded-xl border border-slate-200 shadow-2xs">
        <div class="text-[10px] font-semibold uppercase tracking-wider text-slate-500 mb-1">Sản Lượng Giao</div>
        <div class="text-xl font-bold font-mono text-slate-900 tracking-tight">
          {{ stats.totalQuantity }} <span class="text-xs font-normal text-slate-500">bình</span>
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Tổng bình đã xuất kho</p>
      </div>

      <!-- 2. Tổng Doanh Thu Bán -->
      <div class="bg-white p-3.5 rounded-xl border border-slate-200 shadow-2xs">
        <div class="text-[10px] font-semibold uppercase tracking-wider text-slate-500 mb-1">Tổng Doanh Số</div>
        <div class="text-base font-bold font-mono text-slate-900 tracking-tight">
          {{ formatCurrency(stats.totalRevenue) }}
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Giá trị hàng xuất</p>
      </div>

      <!-- 3. Thực Thu Tiền Mặt (Thu +) -->
      <div class="bg-white p-3.5 rounded-xl border border-slate-200 shadow-2xs">
        <div class="text-[10px] font-semibold uppercase tracking-wider text-emerald-700 mb-1">Tiền Đã Thu (+)</div>
        <div class="text-base font-bold font-mono text-emerald-600 tracking-tight">
          {{ formatCurrency(stats.actualReceived) }}
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Tiền mặt shipper thu</p>
      </div>

      <!-- 4. Chi Phí Ca Trực (Chi -) -->
      <div class="bg-white p-3.5 rounded-xl border border-slate-200 shadow-2xs">
        <div class="text-[10px] font-semibold uppercase tracking-wider text-rose-700 mb-1">Chi Phí Ca (-)</div>
        <div class="text-base font-bold font-mono text-rose-600 tracking-tight">
          {{ formatCurrency(stats.totalExpenses) }}
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Xăng xe, ăn trưa, sửa xe</p>
      </div>

      <!-- 5. Thực Nộp Về Quỹ (= Thu - Chi) -->
      <div class="bg-slate-900 text-white p-3.5 rounded-xl shadow-2xs border border-slate-800">
        <div class="text-[10px] font-semibold uppercase tracking-wider text-slate-300 mb-1">Thực Nộp Két (=)</div>
        <div class="text-base font-bold font-mono text-emerald-400 tracking-tight">
          {{ formatCurrency(stats.netRemittance) }}
        </div>
        <p class="text-[10px] text-slate-400 mt-1">Thực thu - Chi phí ca</p>
      </div>

      <!-- 6. Khách Nợ Tiền & Nợ Vỏ -->
      <div class="bg-white p-3.5 rounded-xl border border-slate-200 shadow-2xs">
        <div class="text-[10px] font-semibold uppercase tracking-wider text-amber-700 mb-1">Khách Còn Nợ</div>
        <div class="text-base font-bold font-mono text-rose-600 tracking-tight">
          {{ formatCurrency(stats.totalDebt) }}
        </div>
        <p class="text-[10px] text-amber-700 font-medium mt-1">
          Nợ vỏ: <strong>{{ stats.totalDebtShells }} vỏ</strong>
        </p>
      </div>
    </div>

    <!-- BỘ LỌC ĐA CHIỀU (BỘ LỌC TỪNG NHÂN VIÊN & THỜI GIAN & THU CHI) -->
    <div class="bg-white p-4 rounded-xl border border-slate-200 shadow-2xs mb-6 space-y-3">
      <div class="flex items-center justify-between flex-wrap gap-2 pb-2 border-b border-slate-100">
        <div class="text-xs font-bold text-slate-800 uppercase tracking-tight">
          Bộ Lọc Báo Cáo Nhân Viên & Đối Soát
        </div>
        <div class="flex items-center gap-1.5 flex-wrap">
          <span class="text-[10px] font-semibold text-slate-400 uppercase tracking-wider">Thời gian:</span>
          <button 
            type="button" 
            @click="setQuickDate('TODAY')"
            class="px-2.5 py-1 rounded-md text-xs font-medium transition-colors"
            :class="quickDatePreset === 'TODAY' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Hôm Nay
          </button>
          <button 
            type="button" 
            @click="setQuickDate('YESTERDAY')"
            class="px-2.5 py-1 rounded-md text-xs font-medium transition-colors"
            :class="quickDatePreset === 'YESTERDAY' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Hôm Qua
          </button>
          <button 
            type="button" 
            @click="setQuickDate('WEEK')"
            class="px-2.5 py-1 rounded-md text-xs font-medium transition-colors"
            :class="quickDatePreset === 'WEEK' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            7 Ngày Qua
          </button>
          <button 
            type="button" 
            @click="setQuickDate('MONTH')"
            class="px-2.5 py-1 rounded-md text-xs font-medium transition-colors"
            :class="quickDatePreset === 'MONTH' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Tháng Này
          </button>
          <button 
            type="button" 
            @click="setQuickDate('ALL')"
            class="px-2.5 py-1 rounded-md text-xs font-medium transition-colors"
            :class="quickDatePreset === 'ALL' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Toàn Bộ
          </button>
        </div>
      </div>

      <!-- Grid các trường lọc -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
        <!-- 1. BỘ LỌC TỪNG NHÂN VIÊN GIAO HÀNG -->
        <div>
          <label class="block text-[11px] font-semibold text-slate-600 mb-1">
            Nhân Viên Giao Hàng
          </label>
          <select 
            v-model="filters.employee"
            class="w-full px-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
          >
            <option value="ALL">Toàn Bộ Nhân Viên (Tất cả)</option>
            <option v-for="emp in employeeList" :key="emp.id" :value="emp.name">
              {{ emp.name }} ({{ emp.role }})
            </option>
          </select>
        </div>

        <!-- 2. Lọc Từ Ngày -->
        <div>
          <label class="block text-[11px] font-semibold text-slate-600 mb-1">
            Từ Ngày
          </label>
          <input 
            type="date" 
            v-model="filters.startDate"
            class="w-full px-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors cursor-pointer"
          />
        </div>

        <!-- 3. Lọc Đến Ngày -->
        <div>
          <label class="block text-[11px] font-semibold text-slate-600 mb-1">
            Đến Ngày
          </label>
          <input 
            type="date" 
            v-model="filters.endDate"
            class="w-full px-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors cursor-pointer"
          />
        </div>

        <!-- 4. Lọc Trạng Thái Thanh Toán -->
        <div>
          <label class="block text-[11px] font-semibold text-slate-600 mb-1">
            Trạng Thái Tiền
          </label>
          <select 
            v-model="filters.paymentStatus"
            class="w-full px-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
          >
            <option value="ALL">Tất Cả Trạng Thái Tiền</option>
            <option value="PAID">Đã Thu Đủ Tiền (PAID)</option>
            <option value="DEBT">Khách Còn Nợ Tiền (DEBT)</option>
          </select>
        </div>

        <!-- 5. Lọc Tình Trạng Trả Vỏ -->
        <div>
          <label class="block text-[11px] font-semibold text-slate-600 mb-1">
            Tình Trạng Trả Vỏ
          </label>
          <select 
            v-model="filters.shellStatus"
            class="w-full px-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
          >
            <option value="ALL">Tất Cả Tình Trạng Vỏ</option>
            <option value="RETURNED">Đã Trả Đủ Vỏ (Đủ)</option>
            <option value="OWING">Khách Còn Nợ Vỏ (Nợ)</option>
          </select>
        </div>
      </div>

      <!-- Ô tìm kiếm từ khóa -->
      <div class="pt-2 border-t border-slate-100 flex items-center justify-between gap-3 flex-wrap">
        <div class="relative flex-1 min-w-[260px]">
          <input 
            type="text" 
            v-model="filters.keyword"
            placeholder="Tìm theo tên khách, mã phiếu, chủng loại bình gas hoặc ghi chú..." 
            class="w-full pl-9 pr-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-2.5 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- Nút đặt lại bộ lọc -->
        <button 
          @click="resetFilters" 
          class="px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-semibold transition-colors"
        >
          Đặt lại bộ lọc
        </button>
      </div>
    </div>

    <!-- MAIN TABS: 1. GIAO GAS | 2. CHI PHÍ CA TRỰC | 3. BẢNG ĐỐI SOÁT TỪNG NHÂN SỰ -->
    <div class="bg-white rounded-xl border border-slate-200 shadow-2xs overflow-hidden mb-6">
      <!-- Tab Header Buttons -->
      <div class="px-4 py-2.5 border-b border-slate-200 bg-slate-50 flex items-center justify-between flex-wrap gap-2">
        <div class="flex items-center gap-1.5 flex-wrap">
          <button 
            @click="activeTab = 'DELIVERY'"
            class="px-3 py-1.5 rounded-md text-xs font-semibold transition-colors"
            :class="activeTab === 'DELIVERY' 
              ? 'bg-slate-900 text-white' 
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200'"
          >
            Danh Sách Giao Gas ({{ filteredDeliveries.length }})
          </button>

          <button 
            @click="activeTab = 'EXPENSES'"
            class="px-3 py-1.5 rounded-md text-xs font-semibold transition-colors"
            :class="activeTab === 'EXPENSES' 
              ? 'bg-slate-900 text-white' 
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200'"
          >
            Chi Phí Ca Trực ({{ filteredExpenses.length }})
          </button>

          <button 
            @click="activeTab = 'SUMMARY'"
            class="px-3 py-1.5 rounded-md text-xs font-semibold transition-colors"
            :class="activeTab === 'SUMMARY' 
              ? 'bg-slate-900 text-white' 
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200'"
          >
            Bảng Đối Soát Từng Nhân Sự
          </button>
        </div>

        <div class="text-xs text-slate-500 font-medium">
          Đang xem: <strong class="text-slate-900">{{ activeTab === 'DELIVERY' ? filteredDeliveries.length + ' chuyến giao' : (activeTab === 'EXPENSES' ? filteredExpenses.length + ' khoản chi' : employeeSummaryList.length + ' nhân sự') }}</strong>
        </div>
      </div>

      <!-- TAB 1: BẢNG BÁO CÁO GIAO GAS -->
      <div v-if="activeTab === 'DELIVERY'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-50 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
              <th class="py-2.5 px-3">Ngày Giao</th>
              <th class="py-2.5 px-3">Nhân Viên</th>
              <th class="py-2.5 px-3">Khách Hàng</th>
              <th class="py-2.5 px-3">Loại Gas</th>
              <th class="py-2.5 px-3 text-center">SL</th>
              <th class="py-2.5 px-3 text-right">Đơn Giá</th>
              <th class="py-2.5 px-3 text-right">Thành Tiền</th>
              <th class="py-2.5 px-3 text-right">Thực Thu</th>
              <th class="py-2.5 px-3 text-center">Tiền Mặt</th>
              <th class="py-2.5 px-3 text-center">Vỏ Bình</th>
              <th class="py-2.5 px-3">Ghi Chú</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr v-if="filteredDeliveries.length === 0">
              <td colspan="11" class="py-10 text-center text-slate-400">
                Không tìm thấy phiếu giao gas nào phù hợp với bộ lọc hiện tại.
              </td>
            </tr>
            <tr 
              v-for="item in filteredDeliveries" 
              :key="item.id"
              class="hover:bg-slate-50/70 transition-colors"
            >
              <td class="py-3 px-3 font-medium text-slate-600 whitespace-nowrap">
                {{ item.reportDate }}
              </td>
              <td class="py-3 px-3 font-bold text-slate-900 whitespace-nowrap">
                {{ item.employeeName }}
              </td>
              <td class="py-3 px-3 font-semibold text-slate-800">
                {{ item.customerName }}
              </td>
              <td class="py-3 px-3 whitespace-nowrap">
                <span class="px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-700">
                  {{ item.containerType }}
                </span>
              </td>
              <td class="py-3 px-3 text-center font-bold text-slate-900">
                {{ item.quantity }}
              </td>
              <td class="py-3 px-3 text-right font-mono text-slate-600">
                {{ formatCurrency(item.unitPrice) }}
              </td>
              <td class="py-3 px-3 text-right font-mono font-bold text-slate-900">
                {{ formatCurrency(item.total) }}
              </td>
              <td class="py-3 px-3 text-right font-mono font-bold text-emerald-600">
                {{ formatCurrency(item.actualReceived !== undefined ? item.actualReceived : item.total) }}
              </td>
              <td class="py-3 px-3 text-center whitespace-nowrap">
                <span 
                  class="inline-block px-2 py-0.5 rounded text-[10px] font-bold"
                  :class="item.paymentStatus === 'PAID' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-rose-50 text-rose-700 border border-rose-200'"
                >
                  {{ item.paymentStatus === 'PAID' ? 'ĐÃ THU' : 'CÒN NỢ' }}
                </span>
              </td>
              <td class="py-3 px-3 text-center whitespace-nowrap">
                <span 
                  v-if="(item.debtShells || 0) === 0" 
                  class="inline-block px-2 py-0.5 rounded text-[10px] font-medium bg-slate-100 text-slate-600"
                >
                  Đủ vỏ
                </span>
                <span 
                  v-else 
                  class="inline-block px-2 py-0.5 rounded text-[10px] font-bold bg-amber-50 text-amber-700 border border-amber-200"
                >
                  Nợ {{ item.debtShells }} vỏ
                </span>
              </td>
              <td class="py-3 px-3 text-slate-500 max-w-xs truncate">
                {{ item.notes || '—' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- TAB 2: BẢNG CHI PHÍ PHÁT SINH CỦA NHÂN VIÊN -->
      <div v-else-if="activeTab === 'EXPENSES'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-50 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
              <th class="py-2.5 px-3">Ngày Chi</th>
              <th class="py-2.5 px-3">Nhân Viên</th>
              <th class="py-2.5 px-3">Khoản Mục Chi Tiêu</th>
              <th class="py-2.5 px-3 text-right">Số Tiền (VNĐ)</th>
              <th class="py-2.5 px-3 text-center">Trạng Thái Duyệt</th>
              <th class="py-2.5 px-3">Ghi Chú Giải Trình</th>
              <th class="py-2.5 px-3 text-right">Thao Tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr v-if="filteredExpenses.length === 0">
              <td colspan="7" class="py-10 text-center text-slate-400">
                Không có khoản chi phí nào phù hợp với bộ lọc hiện tại.
              </td>
            </tr>
            <tr 
              v-for="exp in filteredExpenses" 
              :key="exp.id"
              class="hover:bg-slate-50/70 transition-colors"
            >
              <td class="py-3 px-3 text-slate-600 whitespace-nowrap">
                {{ exp.expenseDate }}
              </td>
              <td class="py-3 px-3 font-bold text-slate-900 whitespace-nowrap">
                {{ exp.employeeName }}
              </td>
              <td class="py-3 px-3 font-semibold text-slate-800">
                {{ exp.category }}
              </td>
              <td class="py-3 px-3 text-right font-mono font-bold text-rose-600">
                {{ formatCurrency(exp.amount) }}
              </td>
              <td class="py-3 px-3 text-center whitespace-nowrap">
                <span 
                  class="inline-block px-2 py-0.5 rounded text-[10px] font-bold"
                  :class="exp.status === 'APPROVED' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-amber-50 text-amber-700 border border-amber-200'"
                >
                  {{ exp.status === 'APPROVED' ? 'Đã duyệt' : 'Chờ duyệt' }}
                </span>
              </td>
              <td class="py-3 px-3 text-slate-500 max-w-xs truncate">
                {{ exp.notes || '—' }}
              </td>
              <td class="py-3 px-3 text-right whitespace-nowrap">
                <button 
                  v-if="exp.status !== 'APPROVED'"
                  @click="handleApproveExpense(exp.id)"
                  class="text-emerald-700 hover:text-emerald-800 font-semibold px-2 py-1 rounded hover:bg-emerald-50 transition-colors mr-1"
                >
                  Duyệt
                </button>
                <button 
                  @click="handleDeleteExpense(exp.id)"
                  class="text-rose-600 hover:text-rose-800 font-semibold px-2 py-1 rounded hover:bg-rose-50 transition-colors"
                >
                  Xóa
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- TAB 3: BẢNG ĐỐI SOÁT TỔNG HỢP THEO TỪNG NHÂN SỰ -->
      <div v-else-if="activeTab === 'SUMMARY'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="border-b border-slate-200 bg-slate-50 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
              <th class="py-2.5 px-3">Nhân Viên Shipper</th>
              <th class="py-2.5 px-3 text-center">SL Chuyến</th>
              <th class="py-2.5 px-3 text-center">Bình Giao</th>
              <th class="py-2.5 px-3 text-right">Tổng Doanh Số</th>
              <th class="py-2.5 px-3 text-right">Tiền Thực Thu (+)</th>
              <th class="py-2.5 px-3 text-right">Chi Phí Ca (-)</th>
              <th class="py-2.5 px-3 text-right font-bold text-slate-900">Thực Nộp Két (=)</th>
              <th class="py-2.5 px-3 text-right">Khách Còn Nợ</th>
              <th class="py-2.5 px-3 text-center">Vỏ Chưa Thu</th>
              <th class="py-2.5 px-3 text-center">Trạng Thái Ca</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            <tr v-if="employeeSummaryList.length === 0">
              <td colspan="10" class="py-10 text-center text-slate-400">
                Không có dữ liệu đối soát nhân sự trong kỳ lọc.
              </td>
            </tr>
            <tr 
              v-for="s in employeeSummaryList" 
              :key="s.employeeName"
              class="hover:bg-slate-50/70 transition-colors"
            >
              <td class="py-3 px-3 font-bold text-slate-900 whitespace-nowrap">
                {{ s.employeeName }}
              </td>
              <td class="py-3 px-3 text-center font-mono">
                {{ s.deliveryCount }}
              </td>
              <td class="py-3 px-3 text-center font-mono font-bold text-slate-900">
                {{ s.totalQuantity }}
              </td>
              <td class="py-3 px-3 text-right font-mono text-slate-600">
                {{ formatCurrency(s.totalRevenue) }}
              </td>
              <td class="py-3 px-3 text-right font-mono font-bold text-emerald-600">
                {{ formatCurrency(s.actualReceived) }}
              </td>
              <td class="py-3 px-3 text-right font-mono font-bold text-rose-600">
                {{ formatCurrency(s.totalExpenses) }}
              </td>
              <td class="py-3 px-3 text-right font-mono font-bold text-emerald-700 bg-slate-50">
                {{ formatCurrency(s.netRemittance) }}
              </td>
              <td class="py-3 px-3 text-right font-mono text-rose-600">
                {{ formatCurrency(s.debtAmount) }}
              </td>
              <td class="py-3 px-3 text-center">
                <span 
                  v-if="s.debtShells > 0"
                  class="inline-block px-2 py-0.5 rounded text-[10px] font-bold bg-amber-50 text-amber-700 border border-amber-200"
                >
                  Nợ {{ s.debtShells }} vỏ
                </span>
                <span v-else class="text-slate-400 text-[10px]">Đủ vỏ</span>
              </td>
              <td class="py-3 px-3 text-center">
                <span 
                  class="inline-block px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200"
                >
                  Đã hoàn tất ca
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Kê Khai Chi Phí Ca Trực (Corporate Clean) -->
    <div 
      v-if="expenseModal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs"
    >
      <div class="bg-white rounded-xl shadow-xl border border-slate-200 max-w-md w-full p-6 overflow-hidden">
        <div class="flex items-center justify-between pb-3 border-b border-slate-100 mb-4">
          <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight">Kê Khai Chi Phí Ca Trực</h3>
          <button @click="closeExpenseModal" class="text-slate-400 hover:text-slate-600 text-lg leading-none">
            ✕
          </button>
        </div>

        <div v-if="expenseModal.error" class="mb-4 p-2.5 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg">
          {{ expenseModal.error }}
        </div>

        <form @submit.prevent="submitExpenseForm" class="space-y-3.5 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Nhân Viên Chi Tiêu *</label>
            <select 
              v-model="expenseForm.employeeName" 
              required
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            >
              <option v-for="emp in employeeList" :key="emp.id" :value="emp.name">
                {{ emp.name }}
              </option>
            </select>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Khoản Mục Chi Tiêu *</label>
            <select 
              v-model="expenseForm.category" 
              required
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            >
              <option value="Xăng xe máy giao hàng">Xăng xe máy giao hàng</option>
              <option value="Cơm trưa / Nước uống ca trực">Cơm trưa / Nước uống ca trực</option>
              <option value="Vá xe / Sửa chữa xe khẩn cấp">Vá xe / Sửa chữa xe khẩn cấp</option>
              <option value="Mua dây chằng, bao tải bảo hộ">Mua dây chằng, bao tải bảo hộ</option>
              <option value="Chi phí phát sinh khác">Chi phí phát sinh khác</option>
            </select>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Số Tiền (VNĐ) *</label>
            <input 
              type="number" 
              v-model.number="expenseForm.amount" 
              min="1000" 
              step="1000"
              required
              placeholder="VD: 50000, 100000..."
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            />
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Ghi Chú Giải Trình</label>
            <textarea 
              v-model="expenseForm.notes" 
              rows="2"
              placeholder="VD: Đổ xăng tại cây xăng Petrolimex Q7..."
              class="w-full px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors resize-none"
            ></textarea>
          </div>

          <div class="flex items-center justify-end gap-2 pt-3 border-t border-slate-100">
            <button 
              type="button" 
              @click="closeExpenseModal"
              class="px-3.5 py-1.5 text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-lg font-semibold transition-colors"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="expenseModal.submitting"
              class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 disabled:opacity-50 text-white rounded-lg font-semibold shadow-2xs transition-colors"
            >
              {{ expenseModal.submitting ? 'Đang lưu...' : 'Lưu Khoản Chi' }}
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
import { deliveryReportService } from '../services/deliveryReportService.js'
import { expenseService } from '../services/expenseService.js'

const activeTab = ref('DELIVERY') // 'DELIVERY', 'EXPENSES', 'SUMMARY'
const quickDatePreset = ref('TODAY')

const employeeList = ref([
  { id: 'emp-01', name: 'Nguyễn Văn Giao (Shipper 01)', role: 'Tài xế giao gas' },
  { id: 'emp-02', name: 'Trần Văn Tốc (Shipper 02)', role: 'Tài xế giao gas' },
  { id: 'emp-03', name: 'Lê Hoàng Nam (Shipper 03)', role: 'Tài xế giao gas' }
])

const deliveries = ref([
  {
    id: 'rep-001',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    customerName: 'Quán Ăn Cô Ba',
    containerType: 'Bình 12kg Xám',
    quantity: 2,
    returnedShells: 2,
    debtShells: 0,
    unitPrice: 350000,
    total: 700000,
    actualReceived: 700000,
    paymentStatus: 'PAID',
    notes: 'Giao trực tiếp tại bếp'
  },
  {
    id: 'rep-002',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    customerName: 'Bún Bò Huế 36',
    containerType: 'Bình 45kg Công Nghiệp',
    quantity: 1,
    returnedShells: 0,
    debtShells: 1,
    unitPrice: 1250000,
    total: 1250000,
    actualReceived: 0,
    paymentStatus: 'DEBT',
    notes: 'Khách hẹn chiều lấy tiền, nợ 1 vỏ 45kg'
  },
  {
    id: 'rep-003',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Trần Văn Tốc (Shipper 02)',
    customerName: 'Anh Nam - Chung cư Flora',
    containerType: 'Bình 12kg Đỏ',
    quantity: 1,
    returnedShells: 1,
    debtShells: 0,
    unitPrice: 360000,
    total: 360000,
    actualReceived: 360000,
    paymentStatus: 'PAID',
    notes: 'Chuyển khoản QR'
  }
])

const expenses = ref([
  {
    id: 'exp-01',
    expenseDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    category: 'Xăng xe máy giao hàng',
    amount: 50000,
    status: 'APPROVED',
    notes: 'Đổ xăng ca sáng'
  },
  {
    id: 'exp-02',
    expenseDate: new Date().toISOString().split('T')[0],
    employeeName: 'Trần Văn Tốc (Shipper 02)',
    category: 'Vá xe / Sửa chữa xe khẩn cấp',
    amount: 30000,
    status: 'APPROVED',
    notes: 'Cán đinh đường Nguyễn Thị Thập'
  }
])

const filters = reactive({
  employee: 'ALL',
  startDate: new Date().toISOString().split('T')[0],
  endDate: new Date().toISOString().split('T')[0],
  paymentStatus: 'ALL',
  shellStatus: 'ALL',
  keyword: ''
})

const expenseModal = reactive({
  isOpen: false,
  submitting: false,
  error: ''
})

const expenseForm = reactive({
  employeeName: employeeList.value[0].name,
  category: 'Xăng xe máy giao hàng',
  amount: 50000,
  notes: ''
})

function formatCurrency(amount) {
  if (amount === undefined || amount === null) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

function setQuickDate(preset) {
  quickDatePreset.value = preset
  const today = new Date()
  const todayStr = today.toISOString().split('T')[0]

  if (preset === 'TODAY') {
    filters.startDate = todayStr
    filters.endDate = todayStr
  } else if (preset === 'YESTERDAY') {
    const yest = new Date(today)
    yest.setDate(yest.getDate() - 1)
    const yestStr = yest.toISOString().split('T')[0]
    filters.startDate = yestStr
    filters.endDate = yestStr
  } else if (preset === 'WEEK') {
    const pastWeek = new Date(today)
    pastWeek.setDate(pastWeek.getDate() - 7)
    filters.startDate = pastWeek.toISOString().split('T')[0]
    filters.endDate = todayStr
  } else if (preset === 'MONTH') {
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1)
    filters.startDate = firstDay.toISOString().split('T')[0]
    filters.endDate = todayStr
  } else if (preset === 'ALL') {
    filters.startDate = ''
    filters.endDate = ''
  }
}

function resetFilters() {
  filters.employee = 'ALL'
  filters.paymentStatus = 'ALL'
  filters.shellStatus = 'ALL'
  filters.keyword = ''
  setQuickDate('TODAY')
}

const filteredDeliveries = computed(() => {
  return deliveries.value.filter(d => {
    if (filters.employee !== 'ALL' && d.employeeName !== filters.employee) return false
    if (filters.startDate && d.reportDate < filters.startDate) return false
    if (filters.endDate && d.reportDate > filters.endDate) return false
    if (filters.paymentStatus !== 'ALL' && d.paymentStatus !== filters.paymentStatus) return false
    if (filters.shellStatus === 'RETURNED' && (d.debtShells || 0) > 0) return false
    if (filters.shellStatus === 'OWING' && (d.debtShells || 0) === 0) return false

    if (filters.keyword) {
      const kw = filters.keyword.toLowerCase()
      const matchCust = d.customerName && d.customerName.toLowerCase().includes(kw)
      const matchType = d.containerType && d.containerType.toLowerCase().includes(kw)
      const matchNote = d.notes && d.notes.toLowerCase().includes(kw)
      if (!matchCust && !matchType && !matchNote) return false
    }
    return true
  })
})

const filteredExpenses = computed(() => {
  return expenses.value.filter(e => {
    if (filters.employee !== 'ALL' && e.employeeName !== filters.employee) return false
    if (filters.startDate && e.expenseDate < filters.startDate) return false
    if (filters.endDate && e.expenseDate > filters.endDate) return false
    if (filters.keyword) {
      const kw = filters.keyword.toLowerCase()
      const matchCat = e.category && e.category.toLowerCase().includes(kw)
      const matchNote = e.notes && e.notes.toLowerCase().includes(kw)
      if (!matchCat && !matchNote) return false
    }
    return true
  })
})

const stats = computed(() => {
  const totalQuantity = filteredDeliveries.value.reduce((s, d) => s + (Number(d.quantity) || 0), 0)
  const totalRevenue = filteredDeliveries.value.reduce((s, d) => s + (Number(d.total) || 0), 0)
  const actualReceived = filteredDeliveries.value.reduce((s, d) => s + (Number(d.actualReceived !== undefined ? d.actualReceived : d.total) || 0), 0)
  const totalDebt = Math.max(0, totalRevenue - actualReceived)
  const totalDebtShells = filteredDeliveries.value.reduce((s, d) => s + (Number(d.debtShells) || 0), 0)
  const totalExpenses = filteredExpenses.value.reduce((s, e) => s + (Number(e.amount) || 0), 0)
  const netRemittance = Math.max(0, actualReceived - totalExpenses)

  return { totalQuantity, totalRevenue, actualReceived, totalDebt, totalDebtShells, totalExpenses, netRemittance }
})

const employeeSummaryList = computed(() => {
  const map = {}
  filteredDeliveries.value.forEach(d => {
    const emp = d.employeeName || 'Khác'
    if (!map[emp]) {
      map[emp] = {
        employeeName: emp,
        deliveryCount: 0,
        totalQuantity: 0,
        totalRevenue: 0,
        actualReceived: 0,
        debtAmount: 0,
        debtShells: 0,
        totalExpenses: 0
      }
    }
    map[emp].deliveryCount += 1
    map[emp].totalQuantity += Number(d.quantity) || 0
    map[emp].totalRevenue += Number(d.total) || 0
    map[emp].actualReceived += Number(d.actualReceived !== undefined ? d.actualReceived : d.total) || 0
    map[emp].debtAmount += Math.max(0, (Number(d.total) || 0) - (Number(d.actualReceived !== undefined ? d.actualReceived : d.total) || 0))
    map[emp].debtShells += Number(d.debtShells) || 0
  })

  filteredExpenses.value.forEach(e => {
    const emp = e.employeeName || 'Khác'
    if (!map[emp]) {
      map[emp] = {
        employeeName: emp,
        deliveryCount: 0,
        totalQuantity: 0,
        totalRevenue: 0,
        actualReceived: 0,
        debtAmount: 0,
        debtShells: 0,
        totalExpenses: 0
      }
    }
    map[emp].totalExpenses += Number(e.amount) || 0
  })

  return Object.values(map).map(m => ({
    ...m,
    netRemittance: Math.max(0, m.actualReceived - m.totalExpenses)
  }))
})

function openExpenseModal() {
  expenseModal.error = ''
  expenseModal.isOpen = true
  expenseForm.employeeName = employeeList.value[0].name
  expenseForm.category = 'Xăng xe máy giao hàng'
  expenseForm.amount = 50000
  expenseForm.notes = ''
}

function closeExpenseModal() {
  expenseModal.isOpen = false
  expenseModal.error = ''
}

async function submitExpenseForm() {
  expenseModal.error = ''
  expenseModal.submitting = true
  try {
    const newExp = {
      id: 'exp-' + Date.now(),
      expenseDate: new Date().toISOString().split('T')[0],
      employeeName: expenseForm.employeeName,
      category: expenseForm.category,
      amount: expenseForm.amount,
      status: 'APPROVED',
      notes: expenseForm.notes
    }
    expenses.value.unshift(newExp)
    closeExpenseModal()
  } catch (err) {
    expenseModal.error = err.message || 'Thao tác không thành công!'
  } finally {
    expenseModal.submitting = false
  }
}

function handleApproveExpense(id) {
  const item = expenses.value.find(e => e.id === id)
  if (item) {
    item.status = 'APPROVED'
  }
}

function handleDeleteExpense(id) {
  if (confirm('Xóa khoản chi phí này?')) {
    const idx = expenses.value.findIndex(e => e.id === id)
    if (idx !== -1) {
      expenses.value.splice(idx, 1)
    }
  }
}

function handlePrintReport() {
  window.print()
}

function handleExportCSV() {
  let csv = '\uFEFFMã Phiếu,Ngày Giao,Nhân Viên,Khách Hàng,Loại Gas,Số Lượng,Đơn Giá,Thành Tiền,Thực Thu,Trạng Thái Tiền,Nợ Vỏ,Ghi Chú\n'
  filteredDeliveries.value.forEach(d => {
    csv += `"${d.id}","${d.reportDate}","${d.employeeName}","${d.customerName}","${d.containerType}",${d.quantity},${d.unitPrice},${d.total},${d.actualReceived !== undefined ? d.actualReceived : d.total},"${d.paymentStatus}","${d.debtShells || 0}","${d.notes || ''}"\n`
  })
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `BaoCao_DoiSoat_${new Date().toISOString().split('T')[0]}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

async function loadData() {
  try {
    const repRes = await deliveryReportService.getTodayReports()
    if (repRes && repRes.data && repRes.data.length > 0) {
      deliveries.value = repRes.data
    }
  } catch (err) {
    console.warn('Lỗi nạp báo cáo:', err)
  }
}

onMounted(() => {
  loadData()
})
</script>
