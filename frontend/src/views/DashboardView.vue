<template>
  <EmployeeLayout>
    <!-- Top Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2.5 h-2.5 rounded-full bg-slate-400"></span>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">Tổng Quan Hệ Thống</h2>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Trung tâm điều hành và điều hướng các phân hệ nghiệp vụ công ty</p>
      </div>

      <div class="flex items-center gap-2">
        <span class="text-xs text-slate-500 font-medium">Phiên bản: <strong class="text-slate-800">2026 Enterprise</strong></span>
      </div>
    </div>

    <!-- Quick Metrics -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-3.5 mb-6">
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-xs">
        <div class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">Nhân Sự</div>
        <div class="text-lg font-bold text-slate-900">Quản Lý & Phân Quyền</div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-xs">
        <div class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">Kho Bãi</div>
        <div class="text-lg font-bold text-slate-900">Tồn Kho & Cảnh Báo</div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-xs">
        <div class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">Giao Vận</div>
        <div class="text-lg font-bold text-slate-900">Sổ Nợ & Quyết Toán</div>
      </div>

      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-xs">
        <div class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">Lương Thưởng</div>
        <div class="text-lg font-bold text-slate-900">Công Thức SpEL</div>
      </div>
    </div>

    <!-- Navigation Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div class="bg-white border border-slate-200 rounded-xl p-5 shadow-xs flex flex-col justify-between">
        <div>
          <h3 class="text-sm font-bold text-slate-900 mb-1.5 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-slate-700"></span>
            Phân Hệ Giao Gas & Thu Tiền
          </h3>
          <p class="text-xs text-slate-600 leading-relaxed mb-4">
            Theo dõi danh sách các đơn giao trong ngày, kiểm tra công nợ khách hàng, số lượng bình giao và đổi trạng thái thanh toán.
          </p>
        </div>
        <div>
          <router-link 
            to="/employment" 
            class="inline-flex items-center gap-1.5 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg transition-colors"
          >
            <span>Vào Phân Hệ Giao Gas</span>
            <span>&rarr;</span>
          </router-link>
        </div>
      </div>

      <div class="bg-white border border-slate-200 rounded-xl p-5 shadow-xs flex flex-col justify-between">
        <div>
          <h3 class="text-sm font-bold text-slate-900 mb-1.5 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-slate-700"></span>
            Danh Bạ Khách Hàng & Bản Đồ GPS
          </h3>
          <p class="text-xs text-slate-600 leading-relaxed mb-4">
            Quản lý cơ sở dữ liệu khách hàng, lưu tọa độ GPS vị trí giao gas để dẫn đường cho tài xế và nhân viên.
          </p>
        </div>
        <div>
          <router-link 
            to="/customers" 
            class="inline-flex items-center gap-1.5 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg transition-colors"
          >
            <span>Xem Danh Bạ Khách</span>
            <span>&rarr;</span>
          </router-link>
        </div>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
</script>
