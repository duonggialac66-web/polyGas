<template>
  <EmployeeLayout>
    <!-- Header -->
    <div class="flex items-center justify-between mb-5">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2 h-2 rounded-full bg-sky-500"></span>
          <h2 class="text-xl font-extrabold tracking-tight text-slate-900">Danh Bạ Khách Hàng</h2>
        </div>
        <p class="text-xs text-slate-500 mt-0.5">Thêm khách hàng mới, ghim tọa độ định vị GPS giao hàng</p>
      </div>

      <button 
        @click="alertNewCustomer"
        class="inline-flex items-center gap-1.5 px-4 py-2 bg-sky-600 hover:bg-sky-700 active:scale-95 text-white text-xs font-bold rounded-xl shadow-sm transition-all"
      >
        <span>➕ Thêm Khách Mới</span>
      </button>
    </div>

    <!-- Features Overview Card -->
    <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs mb-5">
      <h3 class="text-sm font-bold text-slate-900 mb-2">Chức Năng Dành Cho Nhân Viên Giao Hàng:</h3>
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div class="p-3 bg-slate-50 rounded-xl border border-slate-100">
          <div class="text-lg mb-1">📍</div>
          <div class="text-xs font-bold text-slate-800">Ghim Định Vị GPS</div>
          <div class="text-[11px] text-slate-500 mt-0.5">Lưu tọa độ vị trí bếp/nhà của khách hàng để lần sau chỉ đường nhanh.</div>
        </div>
        <div class="p-3 bg-slate-50 rounded-xl border border-slate-100">
          <div class="text-lg mb-1">👤</div>
          <div class="text-xs font-bold text-slate-800">Thêm Khách Hàng</div>
          <div class="text-[11px] text-slate-500 mt-0.5">Nhập số điện thoại, tên quán, địa chỉ khi có khách mới gọi gas.</div>
        </div>
        <div class="p-3 bg-slate-50 rounded-xl border border-slate-100">
          <div class="text-lg mb-1">📩</div>
          <div class="text-xs font-bold text-slate-800">Xin Admin Sửa Thông Tin</div>
          <div class="text-[11px] text-slate-500 mt-0.5">Gửi yêu cầu điều chỉnh tên hoặc đổi chủ quán khi có thay đổi.</div>
        </div>
      </div>
    </div>

    <!-- Sample Customers List -->
    <div class="space-y-2.5">
      <div v-for="c in sampleCustomers" :key="c.id" class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
        <div>
          <div class="text-sm font-bold text-slate-900">{{ c.name }}</div>
          <div class="text-xs text-slate-500 mt-0.5">{{ c.phone }} • {{ c.address }}</div>
        </div>
        <button class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition-all flex items-center gap-1">
          <span>📍</span>
          <span>Xem Vị Trí</span>
        </button>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'

const sampleCustomers = [
  { id: 1, name: 'Quán Ăn Cô Ba', phone: '0908 123 456', address: '128 Nguyễn Trãi, P.3, Q.5' },
  { id: 2, name: 'Bún Bò Huế 36', phone: '0933 789 101', address: '36 Lê Lợi, P. Bến Nghé, Q.1' },
  { id: 3, name: 'Anh Nam (Chung cư Flora)', phone: '0912 345 678', address: 'Phòng 502, Tháp B, Chung cư Flora' }
]

const alertNewCustomer = () => {
  alert('Chức năng thêm khách hàng và lấy tọa độ GPS đang được thành viên phụ trách hoàn thiện!')
}
</script>

