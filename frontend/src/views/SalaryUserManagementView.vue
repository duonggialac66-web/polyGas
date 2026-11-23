<template>
  <div class="min-h-screen bg-slate-50 flex flex-col antialiased selection:bg-sky-500 selection:text-white">
    
    <!-- TOAST NOTIFICATION POPUP (HỆ THỐNG THÔNG BÁO LỖI / THÀNH CÔNG ĐẸP MẮT) -->
    <div class="fixed top-5 right-5 z-50 flex flex-col gap-2 max-w-sm pointer-events-none">
      <transition-group 
        enter-active-class="transform ease-out duration-300 transition" 
        enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2" 
        enter-to-class="translate-y-0 opacity-100 sm:translate-x-0" 
        leave-active-class="transition ease-in duration-200" 
        leave-from-class="opacity-100" 
        leave-to-class="opacity-0"
      >
        <div 
          v-for="toast in toasts" 
          :key="toast.id" 
          class="pointer-events-auto flex items-start gap-3 p-4 rounded-2xl shadow-xl border text-xs font-semibold backdrop-blur-md transition-all"
          :class="{
            'bg-emerald-50/95 border-emerald-200 text-emerald-900 shadow-emerald-500/10': toast.type === 'success',
            'bg-rose-50/95 border-rose-200 text-rose-900 shadow-rose-500/10': toast.type === 'error',
            'bg-amber-50/95 border-amber-200 text-amber-900 shadow-amber-500/10': toast.type === 'warning',
            'bg-sky-50/95 border-sky-200 text-sky-900 shadow-sky-500/10': toast.type === 'info'
          }"
        >
          <span class="text-base flex-shrink-0">
            {{ toast.type === 'success' ? '✅' : toast.type === 'error' ? '❌' : toast.type === 'warning' ? '⚠️' : 'ℹ️' }}
          </span>
          <div class="flex-1 leading-snug">
            <div class="font-bold text-slate-900 mb-0.5">{{ toast.title }}</div>
            <div class="text-[11px] opacity-90">{{ toast.message }}</div>
          </div>
          <button @click="removeToast(toast.id)" class="text-slate-400 hover:text-slate-600 p-0.5">✕</button>
        </div>
      </transition-group>
    </div>

    <!-- Header Điều Hướng Cấp Cao -->
    <header class="sticky top-0 z-30 bg-white/95 backdrop-blur-md border-b border-slate-200/80 px-4 sm:px-8 py-3.5 shadow-sm">
      <div class="max-w-7xl mx-auto flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <!-- Logo & Thông Tin Phân Hệ -->
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-2xl bg-gradient-to-tr from-sky-600 via-sky-500 to-cyan-400 flex items-center justify-center text-white shadow-md shadow-sky-500/20">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <div>
            <div class="flex items-center gap-2">
              <h1 class="text-base sm:text-lg font-black tracking-tight text-slate-900">
                OriginGas Enterprise
              </h1>
              <span class="px-2 py-0.5 rounded-full text-[10px] font-extrabold uppercase bg-sky-100 text-sky-800 tracking-wider">
                Module Lương & Nhân Sự
              </span>
            </div>
            <p class="text-xs text-slate-500 flex items-center gap-1.5 mt-0.5">
              <span>Phụ trách: <strong>Hà Trọng Phúc (PC11019)</strong></span>
              <span class="inline-block w-1 h-1 rounded-full bg-slate-300"></span>
              <span class="text-slate-400">Nhánh: <code>feature/salary-user-management</code></span>
            </p>
          </div>
        </div>

        <!-- Trạng Thái Kết Nối & Quick Links -->
        <div class="flex items-center gap-2.5">
          <div 
            class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold"
            :class="isBackendConnected ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-amber-50 text-amber-800 border border-amber-200'"
          >
            <span class="w-2 h-2 rounded-full" :class="isBackendConnected ? 'bg-emerald-500 animate-pulse' : 'bg-amber-500'"></span>
            <span>{{ isBackendConnected ? 'Backend API 8080 (Online)' : 'Chế Độ Thử Nghiệm (Mock Data)' }}</span>
          </div>

          <button 
            @click="refreshAllData"
            :disabled="loading"
            class="p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-xl transition-all active:scale-95 border border-slate-200/80 bg-white"
            title="Làm mới dữ liệu"
          >
            <svg class="w-4 h-4" :class="{ 'animate-spin': loading }" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Navigation Tabs (Chuyển Đổi Tab Chuyên Nghiệp) -->
      <div class="max-w-7xl mx-auto mt-3 border-t border-slate-100 pt-2 flex items-center gap-2 overflow-x-auto no-scrollbar">
        <button
          @click="activeTab = 'salaries'"
          class="flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all whitespace-nowrap"
          :class="activeTab === 'salaries' 
            ? 'bg-sky-600 text-white shadow-sm shadow-sky-600/30' 
            : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>Bảng Lương & Tính SpEL</span>
          <span 
            class="px-1.5 py-0.5 rounded-full text-[10px]"
            :class="activeTab === 'salaries' ? 'bg-white/20 text-white' : 'bg-slate-200 text-slate-700'"
          >
            {{ salaries.length }}
          </span>
        </button>

        <button
          @click="activeTab = 'users'"
          class="flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all whitespace-nowrap"
          :class="activeTab === 'users' 
            ? 'bg-sky-600 text-white shadow-sm shadow-sky-600/30' 
            : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
          <span>Quản Lý Nhân Sự (Users)</span>
          <span 
            class="px-1.5 py-0.5 rounded-full text-[10px]"
            :class="activeTab === 'users' ? 'bg-white/20 text-white' : 'bg-slate-200 text-slate-700'"
          >
            {{ users.length }}
          </span>
        </button>

        <button
          @click="activeTab = 'formula'"
          class="flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all whitespace-nowrap"
          :class="activeTab === 'formula' 
            ? 'bg-sky-600 text-white shadow-sm shadow-sky-600/30' 
            : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
          </svg>
          <span>Cấu Hình Công Thức & Hoa Hồng</span>
        </button>
      </div>
    </header>

    <!-- Nội dung chính Workspace -->
    <main class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-8 py-6">
      
      <!-- ========================================================================= -->
      <!-- TAB 1: BẢNG LƯƠNG & BỘ MÁY TÍNH SPEL ENGINE (CHỨC NĂNG CỐT LÕI)          -->
      <!-- ========================================================================= -->
      <section v-if="activeTab === 'salaries'" class="space-y-6">
        
        <!-- Banner Công Thức SpEL Kích Hoạt -->
        <div class="relative overflow-hidden bg-gradient-to-r from-slate-900 via-sky-950 to-slate-900 text-white p-5 sm:p-6 rounded-3xl shadow-lg border border-slate-800">
          <div class="relative z-10 flex flex-col lg:flex-row lg:items-center justify-between gap-4">
            <div>
              <div class="flex items-center gap-2 mb-2">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-sky-500/20 text-sky-300 border border-sky-400/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-sky-400 animate-ping"></span>
                  Spring SpEL Engine Đang Áp Dụng
                </span>
                <span class="text-xs text-slate-400 font-mono">{{ currentFormula.name }}</span>
              </div>
              <div class="font-mono text-sm sm:text-base font-bold text-amber-300 bg-black/40 px-3.5 py-2 rounded-xl inline-block border border-amber-300/30">
                {{ currentFormula.formulaExpression }}
              </div>
              <p class="text-xs text-slate-300 mt-2">
                💡 <span class="text-slate-400">Quy tắc tính:</span> 
                (Lương cơ bản / 26 × Ngày công) + (Hoa hồng gas × Hệ số tăng ca) + Thưởng/Phụ cấp
              </p>
            </div>

            <!-- Controls: Tháng & Nút Tính Lương -->
            <div class="flex flex-wrap items-center gap-3 bg-white/10 p-3 rounded-2xl backdrop-blur-md border border-white/10">
              <div>
                <label class="block text-[10px] uppercase font-bold text-slate-300 mb-1">Tháng Tính Lương</label>
                <input 
                  type="month" 
                  v-model="selectedMonth" 
                  @change="fetchSalaries"
                  class="bg-white text-slate-900 text-xs font-bold px-3 py-2 rounded-xl border-none focus:ring-2 focus:ring-sky-400 shadow-sm cursor-pointer"
                />
              </div>

              <button 
                @click="runCalculateSalary" 
                :disabled="calculating"
                class="inline-flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 active:scale-95 text-slate-950 font-black text-xs rounded-xl shadow-lg shadow-amber-500/25 transition-all mt-auto"
              >
                <svg v-if="!calculating" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                <svg v-else class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span>{{ calculating ? 'Đang Tính SpEL...' : 'Chạy Thuật Toán Tính Lương' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Metric Cards Thống Kê Nhanh Tháng -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Tổng Lương Chi Trả</span>
            <div class="text-xl sm:text-2xl font-black text-sky-600 tracking-tight">
              {{ formatCurrency(salaryMetrics.totalCalculatedSalary) }}
            </div>
            <p class="text-[11px] text-slate-500 mt-1">Đã tính theo công thức SpEL</p>
          </div>

          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Tổng Hoa Hồng Gas</span>
            <div class="text-xl sm:text-2xl font-black text-emerald-600 tracking-tight">
              {{ formatCurrency(salaryMetrics.totalCommissions) }}
            </div>
            <p class="text-[11px] text-slate-500 mt-1">Từ sản lượng shipper giao</p>
          </div>

          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Sản Lượng Bình Giao</span>
            <div class="text-xl sm:text-2xl font-black text-amber-500 tracking-tight">
              {{ salaryMetrics.totalDeliveredCylinders }} <span class="text-xs font-normal text-slate-500">bình</span>
            </div>
            <p class="text-[11px] text-slate-500 mt-1">Trong tháng {{ selectedMonth }}</p>
          </div>

          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Nhân Sự Được Tính</span>
            <div class="text-xl sm:text-2xl font-black text-slate-800 tracking-tight">
              {{ salaries.length }} <span class="text-xs font-normal text-slate-500">nhân viên</span>
            </div>
            <p class="text-[11px] text-slate-500 mt-1">Chốt lương tháng {{ selectedMonth }}</p>
          </div>
        </div>

        <!-- Bảng Dữ Liệu Lương Tháng -->
        <div class="bg-white rounded-3xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div class="p-4 sm:p-5 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-slate-50/50">
            <div>
              <h2 class="text-sm font-extrabold text-slate-900 uppercase tracking-wider">
                Bảng Lương Nhân Viên Tháng {{ selectedMonth }}
              </h2>
              <p class="text-xs text-slate-500 mt-0.5">
                Nhấn "Chấm công" để điều chỉnh ngày công thực tế, tăng ca hoặc thưởng thêm.
              </p>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs text-slate-500 font-medium">Lưu vết Audit: Bảng <code>monthly_salary_inputs</code></span>
            </div>
          </div>

          <div class="overflow-x-auto">
            <table class="w-full text-left text-xs">
              <thead class="bg-slate-100/70 text-slate-600 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200/70">
                <tr>
                  <th class="py-3 px-4">Nhân Viên</th>
                  <th class="py-3 px-4">Lương Cơ Bản</th>
                  <th class="py-3 px-4 text-center">Ngày Công (workDays)</th>
                  <th class="py-3 px-4 text-center">Tăng Ca (overtime)</th>
                  <th class="py-3 px-4">Thưởng Thêm (bonus)</th>
                  <th class="py-3 px-4">Hoa Hồng Gas (#commissionTotal)</th>
                  <th class="py-3 px-4 text-right">Lương Thực Nhận (SpEL)</th>
                  <th class="py-3 px-4 text-center">Thao Tác</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 text-slate-700">
                <tr v-if="salaries.length === 0">
                  <td colspan="8" class="text-center py-10 text-slate-400">
                    Chưa có dữ liệu bảng lương tháng {{ selectedMonth }}. Bấm "Chạy Thuật Toán Tính Lương" để tính ngay!
                  </td>
                </tr>
                <tr v-for="item in salaries" :key="item.id || item.employeeId" class="hover:bg-sky-50/40 transition-colors">
                  <!-- Nhân viên -->
                  <td class="py-3.5 px-4 font-medium">
                    <div class="flex items-center gap-2.5">
                      <div class="w-8 h-8 rounded-xl bg-gradient-to-br from-sky-500 to-indigo-600 text-white font-bold text-xs flex items-center justify-center flex-shrink-0 shadow-sm">
                        {{ getInitials(item.employeeName) }}
                      </div>
                      <div>
                        <div class="font-bold text-slate-900 text-xs">{{ item.employeeName }}</div>
                        <div class="text-[11px] text-slate-400">@{{ item.username }} • <span class="text-slate-500">{{ item.role }}</span></div>
                      </div>
                    </div>
                  </td>

                  <!-- Lương cơ bản -->
                  <td class="py-3.5 px-4 font-semibold text-slate-800">
                    {{ formatCurrency(item.baseSalary) }}
                  </td>

                  <!-- Ngày công -->
                  <td class="py-3.5 px-4 text-center">
                    <span class="inline-block px-2.5 py-1 rounded-lg font-bold text-xs" :class="item.workDays >= 26 ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'">
                      {{ item.workDays }} / 26
                    </span>
                  </td>

                  <!-- Tăng ca -->
                  <td class="py-3.5 px-4 text-center font-bold text-indigo-600">
                    x{{ item.overtime }}
                  </td>

                  <!-- Thưởng -->
                  <td class="py-3.5 px-4 font-semibold text-emerald-600">
                    +{{ formatCurrency(item.bonus) }}
                  </td>

                  <!-- Hoa hồng giao gas -->
                  <td class="py-3.5 px-4">
                    <div class="font-bold text-amber-600">{{ formatCurrency(item.commissionTotal) }}</div>
                    <div class="text-[10px] text-slate-400">({{ item.deliveredCylinders || 0 }} bình đã giao)</div>
                  </td>

                  <!-- Lương thực nhận -->
                  <td class="py-3.5 px-4 text-right">
                    <span class="inline-block px-3 py-1.5 rounded-xl font-black text-sm bg-sky-50 text-sky-700 border border-sky-200/80">
                      {{ formatCurrency(item.calculatedSalary) }}
                    </span>
                  </td>

                  <!-- Thao tác -->
                  <td class="py-3.5 px-4 text-center">
                    <div class="flex items-center justify-center gap-1.5">
                      <!-- Nút Sửa Chấm Công -->
                      <button 
                        @click="openEditSalaryModal(item)"
                        class="p-1.5 rounded-lg text-slate-600 hover:text-sky-600 hover:bg-sky-50 transition-colors"
                        title="Chỉnh sửa biến số chấm công (workDays, overtime, bonus)"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>

                      <!-- Nút Xem Phiếu Lương -->
                      <button 
                        @click="openPayslipModal(item)"
                        class="p-1.5 rounded-lg text-slate-600 hover:text-emerald-600 hover:bg-emerald-50 transition-colors"
                        title="Xem phiếu lương chi tiết & in ấn"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- ========================================================================= -->
      <!-- TAB 2: QUẢN LÝ NHÂN SỰ & NGƯỜI DÙNG (USERS / EMPLOYEES)                   -->
      <!-- ========================================================================= -->
      <section v-else-if="activeTab === 'users'" class="space-y-6">
        
        <!-- Quick Stats Nhân Sự -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Tổng Số Nhân Viên</span>
            <div class="text-2xl font-black text-slate-900 tracking-tight">{{ users.length }}</div>
            <p class="text-[11px] text-slate-500 mt-1">Toàn bộ nhân sự công ty</p>
          </div>

          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-emerald-600 uppercase tracking-wider block mb-1">Đang Làm Việc (ACTIVE)</span>
            <div class="text-2xl font-black text-emerald-600 tracking-tight">{{ activeUsersCount }}</div>
            <p class="text-[11px] text-slate-500 mt-1">Có thể tham gia giao gas & tính lương</p>
          </div>

          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-rose-500 uppercase tracking-wider block mb-1">Tạm Ngưng (INACTIVE)</span>
            <div class="text-2xl font-black text-rose-500 tracking-tight">{{ inactiveUsersCount }}</div>
            <p class="text-[11px] text-slate-500 mt-1">Đã khóa hoặc nghỉ việc</p>
          </div>

          <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm">
            <span class="text-[11px] font-bold text-sky-600 uppercase tracking-wider block mb-1">Tổng Quỹ Lương Cơ Bản</span>
            <div class="text-2xl font-black text-sky-700 tracking-tight">{{ formatCurrency(totalBaseSalary) }}</div>
            <p class="text-[11px] text-slate-500 mt-1">Định mức lương gốc hàng tháng</p>
          </div>
        </div>

        <!-- Thanh Tìm Kiếm, Bộ Lọc & Nút Thêm Mới -->
        <div class="bg-white p-4 rounded-3xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div class="flex flex-wrap items-center gap-2.5 flex-1">
            <!-- Ô tìm kiếm -->
            <div class="relative flex-1 min-w-[200px]">
              <svg class="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input 
                type="text" 
                v-model="userFilter.keyword" 
                placeholder="Tìm tên nhân viên, username, SĐT..."
                class="w-full text-xs pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500"
              />
            </div>

            <!-- Lọc vai trò -->
            <select 
              v-model="userFilter.role" 
              class="text-xs bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500 cursor-pointer"
            >
              <option value="">Tất Cả Vai Trò</option>
              <option value="ADMIN">Quản Trị (ADMIN)</option>
              <option value="EMPLOYEE">Nhân Viên (EMPLOYEE)</option>
            </select>

            <!-- Lọc trạng thái -->
            <select 
              v-model="userFilter.status" 
              class="text-xs bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-sky-500 cursor-pointer"
            >
              <option value="">Tất Cả Trạng Thái</option>
              <option value="ACTIVE">Đang làm việc (ACTIVE)</option>
              <option value="INACTIVE">Tạm ngưng (INACTIVE)</option>
            </select>
          </div>

          <!-- Nút Thêm Nhân Viên Mới -->
          <button 
            @click="openCreateUserModal"
            class="inline-flex items-center gap-2 px-4 py-2 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white font-bold text-xs rounded-xl shadow-sm shadow-sky-600/30 transition-all flex-shrink-0"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            <span>Thêm Nhân Viên</span>
          </button>
        </div>

        <!-- Bảng Danh Sách Nhân Viên -->
        <div class="bg-white rounded-3xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left text-xs">
              <thead class="bg-slate-100/70 text-slate-600 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200/70">
                <tr>
                  <th class="py-3 px-4">Nhân Viên</th>
                  <th class="py-3 px-4">Vai Trò</th>
                  <th class="py-3 px-4">Số Điện Thoại</th>
                  <th class="py-3 px-4">Ngày Bắt Đầu</th>
                  <th class="py-3 px-4">Lương Cơ Bản</th>
                  <th class="py-3 px-4 text-center">Trạng Thái (Toggle)</th>
                  <th class="py-3 px-4 text-center">Hành Động</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 text-slate-700">
                <tr v-if="filteredUsers.length === 0">
                  <td colspan="7" class="text-center py-10 text-slate-400">
                    Không tìm thấy nhân viên nào phù hợp với bộ lọc hiện tại.
                  </td>
                </tr>
                <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-slate-50 transition-colors">
                  <!-- Họ tên & Username -->
                  <td class="py-3.5 px-4 font-medium">
                    <div class="flex items-center gap-2.5">
                      <div class="w-8 h-8 rounded-xl bg-slate-800 text-white font-bold text-xs flex items-center justify-center flex-shrink-0">
                        {{ getInitials(user.name) }}
                      </div>
                      <div>
                        <div class="font-bold text-slate-900 text-xs">{{ user.name }}</div>
                        <div class="text-[11px] text-slate-400">@{{ user.username }}</div>
                      </div>
                    </div>
                  </td>

                  <!-- Vai trò -->
                  <td class="py-3.5 px-4">
                    <span 
                      class="px-2.5 py-1 rounded-full text-[10px] font-extrabold uppercase tracking-wide"
                      :class="user.role === 'ADMIN' ? 'bg-purple-50 text-purple-700 border border-purple-200' : 'bg-blue-50 text-blue-700 border border-blue-200'"
                    >
                      {{ user.role }}
                    </span>
                  </td>

                  <!-- Số điện thoại -->
                  <td class="py-3.5 px-4 font-mono text-slate-600">
                    {{ user.phone || 'Chưa cập nhật' }}
                  </td>

                  <!-- Ngày bắt đầu -->
                  <td class="py-3.5 px-4 text-slate-500">
                    {{ user.startDate || '—' }}
                  </td>

                  <!-- Lương cơ bản -->
                  <td class="py-3.5 px-4 font-bold text-slate-900">
                    {{ formatCurrency(user.baseSalary) }}
                  </td>

                  <!-- Toggle Trạng thái -->
                  <td class="py-3.5 px-4 text-center">
                    <button 
                      @click="requestToggleStatus(user)"
                      class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-[11px] font-bold transition-all active:scale-95 cursor-pointer"
                      :class="user.status === 'ACTIVE' 
                        ? 'bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100' 
                        : 'bg-rose-50 text-rose-700 border border-rose-200 hover:bg-rose-100'"
                      :title="'Bấm để chuyển sang ' + (user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')"
                    >
                      <span class="w-1.5 h-1.5 rounded-full" :class="user.status === 'ACTIVE' ? 'bg-emerald-500' : 'bg-rose-500'"></span>
                      <span>{{ user.status === 'ACTIVE' ? 'ACTIVE' : 'INACTIVE' }}</span>
                    </button>
                  </td>

                  <!-- Hành động -->
                  <td class="py-3.5 px-4 text-center">
                    <div class="flex items-center justify-center gap-1.5">
                      <!-- Nút Sửa -->
                      <button 
                        @click="openEditUserModal(user)"
                        class="p-1.5 rounded-lg text-slate-600 hover:text-sky-600 hover:bg-sky-50 transition-colors"
                        title="Sửa thông tin nhân viên"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>

                      <!-- Nút Xóa -->
                      <button 
                        @click="requestDeleteUser(user)"
                        class="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
                        title="Xóa nhân viên"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- ========================================================================= -->
      <!-- TAB 3: CẤU HÌNH CÔNG THỨC SPEL & ĐƠN GIÁ HOA HỒNG GAS                     -->
      <!-- ========================================================================= -->
      <section v-else-if="activeTab === 'formula'" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Chi Tiết Công Thức SpEL -->
          <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-sm space-y-4">
            <div class="flex items-center gap-2">
              <span class="text-xl">📐</span>
              <h3 class="text-sm font-extrabold text-slate-900 uppercase tracking-wider">
                Đặc Tả Công Thức Tính Lương Động SpEL
              </h3>
            </div>
            <p class="text-xs text-slate-600 leading-relaxed">
              Dự án sử dụng bộ máy <code>SpelExpressionParser</code> của Spring Framework. Khi có sự thay đổi quy chế lương qua các năm, chỉ cần cập nhật biểu thức trong bảng <code>salary_formulas</code> mà không cần viết lại mã nguồn backend.
            </p>
            <div class="bg-slate-900 text-slate-100 p-4 rounded-2xl font-mono text-xs space-y-1">
              <div class="text-amber-400 font-bold">// Biểu thức thực thi</div>
              <div>{{ currentFormula.formulaExpression }}</div>
            </div>
            <div class="space-y-2 text-xs">
              <div class="font-bold text-slate-800">Các biến số hệ thống đưa vào Context:</div>
              <ul class="list-disc pl-5 space-y-1 text-slate-600">
                <li><code>#baseSalary</code>: Lương cơ bản của nhân sự theo tháng</li>
                <li><code>#workDays</code>: Số ngày công thực tế (chuẩn 26 ngày)</li>
                <li><code>#commissionTotal</code>: Tổng tiền hoa hồng theo sản lượng bình gas shipper giao</li>
                <li><code>#overtime</code>: Hệ số làm thêm giờ / tăng ca (mặc định 1.0)</li>
                <li><code>#bonus</code>: Tiền thưởng chuyên cần, phụ cấp phát sinh</li>
              </ul>
            </div>
          </div>

          <!-- Bảng Cấu Hình Hoa Hồng Theo Loại Bình Gas -->
          <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-sm space-y-4">
            <div class="flex items-center gap-2">
              <span class="text-xl">🛢️</span>
              <h3 class="text-sm font-extrabold text-slate-900 uppercase tracking-wider">
                Đơn Giá Hoa Hồng Giao Gas (Bảng salary_configs)
              </h3>
            </div>
            <p class="text-xs text-slate-600 leading-relaxed">
              Hoa hồng được tính lũy tiến theo số lượng bình shipper đã giao thành công trong bảng <code>delivery_reports</code>.
            </p>
            <div class="divide-y divide-slate-100 border border-slate-100 rounded-2xl overflow-hidden text-xs">
              <div v-for="cfg in salaryConfigs" :key="cfg.containerType" class="p-3.5 flex items-center justify-between hover:bg-slate-50">
                <div>
                  <div class="font-bold text-slate-900">{{ cfg.containerType }}</div>
                  <div v-if="cfg.milestoneBonusThreshold" class="text-[11px] text-amber-600 mt-0.5">
                    Thưởng đạt mốc: ≥ {{ cfg.milestoneBonusThreshold }} bình (+{{ formatCurrency(cfg.milestoneBonusAmount) }})
                  </div>
                </div>
                <div class="text-right font-black text-emerald-600">
                  {{ formatCurrency(cfg.commissionPerUnit) }} / bình
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

    </main>

    <!-- ========================================================================= -->
    <!-- MODAL 1: THÊM MỚI / CHỈNH SỬA NHÂN VIÊN (BẮT LỖI FORM CHI TIẾT)           -->
    <!-- ========================================================================= -->
    <div v-if="showUserModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div class="bg-white w-full max-w-lg rounded-3xl shadow-2xl border border-slate-100 overflow-hidden transform transition-all">
        <div class="p-5 border-b border-slate-100 flex items-center justify-between bg-slate-50/70">
          <div>
            <h3 class="text-sm font-black text-slate-900">
              {{ isEditingUser ? 'Chỉnh Sửa Thông Tin Nhân Viên' : 'Thêm Mới Nhân Viên Công Ty' }}
            </h3>
            <p class="text-[11px] text-slate-500">Mọi thông tin đều được kiểm tra tính hợp lệ trước khi lưu</p>
          </div>
          <button @click="showUserModal = false" class="text-slate-400 hover:text-slate-600 p-1 rounded-xl">✕</button>
        </div>

        <form @submit.prevent="saveUser" class="p-5 space-y-3.5 text-xs">
          <!-- Họ tên -->
          <div>
            <label class="block font-bold text-slate-700 mb-1">
              Họ và Tên Nhân Viên <span class="text-rose-500">*</span>
            </label>
            <input 
              type="text" 
              v-model="userForm.name" 
              @input="clearUserError('name')"
              placeholder="Ví dụ: Nguyễn Văn Giao"
              class="w-full px-3.5 py-2.5 rounded-xl border outline-none transition-all"
              :class="userErrors.name 
                ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
            />
            <p v-if="userErrors.name" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
              <span>⚠️</span> <span>{{ userErrors.name }}</span>
            </p>
          </div>

          <!-- Username & Password -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">
                Tên Đăng Nhập (Username) <span class="text-rose-500">*</span>
              </label>
              <input 
                type="text" 
                v-model="userForm.username" 
                :disabled="isEditingUser"
                @input="clearUserError('username')"
                placeholder="shipper01"
                class="w-full px-3.5 py-2.5 rounded-xl border outline-none transition-all disabled:bg-slate-100 disabled:text-slate-400"
                :class="userErrors.username 
                  ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                  : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
              />
              <p v-if="userErrors.username" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
                <span>⚠️</span> <span>{{ userErrors.username }}</span>
              </p>
            </div>

            <div>
              <label class="block font-bold text-slate-700 mb-1">
                {{ isEditingUser ? 'Mật Khẩu Mới (nếu muốn đổi)' : 'Mật Khẩu Khởi Tạo *' }}
              </label>
              <input 
                type="password" 
                v-model="userForm.password" 
                @input="clearUserError('password')"
                placeholder="Tối thiểu 6 ký tự"
                class="w-full px-3.5 py-2.5 rounded-xl border outline-none transition-all"
                :class="userErrors.password 
                  ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                  : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
              />
              <p v-if="userErrors.password" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
                <span>⚠️</span> <span>{{ userErrors.password }}</span>
              </p>
            </div>
          </div>

          <!-- Số điện thoại & Lương cơ bản -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">
                Số Điện Thoại <span class="text-rose-500">*</span>
              </label>
              <input 
                type="tel" 
                v-model="userForm.phone" 
                @input="clearUserError('phone')"
                placeholder="0987654321"
                class="w-full px-3.5 py-2.5 rounded-xl border outline-none transition-all font-mono"
                :class="userErrors.phone 
                  ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                  : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
              />
              <p v-if="userErrors.phone" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
                <span>⚠️</span> <span>{{ userErrors.phone }}</span>
              </p>
            </div>

            <div>
              <label class="block font-bold text-slate-700 mb-1">
                Lương Cơ Bản (VNĐ) <span class="text-rose-500">*</span>
              </label>
              <input 
                type="number" 
                v-model.number="userForm.baseSalary" 
                @input="clearUserError('baseSalary')"
                min="0"
                step="100000"
                class="w-full px-3.5 py-2.5 rounded-xl border outline-none transition-all font-bold text-sky-700"
                :class="userErrors.baseSalary 
                  ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                  : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
              />
              <p v-if="userErrors.baseSalary" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
                <span>⚠️</span> <span>{{ userErrors.baseSalary }}</span>
              </p>
            </div>
          </div>

          <!-- Vai trò & Ngày bắt đầu -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">Vai Trò (Role)</label>
              <select 
                v-model="userForm.role"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-sky-500 outline-none font-medium cursor-pointer"
              >
                <option value="EMPLOYEE">Nhân Viên Giao Hàng (EMPLOYEE)</option>
                <option value="ADMIN">Quản Trị Viên (ADMIN)</option>
              </select>
            </div>

            <div>
              <label class="block font-bold text-slate-700 mb-1">
                Ngày Bắt Đầu Làm Việc <span class="text-rose-500">*</span>
              </label>
              <input 
                type="date" 
                v-model="userForm.startDate" 
                @input="clearUserError('startDate')"
                class="w-full px-3.5 py-2.5 rounded-xl border outline-none cursor-pointer"
                :class="userErrors.startDate 
                  ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                  : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
              />
              <p v-if="userErrors.startDate" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
                <span>⚠️</span> <span>{{ userErrors.startDate }}</span>
              </p>
            </div>
          </div>

          <!-- Actions -->
          <div class="pt-4 border-t border-slate-100 flex items-center justify-end gap-2.5">
            <button 
              type="button" 
              @click="showUserModal = false"
              class="px-4 py-2.5 text-slate-600 hover:bg-slate-100 font-bold rounded-xl transition-all cursor-pointer"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="saving"
              class="px-5 py-2.5 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white font-bold rounded-xl shadow-md shadow-sky-600/30 transition-all flex items-center gap-1.5 cursor-pointer disabled:opacity-50"
            >
              <span>{{ saving ? 'Đang Lưu...' : 'Lưu Dữ Liệu' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ========================================================================= -->
    <!-- MODAL 2: CẬP NHẬT BIẾN SỐ CHẤM CÔNG (BẮT LỖI WORKDAYS / OVERTIME / BONUS)  -->
    <!-- ========================================================================= -->
    <div v-if="showSalaryModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div class="bg-white w-full max-w-md rounded-3xl shadow-2xl border border-slate-100 overflow-hidden transform transition-all">
        <div class="p-5 border-b border-slate-100 flex items-center justify-between bg-sky-50/50">
          <div>
            <h3 class="text-sm font-black text-slate-900">Điều Chỉnh Biến Số Chấm Công</h3>
            <p class="text-[11px] text-slate-500">Nhân viên: <strong>{{ selectedSalaryItem?.employeeName }}</strong> (Tháng {{ selectedMonth }})</p>
          </div>
          <button @click="showSalaryModal = false" class="text-slate-400 hover:text-slate-600 p-1 rounded-xl">✕</button>
        </div>

        <form @submit.prevent="saveSalaryInput" class="p-5 space-y-4 text-xs">
          <!-- Ngày công thực tế -->
          <div>
            <div class="flex items-center justify-between mb-1">
              <label class="font-bold text-slate-700">
                Số Ngày Công Thực Tế (workDays) <span class="text-rose-500">*</span>
              </label>
              <span class="text-[10px] text-slate-400">Chuẩn: 26 (0.0 - 31.0 ngày)</span>
            </div>
            <input 
              type="number" 
              v-model.number="salaryInputForm.workDays" 
              @input="clearSalaryError('workDays')"
              min="0" 
              max="31" 
              step="0.5" 
              class="w-full px-3.5 py-2.5 rounded-xl border outline-none font-bold text-sm text-slate-900"
              :class="salaryErrors.workDays 
                ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
            />
            <p v-if="salaryErrors.workDays" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
              <span>⚠️</span> <span>{{ salaryErrors.workDays }}</span>
            </p>
          </div>

          <!-- Hệ số tăng ca -->
          <div>
            <div class="flex items-center justify-between mb-1">
              <label class="font-bold text-slate-700">
                Hệ Số Tăng Ca (overtime) <span class="text-rose-500">*</span>
              </label>
              <span class="text-[10px] text-slate-400">Từ 1.0 đến 3.0</span>
            </div>
            <input 
              type="number" 
              v-model.number="salaryInputForm.overtime" 
              @input="clearSalaryError('overtime')"
              min="1.0" 
              max="3.0" 
              step="0.1" 
              class="w-full px-3.5 py-2.5 rounded-xl border outline-none font-bold text-sm text-indigo-600"
              :class="salaryErrors.overtime 
                ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
            />
            <p v-if="salaryErrors.overtime" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
              <span>⚠️</span> <span>{{ salaryErrors.overtime }}</span>
            </p>
          </div>

          <!-- Thưởng thêm -->
          <div>
            <div class="flex items-center justify-between mb-1">
              <label class="font-bold text-slate-700">Thưởng Thêm / Phụ Cấp (bonus)</label>
              <span class="text-[10px] text-slate-400">Đơn vị: VNĐ (≥ 0)</span>
            </div>
            <input 
              type="number" 
              v-model.number="salaryInputForm.bonus" 
              @input="clearSalaryError('bonus')"
              min="0" 
              step="50000" 
              class="w-full px-3.5 py-2.5 rounded-xl border outline-none font-bold text-sm text-emerald-600"
              :class="salaryErrors.bonus 
                ? 'bg-rose-50/30 border-rose-400 focus:ring-2 focus:ring-rose-400' 
                : 'bg-slate-50 border-slate-200 focus:bg-white focus:ring-2 focus:ring-sky-500'"
            />
            <p v-if="salaryErrors.bonus" class="text-[11px] text-rose-500 mt-1 font-semibold flex items-center gap-1">
              <span>⚠️</span> <span>{{ salaryErrors.bonus }}</span>
            </p>
          </div>

          <!-- Live SpEL Preview -->
          <div class="p-3 bg-slate-50 rounded-2xl border border-slate-200/80 space-y-1">
            <div class="text-[10px] uppercase font-bold text-slate-400">Dự Tính Lương Thực Nhận (Live SpEL)</div>
            <div class="text-base font-black text-sky-700">
              {{ formatCurrency(previewCalculatedSalary) }}
            </div>
          </div>

          <!-- Actions -->
          <div class="pt-4 border-t border-slate-100 flex items-center justify-end gap-2.5">
            <button 
              type="button" 
              @click="showSalaryModal = false"
              class="px-4 py-2.5 text-slate-600 hover:bg-slate-100 font-bold rounded-xl transition-all cursor-pointer"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="saving"
              class="px-5 py-2.5 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white font-bold rounded-xl shadow-md shadow-sky-600/30 transition-all flex items-center gap-1.5 cursor-pointer disabled:opacity-50"
            >
              <span>{{ saving ? 'Đang Cập Nhật...' : 'Lưu Biến Số' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ========================================================================= -->
    <!-- MODAL 3: XEM VÀ IN PHIẾU LƯƠNG CHI TIẾT (Payslip Modal)                    -->
    <!-- ========================================================================= -->
    <div v-if="showPayslipModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div class="bg-white w-full max-w-lg rounded-3xl shadow-2xl border border-slate-100 overflow-hidden transform transition-all">
        <!-- Header Phiếu Lương -->
        <div class="p-6 border-b border-slate-100 bg-gradient-to-r from-sky-600 to-sky-700 text-white flex items-center justify-between">
          <div>
            <div class="text-[11px] uppercase tracking-widest font-black text-sky-200">OriginGas Enterprise</div>
            <h3 class="text-lg font-black mt-0.5">PHIẾU LƯƠNG NHÂN VIÊN</h3>
            <p class="text-xs text-sky-100">Kỳ tính lương: Tháng {{ selectedMonth }}</p>
          </div>
          <button @click="showPayslipModal = false" class="text-white/80 hover:text-white p-1 rounded-xl text-lg font-bold cursor-pointer">✕</button>
        </div>

        <!-- Chi tiết các khoản cấu thành lương -->
        <div class="p-6 space-y-4 text-xs">
          <!-- Thông tin nhân viên -->
          <div class="grid grid-cols-2 gap-2 pb-3 border-b border-slate-100">
            <div>
              <span class="text-slate-400 block text-[11px]">Họ và Tên:</span>
              <strong class="text-slate-900 text-sm">{{ selectedPayslip?.employeeName }}</strong>
            </div>
            <div>
              <span class="text-slate-400 block text-[11px]">Chức Vụ:</span>
              <strong class="text-slate-900">{{ selectedPayslip?.role }} (@{{ selectedPayslip?.username }})</strong>
            </div>
          </div>

          <!-- Bảng kê các khoản -->
          <div class="space-y-2.5">
            <div class="flex items-center justify-between py-1">
              <span class="text-slate-600">1. Lương Cơ Bản Hợp Đồng:</span>
              <span class="font-bold text-slate-900">{{ formatCurrency(selectedPayslip?.baseSalary) }}</span>
            </div>

            <div class="flex items-center justify-between py-1 text-slate-600">
              <span>2. Số ngày công thực tế ({{ selectedPayslip?.workDays }} / 26 ngày):</span>
              <span class="font-bold text-slate-800">
                {{ formatCurrency((selectedPayslip?.baseSalary / 26) * selectedPayslip?.workDays) }}
              </span>
            </div>

            <div class="flex items-center justify-between py-1 text-slate-600">
              <div>
                <span>3. Hoa hồng sản lượng bình gas:</span>
                <span class="block text-[10px] text-slate-400">
                  ({{ selectedPayslip?.deliveredCylinders || 0 }} bình × đơn giá hoa hồng × tăng ca {{ selectedPayslip?.overtime }})
                </span>
              </div>
              <span class="font-bold text-emerald-600">
                +{{ formatCurrency((selectedPayslip?.commissionTotal || 0) * (selectedPayslip?.overtime || 1)) }}
              </span>
            </div>

            <div class="flex items-center justify-between py-1 text-slate-600">
              <span>4. Tiền thưởng thêm / Phụ cấp chuyên cần:</span>
              <span class="font-bold text-emerald-600">+{{ formatCurrency(selectedPayslip?.bonus) }}</span>
            </div>
          </div>

          <!-- Tổng thực nhận -->
          <div class="pt-4 border-t border-dashed border-slate-300 flex items-center justify-between bg-sky-50 -mx-6 -mb-6 p-6">
            <div>
              <span class="text-[11px] font-bold text-sky-800 uppercase tracking-wider block">TỔNG LƯƠNG THỰC LĨNH</span>
              <span class="text-[10px] text-slate-500">Tính theo công thức SpEL chuẩn</span>
            </div>
            <div class="text-xl font-black text-sky-700">
              {{ formatCurrency(selectedPayslip?.calculatedSalary) }}
            </div>
          </div>
        </div>

        <!-- Footer Actions -->
        <div class="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-end gap-2.5">
          <button 
            type="button" 
            @click="printPayslip"
            class="px-4 py-2 bg-slate-800 hover:bg-slate-900 text-white font-bold text-xs rounded-xl transition-all inline-flex items-center gap-1.5 cursor-pointer"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
            </svg>
            <span>In Phiếu Lương</span>
          </button>
          <button 
            type="button" 
            @click="showPayslipModal = false"
            class="px-4 py-2 bg-white border border-slate-200 text-slate-700 font-bold text-xs rounded-xl hover:bg-slate-100 transition-all cursor-pointer"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>

    <!-- ========================================================================= -->
    <!-- MODAL 4: HỘP THOẠI XÁC NHẬN AN TOÀN (CUSTOM CONFIRM MODAL)                 -->
    <!-- ========================================================================= -->
    <div v-if="confirmDialog.show" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div class="bg-white w-full max-w-sm rounded-3xl shadow-2xl border border-slate-100 overflow-hidden p-5 space-y-4">
        <div class="flex items-center gap-3">
          <div 
            class="w-10 h-10 rounded-2xl flex items-center justify-center text-lg flex-shrink-0"
            :class="confirmDialog.isDanger ? 'bg-rose-50 text-rose-600 border border-rose-200' : 'bg-sky-50 text-sky-600 border border-sky-200'"
          >
            {{ confirmDialog.isDanger ? '⚠️' : '❓' }}
          </div>
          <div>
            <h4 class="text-sm font-black text-slate-900">{{ confirmDialog.title }}</h4>
            <p class="text-xs text-slate-500 mt-0.5 leading-relaxed">{{ confirmDialog.message }}</p>
          </div>
        </div>

        <div class="pt-2 flex items-center justify-end gap-2.5">
          <button 
            @click="confirmDialog.show = false"
            class="px-4 py-2 text-slate-600 hover:bg-slate-100 font-bold text-xs rounded-xl transition-all cursor-pointer"
          >
            Hủy Bỏ
          </button>
          <button 
            @click="handleConfirmAction"
            class="px-4 py-2 text-white font-bold text-xs rounded-xl shadow-md transition-all cursor-pointer"
            :class="confirmDialog.isDanger ? 'bg-rose-600 hover:bg-rose-700 shadow-rose-600/30' : 'bg-sky-600 hover:bg-sky-700 shadow-sky-600/30'"
          >
            {{ confirmDialog.confirmText || 'Đồng Ý' }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { userService } from '../services/userService'
import { salaryService } from '../services/salaryService'

// Tab điều hướng
const activeTab = ref('salaries') // 'salaries' | 'users' | 'formula'

// Trạng thái chung
const loading = ref(false)
const calculating = ref(false)
const saving = ref(false)
const isBackendConnected = ref(false)

// Dữ liệu
const users = ref([])
const salaries = ref([])
const salaryConfigs = ref([])
const currentFormula = ref({
  name: 'Công thức lương chuẩn OriginGas 2026',
  formulaExpression: '(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus'
})

// Bộ lọc
const selectedMonth = ref('2026-11')
const userFilter = ref({
  keyword: '',
  role: '',
  status: ''
})

// Modals state
const showUserModal = ref(false)
const isEditingUser = ref(false)
const userForm = ref({
  id: null,
  name: '',
  username: '',
  password: '',
  phone: '',
  startDate: '',
  role: 'EMPLOYEE',
  status: 'ACTIVE',
  baseSalary: 7000000
})

// Bắt lỗi Form Nhân Viên
const userErrors = ref({
  name: '',
  username: '',
  password: '',
  phone: '',
  baseSalary: '',
  startDate: ''
})

const showSalaryModal = ref(false)
const selectedSalaryItem = ref(null)
const salaryInputForm = ref({
  workDays: 26.0,
  overtime: 1.0,
  bonus: 0
})

// Bắt lỗi Form Chấm Công
const salaryErrors = ref({
  workDays: '',
  overtime: '',
  bonus: ''
})

const showPayslipModal = ref(false)
const selectedPayslip = ref(null)

// Toast Notifications System
const toasts = ref([])
let toastCounter = 0

const showToast = (title, message, type = 'info', duration = 3500) => {
  const id = ++toastCounter
  toasts.value.push({ id, title, message, type })
  setTimeout(() => {
    removeToast(id)
  }, duration)
}

const removeToast = (id) => {
  const idx = toasts.value.findIndex(t => t.id === id)
  if (idx !== -1) {
    toasts.value.splice(idx, 1)
  }
}

// Custom Confirm Dialog
const confirmDialog = ref({
  show: false,
  title: '',
  message: '',
  confirmText: '',
  isDanger: false,
  action: null
})

const openConfirm = (title, message, action, isDanger = false, confirmText = 'Đồng Ý') => {
  confirmDialog.value = {
    show: true,
    title,
    message,
    confirmText,
    isDanger,
    action
  }
}

const handleConfirmAction = async () => {
  if (confirmDialog.value.action) {
    await confirmDialog.value.action()
  }
  confirmDialog.value.show = false
}

// Xóa lỗi khi gõ lại
const clearUserError = (field) => {
  if (userErrors.value[field]) {
    userErrors.value[field] = ''
  }
}

const clearSalaryError = (field) => {
  if (salaryErrors.value[field]) {
    salaryErrors.value[field] = ''
  }
}

// Computed cho Tab Nhân Sự
const filteredUsers = computed(() => {
  let list = users.value
  if (userFilter.value.role) {
    list = list.filter(u => u.role === userFilter.value.role)
  }
  if (userFilter.value.status) {
    list = list.filter(u => u.status === userFilter.value.status)
  }
  if (userFilter.value.keyword && userFilter.value.keyword.trim()) {
    const kw = userFilter.value.keyword.toLowerCase().trim()
    list = list.filter(u => 
      (u.name && u.name.toLowerCase().includes(kw)) ||
      (u.username && u.username.toLowerCase().includes(kw)) ||
      (u.phone && u.phone.includes(kw))
    )
  }
  return list
})

const activeUsersCount = computed(() => users.value.filter(u => u.status === 'ACTIVE').length)
const inactiveUsersCount = computed(() => users.value.filter(u => u.status === 'INACTIVE').length)
const totalBaseSalary = computed(() => users.value.reduce((acc, u) => acc + (u.baseSalary || 0), 0))

// Computed cho Tab Lương
const salaryMetrics = computed(() => {
  const totalCalculatedSalary = salaries.value.reduce((acc, s) => acc + (s.calculatedSalary || 0), 0)
  const totalCommissions = salaries.value.reduce((acc, s) => acc + (s.commissionTotal || 0), 0)
  const totalDeliveredCylinders = salaries.value.reduce((acc, s) => acc + (s.deliveredCylinders || 0), 0)
  return {
    totalCalculatedSalary,
    totalCommissions,
    totalDeliveredCylinders
  }
})

// Live preview tính lương khi sửa biến số
const previewCalculatedSalary = computed(() => {
  if (!selectedSalaryItem.value) return 0
  const base = selectedSalaryItem.value.baseSalary || 0
  const days = salaryInputForm.value.workDays || 0
  const comm = selectedSalaryItem.value.commissionTotal || 0
  const ot = salaryInputForm.value.overtime || 1.0
  const bon = salaryInputForm.value.bonus || 0

  return Math.round((base / 26 * days) + (comm * ot) + bon)
})

// Khởi tạo nạp dữ liệu
onMounted(async () => {
  await refreshAllData()
})

const refreshAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      fetchUsers(),
      fetchSalaries(),
      fetchFormulaAndConfigs()
    ])
  } catch (err) {
    showToast('Lỗi Tải Dữ Liệu', err.message || 'Không thể đồng bộ dữ liệu hệ thống', 'error')
  } finally {
    loading.value = false
  }
}

const fetchUsers = async () => {
  const res = await userService.getUsers()
  users.value = res.data || []
  isBackendConnected.value = !res.isMock
}

const fetchSalaries = async () => {
  const res = await salaryService.getSalariesByMonth(selectedMonth.value)
  salaries.value = res.data || []
}

const fetchFormulaAndConfigs = async () => {
  const [fRes, cRes] = await Promise.all([
    salaryService.getActiveFormula(),
    salaryService.getSalaryConfigs()
  ])
  if (fRes.data) currentFormula.value = fRes.data
  if (cRes.data) salaryConfigs.value = cRes.data
}

// Chạy thuật toán tính lương SpEL
const runCalculateSalary = async () => {
  calculating.value = true
  try {
    const res = await salaryService.calculateSalary(selectedMonth.value)
    salaries.value = res.data || []
    showToast(
      'Tính Lương Thành Công', 
      `Bộ máy SpEL đã tính xong bảng lương tháng ${selectedMonth.value}!`, 
      'success'
    )
  } catch (err) {
    showToast('Lỗi Tính Lương SpEL', err.message || 'Không thể thực thi tính lương', 'error')
  } finally {
    calculating.value = false
  }
}

// Mở modal điều chỉnh biến số chấm công
const openEditSalaryModal = (item) => {
  selectedSalaryItem.value = item
  salaryInputForm.value = {
    workDays: item.workDays ?? 26.0,
    overtime: item.overtime ?? 1.0,
    bonus: item.bonus ?? 0
  }
  salaryErrors.value = {
    workDays: '',
    overtime: '',
    bonus: ''
  }
  showSalaryModal.value = true
}

// BẮT LỖI BIẾN SỐ CHẤM CÔNG (workDays, overtime, bonus)
const validateSalaryInput = () => {
  let isValid = true
  salaryErrors.value = { workDays: '', overtime: '', bonus: '' }

  const days = salaryInputForm.value.workDays
  if (days === null || days === undefined || isNaN(days)) {
    salaryErrors.value.workDays = 'Vui lòng nhập số ngày công thực tế'
    isValid = false
  } else if (days < 0 || days > 31) {
    salaryErrors.value.workDays = 'Số ngày công phải nằm trong khoảng từ 0 đến 31 ngày'
    isValid = false
  }

  const ot = salaryInputForm.value.overtime
  if (ot === null || ot === undefined || isNaN(ot)) {
    salaryErrors.value.overtime = 'Vui lòng nhập hệ số tăng ca'
    isValid = false
  } else if (ot < 1.0 || ot > 3.0) {
    salaryErrors.value.overtime = 'Hệ số tăng ca quy định từ 1.0 đến tối đa 3.0'
    isValid = false
  }

  const b = salaryInputForm.value.bonus
  if (b === null || b === undefined || isNaN(b)) {
    salaryErrors.value.bonus = 'Vui lòng nhập số tiền thưởng thêm (hoặc nhập 0)'
    isValid = false
  } else if (b < 0) {
    salaryErrors.value.bonus = 'Tiền thưởng/phụ cấp không được là số âm'
    isValid = false
  }

  return isValid
}

// Lưu biến số chấm công
const saveSalaryInput = async () => {
  if (!validateSalaryInput()) {
    showToast('Dữ Liệu Chưa Hợp Lệ', 'Vui lòng kiểm tra lại các trường biến số chấm công bị báo đỏ!', 'warning')
    return
  }

  saving.value = true
  try {
    await salaryService.updateSalaryInput(
      selectedSalaryItem.value.employeeId,
      selectedMonth.value,
      salaryInputForm.value
    )
    showSalaryModal.value = false
    await fetchSalaries()
    showToast(
      'Cập Nhật Thành Công', 
      `Đã cập nhật biến số và tính lại lương cho ${selectedSalaryItem.value.employeeName}!`, 
      'success'
    )
  } catch (err) {
    showToast('Lỗi Cập Nhật', err.message || 'Không thể lưu biến số chấm công', 'error')
  } finally {
    saving.value = false
  }
}

// Mở modal phiếu lương
const openPayslipModal = (item) => {
  selectedPayslip.value = item
  showPayslipModal.value = true
}

const printPayslip = () => {
  window.print()
}

// Quản lý nhân viên
const openCreateUserModal = () => {
  isEditingUser.value = false
  userForm.value = {
    id: null,
    name: '',
    username: '',
    password: '',
    phone: '',
    startDate: new Date().toISOString().split('T')[0],
    role: 'EMPLOYEE',
    status: 'ACTIVE',
    baseSalary: 7000000
  }
  userErrors.value = {
    name: '',
    username: '',
    password: '',
    phone: '',
    baseSalary: '',
    startDate: ''
  }
  showUserModal.value = true
}

const openEditUserModal = (user) => {
  isEditingUser.value = true
  userForm.value = {
    ...user,
    password: ''
  }
  userErrors.value = {
    name: '',
    username: '',
    password: '',
    phone: '',
    baseSalary: '',
    startDate: ''
  }
  showUserModal.value = true
}

// BẮT LỖI FORM NHÂN VIÊN TOÀN DIỆN
const validateUserForm = () => {
  let isValid = true
  userErrors.value = {
    name: '',
    username: '',
    password: '',
    phone: '',
    baseSalary: '',
    startDate: ''
  }

  // 1. Kiểm tra Họ và Tên
  if (!userForm.value.name || !userForm.value.name.trim()) {
    userErrors.value.name = 'Họ và tên nhân viên không được để trống'
    isValid = false
  } else if (userForm.value.name.trim().length < 2) {
    userErrors.value.name = 'Họ và tên phải có ít nhất 2 ký tự'
    isValid = false
  }

  // 2. Kiểm tra Username
  if (!isEditingUser.value) {
    const username = userForm.value.username ? userForm.value.username.trim() : ''
    const usernameRegex = /^[a-zA-Z0-9_.-]+$/
    if (!username) {
      userErrors.value.username = 'Tên đăng nhập không được để trống'
      isValid = false
    } else if (username.length < 3 || username.length > 30) {
      userErrors.value.username = 'Tên đăng nhập phải từ 3 đến 30 ký tự'
      isValid = false
    } else if (!usernameRegex.test(username)) {
      userErrors.value.username = 'Chỉ cho phép chữ cái không dấu, số, dấu gạch dưới (_) hoặc gạch ngang (-)'
      isValid = false
    } else {
      // Kiểm tra trùng username trong danh sách
      const isDuplicate = users.value.some(
        u => u.username && u.username.toLowerCase() === username.toLowerCase()
      )
      if (isDuplicate) {
        userErrors.value.username = 'Tên đăng nhập này đã được sử dụng trong hệ thống!'
        isValid = false
      }
    }
  }

  // 3. Kiểm tra Mật Khẩu
  if (!isEditingUser.value) {
    if (!userForm.value.password || userForm.value.password.length < 6) {
      userErrors.value.password = 'Mật khẩu khởi tạo phải có tối thiểu 6 ký tự'
      isValid = false
    }
  } else if (userForm.value.password && userForm.value.password.length < 6) {
    userErrors.value.password = 'Mật khẩu mới phải có tối thiểu 6 ký tự'
    isValid = false
  }

  // 4. Kiểm tra Số Điện Thoại (Chuẩn SĐT Việt Nam: 10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09)
  const phoneRegex = /^(0[3|5|7|8|9])[0-9]{8}$/
  const phone = userForm.value.phone ? userForm.value.phone.trim() : ''
  if (!phone) {
    userErrors.value.phone = 'Số điện thoại không được để trống'
    isValid = false
  } else if (!phoneRegex.test(phone)) {
    userErrors.value.phone = 'SĐT không hợp lệ! Phải là 10 số bắt đầu bằng 03, 05, 07, 08 hoặc 09'
    isValid = false
  }

  // 5. Kiểm tra Lương Cơ Bản
  const sal = userForm.value.baseSalary
  if (sal === null || sal === undefined || isNaN(sal)) {
    userErrors.value.baseSalary = 'Vui lòng nhập định mức lương cơ bản'
    isValid = false
  } else if (sal < 0) {
    userErrors.value.baseSalary = 'Lương cơ bản không được âm'
    isValid = false
  } else if (sal > 500000000) {
    userErrors.value.baseSalary = 'Số tiền lương cơ bản vượt quá giới hạn cho phép'
    isValid = false
  }

  // 6. Kiểm tra Ngày Bắt Đầu Làm Việc
  if (!userForm.value.startDate) {
    userErrors.value.startDate = 'Vui lòng chọn ngày bắt đầu làm việc'
    isValid = false
  }

  return isValid
}

const saveUser = async () => {
  if (!validateUserForm()) {
    showToast('Thông Tin Chưa Hợp Lệ', 'Vui lòng kiểm tra và sửa các trường bị báo lỗi màu đỏ!', 'warning')
    return
  }

  saving.value = true
  try {
    if (isEditingUser.value) {
      await userService.updateUser(userForm.value.id, userForm.value)
      showToast('Cập Nhật Thành Công', `Đã cập nhật thông tin cho ${userForm.value.name}!`, 'success')
    } else {
      await userService.createUser(userForm.value)
      showToast('Thêm Mới Thành Công', `Đã tạo nhân viên ${userForm.value.name} (@${userForm.value.username})!`, 'success')
    }
    showUserModal.value = false
    await fetchUsers()
    await fetchSalaries()
  } catch (err) {
    showToast('Lỗi Lưu Nhân Viên', err.message || 'Không thể lưu thông tin nhân sự', 'error')
  } finally {
    saving.value = false
  }
}

// Xác nhận đổi trạng thái
const requestToggleStatus = (user) => {
  const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const isDeactivating = newStatus === 'INACTIVE'

  openConfirm(
    'Xác Nhận Đổi Trạng Thái',
    `Bạn có chắc chắn muốn chuyển trạng thái nhân viên "${user.name}" sang ${newStatus}?` +
    (isDeactivating ? ' Nhân viên sẽ tạm ngưng giao gas và không được tính lương tự động.' : ''),
    async () => {
      try {
        await userService.toggleStatus(user.id)
        await fetchUsers()
        showToast('Đổi Trạng Thái Thành Công', `Nhân viên ${user.name} hiện đang ở trạng thái ${newStatus}!`, 'success')
      } catch (err) {
        showToast('Lỗi Đổi Trạng Thái', err.message || 'Không thể đổi trạng thái nhân viên', 'error')
      }
    },
    isDeactivating,
    isDeactivating ? 'Tạm Ngưng' : 'Kích Hoạt'
  )
}

// Xác nhận xóa nhân viên
const requestDeleteUser = (user) => {
  openConfirm(
    'Xác Nhận Xóa Nhân Viên',
    `Cảnh báo: Hành động này sẽ xóa vĩnh viễn nhân viên "${user.name}" (@${user.username}) khỏi hệ thống. Bạn có chắc chắn không?`,
    async () => {
      try {
        await userService.deleteUser(user.id)
        await fetchUsers()
        showToast('Đã Xóa Nhân Viên', `Đã xóa nhân viên ${user.name} thành công.`, 'success')
      } catch (err) {
        showToast('Lỗi Xóa Nhân Viên', err.message || 'Không thể xóa nhân viên', 'error')
      }
    },
    true,
    'Xóa Vĩnh Viễn'
  )
}

// Helper format tiền tệ VNĐ
const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

// Helper lấy chữ cái đầu
const getInitials = (name) => {
  if (!name) return 'NV'
  const parts = name.trim().split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
  }
  return name.slice(0, 2).toUpperCase()
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
