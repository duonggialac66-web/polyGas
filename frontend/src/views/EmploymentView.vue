<template>
  <EmployeeLayout :debt-count="debtBillsCount" @refresh="fetchReports">
    <!-- Header Section (Corporate Enterprise) -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-slate-200">
      <div>
        <h2 class="text-lg font-bold text-slate-900 tracking-tight">Báo Cáo Giao Gas Trong Ngày</h2>
        <p class="text-xs text-slate-500 mt-0.5">Quản lý các đợt giao bình gas, hoàn trả vỏ rỗng, công nợ và thu tiền ca trực</p>
      </div>

      <div class="flex items-center gap-2">
        <!-- Date Selector -->
        <input 
          type="date" 
          v-model="selectedDate" 
          @change="fetchReports"
          class="text-xs font-semibold text-slate-700 bg-white border border-slate-300 rounded-lg px-3 py-1.5 shadow-2xs focus:outline-none focus:border-slate-800 transition-colors cursor-pointer"
        />

        <!-- Đối Soát Cuối Ca Button -->
        <button 
          @click="openReconciliationModal"
          class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
          title="Xem số liệu đối soát tiền mặt shipper phải bàn giao lại cho thủ quỹ két"
        >
          <span>📋 Đối Soát Cuối Ca</span>
        </button>

        <!-- Lịch Sử Giao Hàng Button -->
        <button 
          @click="openHistoryModal"
          class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-white text-slate-700 hover:bg-slate-50 border border-slate-300 text-xs font-semibold rounded-lg shadow-2xs transition-colors"
          title="Tra cứu lịch sử giao gas theo khoảng ngày"
        >
          <span>🕒 Lịch Sử</span>
        </button>

        <!-- Action CTA -->
        <button 
          @click="openCreateModal"
          class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg shadow-2xs transition-colors"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          <span>Nhập Phiếu Giao</span>
        </button>
      </div>
    </div>

    <!-- 5 KPI Summary Cards (Corporate Clean) -->
    <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3.5 mb-6">
      <!-- 1. Total Cylinders Delivered -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-1">Bình Đã Giao</div>
        <div class="text-xl font-bold text-slate-900">
          {{ stats.totalQuantity }} <span class="text-xs font-normal text-slate-500">bình</span>
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Sản lượng xuất ca hôm nay</div>
      </div>

      <!-- 2. Total Turnover -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-1">Tổng Doanh Thu</div>
        <div class="text-lg font-bold text-slate-900">
          {{ formatCurrency(stats.totalAmount) }}
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Tổng giá trị đơn hàng</div>
      </div>

      <!-- 3. Paid Amount -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-emerald-600 uppercase mb-1">Thực Thu Tiền</div>
        <div class="text-lg font-bold text-emerald-600">
          {{ formatCurrency(stats.paidAmount) }}
        </div>
        <div class="text-[10px] text-slate-400 mt-1">Tiền mặt shipper đã thu</div>
      </div>

      <!-- 4. Debt Amount -->
      <div class="bg-white border border-slate-200 p-4 rounded-xl shadow-2xs">
        <div class="text-[11px] font-semibold tracking-wider text-rose-600 uppercase mb-1">Khách Còn Nợ</div>
        <div class="text-lg font-bold text-rose-600">
          {{ formatCurrency(stats.debtAmount) }}
        </div>
        <div class="text-[10px] text-rose-500 mt-1">{{ debtBillsCount }} đơn chưa thanh toán đủ</div>
      </div>

      <!-- 5. Debt Shells (Nợ vỏ gas) -->
      <div class="col-span-2 sm:col-span-1 bg-white border border-slate-200 p-4 rounded-xl shadow-2xs" :class="stats.debtShells > 0 ? 'border-amber-300 bg-amber-50/20' : ''">
        <div class="text-[11px] font-semibold tracking-wider text-amber-700 uppercase mb-1">Vỏ Khách Nợ</div>
        <div class="text-xl font-bold" :class="stats.debtShells > 0 ? 'text-amber-700' : 'text-slate-900'">
          {{ stats.debtShells }} <span class="text-xs font-normal text-slate-500">vỏ</span>
        </div>
        <div class="text-[10px] text-amber-600 mt-1">
          {{ stats.debtShells > 0 ? 'Cần thu hồi về kho' : 'Đã thu đủ 100% vỏ' }}
        </div>
      </div>
    </div>

    <!-- Search & Filter Bar -->
    <div class="bg-white p-3 border border-slate-200 rounded-xl mb-4 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between">
      <!-- Search -->
      <div class="relative flex-1">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="Tìm theo tên khách, loại gas, ghi chú..." 
          class="w-full pl-9 pr-3 py-1.5 text-xs text-slate-800 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
        />
        <svg class="w-4 h-4 text-slate-400 absolute left-2.5 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>

      <!-- Filter status -->
      <div class="flex items-center gap-1.5 text-xs">
        <button 
          @click="statusFilter = 'ALL'"
          class="px-2.5 py-1 rounded-md font-medium transition-colors"
          :class="statusFilter === 'ALL' ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Tất Cả ({{ reports.length }})
        </button>
        <button 
          @click="statusFilter = 'PAID'"
          class="px-2.5 py-1 rounded-md font-medium transition-colors"
          :class="statusFilter === 'PAID' ? 'bg-emerald-600 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Đã Thu
        </button>
        <button 
          @click="statusFilter = 'DEBT'"
          class="px-2.5 py-1 rounded-md font-medium transition-colors"
          :class="statusFilter === 'DEBT' ? 'bg-rose-600 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Nợ Tiền ({{ debtBillsCount }})
        </button>
        <button 
          @click="statusFilter = 'DEBT_SHELLS'"
          class="px-2.5 py-1 rounded-md font-medium transition-colors"
          :class="statusFilter === 'DEBT_SHELLS' ? 'bg-amber-600 text-white' : 'text-slate-600 hover:bg-slate-100'"
        >
          Nợ Vỏ
        </button>
      </div>
    </div>

    <!-- Data Table of Delivery Reports -->
    <div class="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-2xs mb-6">
      <div v-if="loading" class="p-8 text-center text-xs text-slate-500">
        Đang tải danh sách giao gas...
      </div>

      <div v-else-if="filteredReports.length === 0" class="p-8 text-center text-xs text-slate-500">
        Không tìm thấy phiếu giao gas nào phù hợp điều kiện lọc.
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left border-collapse text-xs">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-[11px] font-semibold text-slate-600 uppercase tracking-wider">
              <th class="py-2.5 px-3">Khách Hàng</th>
              <th class="py-2.5 px-3">Loại Gas</th>
              <th class="py-2.5 px-3 text-center">SL Giao / Trả Vỏ</th>
              <th class="py-2.5 px-3 text-right">Đơn Giá</th>
              <th class="py-2.5 px-3 text-right">Thành Tiền</th>
              <th class="py-2.5 px-3 text-right">Thực Thu</th>
              <th class="py-2.5 px-3 text-center">Tiền Mặt</th>
              <th class="py-2.5 px-3 text-center">Vỏ Bình</th>
              <th class="py-2.5 px-3">Ghi Chú</th>
              <th class="py-2.5 px-3 text-right">Thao Tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="item in filteredReports" :key="item.id" class="hover:bg-slate-50/80 transition-colors">
              <!-- Khách Hàng -->
              <td class="py-3 px-3">
                <div class="font-bold text-slate-900">{{ item.customerName }}</div>
                <div class="text-[10px] text-slate-400 font-mono">{{ item.id }}</div>
              </td>

              <!-- Loại Gas -->
              <td class="py-3 px-3">
                <span class="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-700">
                  {{ item.containerType }}
                </span>
              </td>

              <!-- SL Giao / Trả Vỏ -->
              <td class="py-3 px-3 text-center">
                <div class="font-bold text-slate-900">{{ item.quantity }} bình</div>
                <div class="text-[10px] text-slate-500">
                  Trả: <span class="font-semibold">{{ item.returnedShells !== undefined ? item.returnedShells : item.quantity }}</span> / {{ item.quantity }}
                </div>
              </td>

              <!-- Đơn Giá -->
              <td class="py-3 px-3 text-right font-mono text-slate-600">
                {{ formatCurrency(item.unitPrice) }}
              </td>

              <!-- Thành Tiền -->
              <td class="py-3 px-3 text-right font-mono font-bold text-slate-900">
                {{ formatCurrency(item.total) }}
              </td>

              <!-- Thực Thu -->
              <td class="py-3 px-3 text-right font-mono font-bold text-emerald-600">
                {{ formatCurrency(item.actualReceived !== undefined ? item.actualReceived : (item.paymentStatus === 'PAID' ? item.total : 0)) }}
              </td>

              <!-- Trạng thái Tiền Mặt (Toggle Button) -->
              <td class="py-3 px-3 text-center">
                <button 
                  @click="handleTogglePayment(item)"
                  class="px-2.5 py-1 rounded-md text-[10px] font-bold border transition-colors"
                  :class="item.paymentStatus === 'PAID' 
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100' 
                    : 'bg-rose-50 text-rose-700 border-rose-200 hover:bg-rose-100'"
                  title="Nhấn để đổi trạng thái thanh toán"
                >
                  {{ item.paymentStatus === 'PAID' ? 'ĐÃ THU' : 'GHI NỢ' }}
                </button>
              </td>

              <!-- Trạng thái Vỏ Bình (Có nút Hoàn trả vỏ nếu nợ) -->
              <td class="py-3 px-3 text-center">
                <div v-if="item.debtShells && item.debtShells > 0" class="flex flex-col items-center gap-1">
                  <span class="inline-block px-2 py-0.5 rounded text-[10px] font-bold bg-amber-50 text-amber-700 border border-amber-200">
                    Nợ {{ item.debtShells }} vỏ
                  </span>
                  <button 
                    @click="handleReturnShellsQuick(item)"
                    class="text-[10px] text-indigo-600 hover:text-indigo-800 font-semibold underline"
                    title="Khách đem trả lại vỏ rỗng"
                  >
                    Thu hồi vỏ
                  </button>
                </div>
                <div v-else>
                  <span class="inline-block px-2 py-0.5 rounded text-[10px] font-medium bg-slate-100 text-slate-600">
                    Đủ vỏ
                  </span>
                </div>
              </td>

              <!-- Ghi Chú -->
              <td class="py-3 px-3 text-slate-500 max-w-[160px] truncate" :title="item.notes">
                {{ item.notes || '—' }}
              </td>

              <!-- Thao Tác (Sửa / Xóa) -->
              <td class="py-3 px-3 text-right whitespace-nowrap">
                <button 
                  @click="openEditModal(item)"
                  class="text-slate-600 hover:text-slate-900 font-medium px-1.5 py-1 rounded hover:bg-slate-100 transition-colors mr-1"
                >
                  Sửa
                </button>
                <button 
                  @click="handleDeleteReport(item.id)"
                  class="text-rose-600 hover:text-rose-800 font-medium px-1.5 py-1 rounded hover:bg-rose-50 transition-colors"
                >
                  Xóa
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create / Edit Modal Dialog (Corporate Clean) -->
    <div 
      v-if="modal.isOpen" 
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs"
    >
      <div class="bg-white rounded-xl shadow-xl border border-slate-200 max-w-lg w-full p-6 overflow-hidden">
        <div class="flex items-center justify-between pb-3 border-b border-slate-100 mb-4">
          <h3 class="text-sm font-bold text-slate-900 uppercase tracking-tight">
            {{ modal.isEdit ? 'Chỉnh Sửa Phiếu Giao Gas' : 'Nhập Phiếu Giao Gas Mới' }}
          </h3>
          <button @click="closeModal" class="text-slate-400 hover:text-slate-600 text-lg leading-none">
            ✕
          </button>
        </div>

        <div v-if="formError" class="mb-4 p-2.5 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg">
          {{ formError }}
        </div>

        <form @submit.prevent="submitForm" class="space-y-3.5 text-xs">
          <!-- Tên Khách Hàng -->
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Tên Khách Hàng / Điểm Giao *</label>
            <input 
              type="text" 
              v-model="form.customerName" 
              required
              placeholder="VD: Quán Ăn Cô Ba, Anh Nam - Flora..."
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            />
          </div>

          <!-- Loại Gas & Tồn Kho Hiện Tại -->
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Loại Bình Gas Giao *</label>
            <select 
              v-model="form.containerType" 
              @change="onGasTypeChange"
              class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
            >
              <option v-for="g in gasTypeOptions" :key="g.type" :value="g.type">
                {{ g.type }} — {{ formatCurrency(g.defaultPrice) }} 
                {{ g.stock !== undefined ? `(Còn trong kho: ${g.stock} bình)` : '' }}
              </option>
            </select>
          </div>

          <!-- Số Lượng Bình & Số Vỏ Trả Lại -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Số Lượng Giao *</label>
              <input 
                type="number" 
                v-model.number="form.quantity" 
                min="1" 
                required
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              />
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Vỏ Khách Trả Ngay</label>
              <input 
                type="number" 
                v-model.number="form.returnedShells" 
                min="0"
                :max="form.quantity"
                :placeholder="form.quantity ? `${form.quantity} (Trả đủ)` : '0'"
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              />
            </div>
          </div>

          <!-- Đơn Giá & Thành Tiền -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Đơn Giá (VNĐ) *</label>
              <input 
                type="number" 
                v-model.number="form.unitPrice" 
                min="0" 
                step="1000"
                required
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              />
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Tổng Tiền Đơn</label>
              <div class="px-3 py-2 bg-slate-100 border border-slate-200 rounded-lg font-bold font-mono text-slate-800">
                {{ formatCurrency(computedTotal) }}
              </div>
            </div>
          </div>

          <!-- Thực Thu & Trạng Thái -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Thực Thu Tiền Mặt</label>
              <input 
                type="number" 
                v-model.number="form.actualReceived" 
                min="0" 
                step="1000"
                :placeholder="computedTotal ? String(computedTotal) : '0'"
                class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors"
              />
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Tình Trạng Nợ</label>
              <div 
                class="px-3 py-2 rounded-lg font-bold text-center border"
                :class="(form.actualReceived >= computedTotal) ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-rose-50 text-rose-700 border-rose-200'"
              >
                {{ (form.actualReceived >= computedTotal) ? 'Đã Thanh Toán Đủ' : `Khách Ghi Nợ ${formatCurrency(Math.max(0, computedTotal - (form.actualReceived || 0)))}` }}
              </div>
            </div>
          </div>

          <!-- Ghi Chú -->
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Ghi Chú Đơn Hàng</label>
            <textarea 
              v-model="form.notes" 
              rows="2"
              placeholder="VD: Khách hẹn chiều trả nợ vỏ, giao lầu 2..."
              class="w-full px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg focus:bg-white focus:outline-none focus:border-slate-800 transition-colors resize-none"
            ></textarea>
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-end gap-2 pt-3 border-t border-slate-100">
            <button 
              type="button" 
              @click="closeModal"
              class="px-3.5 py-1.5 text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-lg font-semibold transition-colors"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              :disabled="submitting"
              class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 disabled:opacity-50 text-white rounded-lg font-semibold shadow-2xs transition-colors"
            >
              {{ submitting ? 'Đang lưu...' : (modal.isEdit ? 'Cập Nhật' : 'Tạo Phiếu') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- MODAL 1: ĐỐI SOÁT TIỀN MẶT CUỐI CA CỦA SHIPPER -->
    <div 
      v-if="reconciliationModal.isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs animate-fade-in"
    >
      <div class="bg-white rounded-2xl border border-slate-200 shadow-xl w-full max-w-lg overflow-hidden animate-scale-up">
        <!-- Header -->
        <div class="p-4 bg-slate-900 text-white flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">📋</span>
            <div>
              <h3 class="text-sm font-bold">Biên Bản Đối Soát Nộp Tiền Cuối Ca</h3>
              <p class="text-[11px] text-slate-300">Ngày: {{ selectedDate }} • {{ reconciliationModal.data?.employeeName || 'Shipper' }}</p>
            </div>
          </div>
          <button @click="reconciliationModal.isOpen = false" class="text-slate-400 hover:text-white text-lg font-bold">✕</button>
        </div>

        <div v-if="reconciliationModal.loading" class="p-8 text-center text-xs text-slate-500">
          <div class="inline-block w-6 h-6 border-2 border-slate-300 border-t-slate-800 rounded-full animate-spin mb-2"></div>
          <div>Đang tính toán số liệu đối soát...</div>
        </div>

        <div v-else class="p-5 space-y-4">
          <!-- Main Highlight: Cash to Handover -->
          <div class="bg-emerald-50 border border-emerald-200 rounded-xl p-4 text-center">
            <div class="text-[11px] font-bold text-emerald-700 uppercase tracking-wider mb-1">
              Tiền Mặt Shipper Phải Nộp Lại Cho Két / Thủ Quỹ
            </div>
            <div class="text-2xl font-black text-emerald-700 font-mono tracking-tight">
              {{ formatCurrency(reconciliationModal.data?.totalCashToHandover) }}
            </div>
            <div class="text-[11px] text-emerald-600 mt-1">
              (Tổng tiền mặt thực tế đã thu từ khách hàng trong ngày hôm nay)
            </div>
          </div>

          <!-- Breakdown Metrics Grid -->
          <div class="grid grid-cols-2 gap-3">
            <div class="p-3 bg-slate-50 rounded-xl border border-slate-200">
              <div class="text-[10px] text-slate-500 uppercase font-semibold">Tổng Số Đơn Đã Giao</div>
              <div class="text-base font-bold text-slate-900 mt-0.5">
                {{ reconciliationModal.data?.totalOrders || 0 }} <span class="text-xs font-normal text-slate-500">phiếu</span>
              </div>
            </div>

            <div class="p-3 bg-slate-50 rounded-xl border border-slate-200">
              <div class="text-[10px] text-slate-500 uppercase font-semibold">Tổng Bình Gas Đã Giao</div>
              <div class="text-base font-bold text-slate-900 mt-0.5">
                {{ reconciliationModal.data?.totalCylinders || 0 }} <span class="text-xs font-normal text-slate-500">bình</span>
              </div>
            </div>

            <div class="p-3 bg-slate-50 rounded-xl border border-slate-200">
              <div class="text-[10px] text-slate-500 uppercase font-semibold">Tổng Doanh Số Đơn</div>
              <div class="text-base font-bold text-slate-900 mt-0.5 font-mono">
                {{ formatCurrency(reconciliationModal.data?.totalSalesAmount) }}
              </div>
            </div>

            <div class="p-3 bg-rose-50 rounded-xl border border-rose-200">
              <div class="text-[10px] text-rose-600 uppercase font-semibold">Khách Chưa Trả (Ghi Nợ)</div>
              <div class="text-base font-bold text-rose-600 mt-0.5 font-mono">
                {{ formatCurrency(reconciliationModal.data?.totalRemainingDebt) }}
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-between pt-3 border-t border-slate-100">
            <button 
              @click="printReconciliation" 
              class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg flex items-center gap-1.5"
            >
              <span>🖨️</span>
              <span>In Phiếu Nộp Tiền</span>
            </button>
            <button 
              @click="reconciliationModal.isOpen = false"
              class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL 2: TRA CỨU LỊCH SỬ GIAO HÀNG THEO KHOẢNG NGÀY -->
    <div 
      v-if="historyModal.isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs animate-fade-in"
    >
      <div class="bg-white rounded-2xl border border-slate-200 shadow-xl w-full max-w-2xl max-h-[90vh] flex flex-col overflow-hidden animate-scale-up">
        <!-- Header -->
        <div class="p-4 bg-slate-900 text-white flex items-center justify-between flex-shrink-0">
          <div class="flex items-center gap-2">
            <span class="text-lg">🕒</span>
            <div>
              <h3 class="text-sm font-bold">Tra Cứu Lịch Sử Báo Cáo Giao Hàng</h3>
              <p class="text-[11px] text-slate-300">Truy vấn toàn bộ phiếu giao gas theo dải ngày</p>
            </div>
          </div>
          <button @click="historyModal.isOpen = false" class="text-slate-400 hover:text-white text-lg font-bold">✕</button>
        </div>

        <!-- Filter bar -->
        <div class="p-3 bg-slate-50 border-b border-slate-200 flex flex-wrap items-center gap-3 flex-shrink-0">
          <div class="flex items-center gap-1.5 text-xs">
            <span class="text-slate-500 font-semibold">Từ:</span>
            <input 
              type="date" 
              v-model="historyModal.fromDate" 
              class="px-2 py-1 bg-white border border-slate-300 rounded text-xs font-semibold"
            />
          </div>
          <div class="flex items-center gap-1.5 text-xs">
            <span class="text-slate-500 font-semibold">Đến:</span>
            <input 
              type="date" 
              v-model="historyModal.toDate" 
              class="px-2 py-1 bg-white border border-slate-300 rounded text-xs font-semibold"
            />
          </div>
          <button 
            @click="fetchHistory" 
            :disabled="historyModal.loading"
            class="px-3 py-1 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded shadow-2xs disabled:opacity-50"
          >
            {{ historyModal.loading ? 'Đang tải...' : 'Lọc Dữ Liệu' }}
          </button>
        </div>

        <!-- Content list -->
        <div class="p-4 overflow-y-auto flex-1 space-y-2">
          <div v-if="historyModal.loading" class="p-8 text-center text-xs text-slate-500">
            <div class="inline-block w-6 h-6 border-2 border-slate-300 border-t-slate-800 rounded-full animate-spin mb-2"></div>
            <div>Đang tải lịch sử giao hàng...</div>
          </div>

          <div v-else-if="historyModal.data.length === 0" class="p-8 text-center text-xs text-slate-500">
            Không có phiếu giao hàng nào trong khoảng thời gian đã chọn.
          </div>

          <div 
            v-else 
            v-for="h in historyModal.data" 
            :key="h.id"
            class="p-3 bg-white border border-slate-200 rounded-xl hover:border-slate-300 flex items-center justify-between text-xs"
          >
            <div>
              <div class="font-bold text-slate-900 flex items-center gap-2">
                <span>{{ h.customerName }}</span>
                <span class="px-1.5 py-0.5 bg-slate-100 rounded text-[10px] text-slate-600 font-normal">
                  {{ h.containerType }} (x{{ h.quantity }})
                </span>
              </div>
              <div class="text-slate-500 text-[11px] mt-0.5">
                Ngày: <span class="font-medium text-slate-700">{{ h.reportDate }}</span>
                <span class="mx-1">•</span>
                Shipper: <span class="font-medium text-slate-700">{{ h.employeeName || 'Nhân viên' }}</span>
              </div>
            </div>

            <div class="text-right">
              <div class="font-bold text-slate-900 font-mono">{{ formatCurrency(h.total) }}</div>
              <span 
                class="inline-block px-1.5 py-0.2 rounded text-[10px] font-semibold mt-0.5"
                :class="h.paymentStatus === 'PAID' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-rose-50 text-rose-700 border border-rose-200'"
              >
                {{ h.paymentStatus === 'PAID' ? 'Đã Thanh Toán' : 'Ghi Nợ' }}
              </span>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-3 bg-slate-50 border-t border-slate-200 flex items-center justify-between text-xs flex-shrink-0">
          <span class="text-slate-500">Tìm thấy <strong>{{ historyModal.data.length }}</strong> phiếu giao gas</span>
          <button 
            @click="historyModal.isOpen = false"
            class="px-3 py-1 bg-slate-200 hover:bg-slate-300 text-slate-800 font-semibold rounded"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  </EmployeeLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import EmployeeLayout from '../components/layout/EmployeeLayout.vue'
import { deliveryReportService, GAS_TYPES } from '../services/deliveryReportService.js'
import { inventoryService } from '../services/inventoryService.js'

const selectedDate = ref(new Date().toISOString().split('T')[0])
const reports = ref([])
const inventories = ref([])
const loading = ref(false)
const submitting = ref(false)
const formError = ref('')

const searchKeyword = ref('')
const statusFilter = ref('ALL') // 'ALL', 'PAID', 'DEBT', 'DEBT_SHELLS'

const modal = reactive({
  isOpen: false,
  isEdit: false,
  editId: null
})

const form = reactive({
  customerName: '',
  containerType: GAS_TYPES[0].type,
  quantity: 1,
  returnedShells: 1,
  unitPrice: GAS_TYPES[0].defaultPrice,
  actualReceived: GAS_TYPES[0].defaultPrice,
  notes: ''
})

const computedTotal = computed(() => {
  return (Number(form.quantity) || 0) * (Number(form.unitPrice) || 0)
})

const gasTypeOptions = computed(() => {
  return GAS_TYPES.map(g => {
    const inv = inventories.value.find(i => i.containerType === g.type)
    return {
      ...g,
      stock: inv ? inv.fullQuantity : undefined
    }
  })
})

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

const debtBillsCount = computed(() => {
  return reports.value.filter(r => r.paymentStatus === 'DEBT').length
})

const filteredReports = computed(() => {
  return reports.value.filter(r => {
    // Lọc theo từ khóa
    if (searchKeyword.value) {
      const kw = searchKeyword.value.toLowerCase()
      const matchName = r.customerName && r.customerName.toLowerCase().includes(kw)
      const matchType = r.containerType && r.containerType.toLowerCase().includes(kw)
      const matchNotes = r.notes && r.notes.toLowerCase().includes(kw)
      if (!matchName && !matchType && !matchNotes) return false
    }

    // Lọc theo trạng thái
    if (statusFilter.value === 'PAID') return r.paymentStatus === 'PAID'
    if (statusFilter.value === 'DEBT') return r.paymentStatus === 'DEBT'
    if (statusFilter.value === 'DEBT_SHELLS') return r.debtShells && r.debtShells > 0

    return true
  })
})

function formatCurrency(amount) {
  if (amount === undefined || amount === null) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

function onGasTypeChange() {
  const selected = GAS_TYPES.find(g => g.type === form.containerType)
  if (selected) {
    form.unitPrice = selected.defaultPrice
    form.actualReceived = computedTotal.value
  }
}

async function fetchReports() {
  loading.value = true
  try {
    const [repRes, invRes] = await Promise.all([
      deliveryReportService.getTodayReports(selectedDate.value),
      inventoryService.getInventories().catch(() => ({ data: [] }))
    ])
    reports.value = repRes.data || []
    inventories.value = invRes.data || []
  } catch (err) {
    console.error('Lỗi nạp danh sách báo cáo:', err)
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  formError.value = ''
  modal.isOpen = true
  modal.isEdit = false
  modal.editId = null
  form.customerName = ''
  form.containerType = GAS_TYPES[0].type
  form.quantity = 1
  form.returnedShells = 1
  form.unitPrice = GAS_TYPES[0].defaultPrice
  form.actualReceived = GAS_TYPES[0].defaultPrice
  form.notes = ''
}

function openEditModal(item) {
  formError.value = ''
  modal.isOpen = true
  modal.isEdit = true
  modal.editId = item.id
  form.customerName = item.customerName
  form.containerType = item.containerType
  form.quantity = item.quantity
  form.returnedShells = item.returnedShells !== undefined ? item.returnedShells : item.quantity
  form.unitPrice = item.unitPrice
  form.actualReceived = item.actualReceived !== undefined ? item.actualReceived : item.total
  form.notes = item.notes || ''
}

function closeModal() {
  modal.isOpen = false
  formError.value = ''
}

async function submitForm() {
  formError.value = ''
  submitting.value = true
  try {
    const payload = {
      customerName: form.customerName,
      containerType: form.containerType,
      quantity: form.quantity,
      returnedShells: form.returnedShells,
      unitPrice: form.unitPrice,
      actualReceived: form.actualReceived !== undefined ? form.actualReceived : computedTotal.value,
      notes: form.notes,
      reportDate: selectedDate.value
    }

    if (modal.isEdit) {
      await deliveryReportService.updateReport(modal.editId, payload)
    } else {
      await deliveryReportService.createReport(payload)
    }

    closeModal()
    await fetchReports()
  } catch (err) {
    formError.value = err.message || 'Thao tác không thành công!'
  } finally {
    submitting.value = false
  }
}

async function handleTogglePayment(item) {
  try {
    await deliveryReportService.togglePaymentStatus(item.id)
    await fetchReports()
  } catch (err) {
    console.error('Lỗi đổi trạng thái:', err)
  }
}

async function handleReturnShellsQuick(item) {
  if (confirm(`Xác nhận khách hàng [${item.customerName}] đã hoàn trả đủ ${item.debtShells} vỏ bình gas còn thiếu? Hệ thống sẽ ghi nhận và cộng vỏ rỗng vào kho.`)) {
    try {
      await deliveryReportService.returnShells(item.id, item.debtShells)
      await fetchReports()
    } catch (err) {
      alert('Lỗi hoàn trả vỏ: ' + err.message)
    }
  }
}

async function handleDeleteReport(id) {
  if (confirm('Bạn có chắc chắn muốn xóa phiếu giao gas này?')) {
    try {
      await deliveryReportService.deleteReport(id)
      await fetchReports()
    } catch (err) {
      alert('Lỗi khi xóa phiếu: ' + err.message)
    }
  }
}

// State Đối Soát Cuối Ca
const reconciliationModal = reactive({
  isOpen: false,
  loading: false,
  data: null
})

// State Tra Cứu Lịch Sử Giao Hàng
const historyModal = reactive({
  isOpen: false,
  loading: false,
  fromDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  toDate: new Date().toISOString().split('T')[0],
  data: []
})

async function openReconciliationModal() {
  reconciliationModal.isOpen = true
  reconciliationModal.loading = true
  try {
    const res = await deliveryReportService.getDailyReconciliation({ reportDate: selectedDate.value })
    reconciliationModal.data = res?.data !== undefined ? res.data : res
  } catch (err) {
    console.error('Lỗi lấy đối soát cuối ca:', err)
  } finally {
    reconciliationModal.loading = false
  }
}

async function openHistoryModal() {
  historyModal.isOpen = true
  await fetchHistory()
}

async function fetchHistory() {
  historyModal.loading = true
  try {
    const res = await deliveryReportService.getDeliveryHistory({
      fromDate: historyModal.fromDate,
      toDate: historyModal.toDate
    })
    historyModal.data = res?.data !== undefined ? res.data : (Array.isArray(res) ? res : [])
  } catch (err) {
    console.error('Lỗi lấy lịch sử giao hàng:', err)
  } finally {
    historyModal.loading = false
  }
}

function printReconciliation() {
  window.print()
}

onMounted(() => {
  fetchReports()
})
</script>
