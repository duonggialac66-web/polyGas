<template>
  <AppLayout>
    <div class="page-header">
      <div class="page-title">
        <h2>📊 Tổng Quan Hệ Thống OriginGas Enterprise</h2>
        <p>Bảng điều khiển trung tâm và định hướng các phân hệ nghiệp vụ</p>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon primary">👥</div>
        <div class="stat-info">
          <p>Nhân Sự</p>
          <h3>Phân Hệ Quản Lý</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon warning">📦</div>
        <div class="stat-info">
          <p>Kho Gas</p>
          <h3>Tồn Kho & Cảnh Báo</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon success">📝</div>
        <div class="stat-info">
          <p>Giao Gas</p>
          <h3>Sổ Nợ & Báo Cáo</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon danger">💰</div>
        <div class="stat-info">
          <p>Lương Bổng</p>
          <h3>Công Thức SpEL</h3>
        </div>
      </div>
    </div>

    <!-- Navigation Hub Cards -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; margin-top: 1rem;">
      <div class="content-card" style="padding: 1.5rem;">
        <h3 style="font-size: 1.15rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem;">
          👥 Phân Hệ Quản Lý Nhân Viên (Employment)
        </h3>
        <p style="font-size: 0.875rem; color: #64748b; margin-bottom: 1.25rem;">
          Trang đã sẵn sàng với bộ lọc, tìm kiếm, thêm mới, sửa và phân quyền tài khoản (Admin / Employee).
        </p>
        <router-link to="/employment" class="btn btn-primary">
          Đi đến Trang Nhân Viên &rarr;
        </router-link>
      </div>

      <div class="content-card" style="padding: 1.5rem;">
        <h3 style="font-size: 1.15rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem;">
          🔌 Kết Nối Backend RESTful API
        </h3>
        <p style="font-size: 0.875rem; color: #64748b; margin-bottom: 0.5rem;">
          • Base URL: <code>http://localhost:8080/api/v1</code><br>
          • API Nhân viên: <code>/api/v1/users</code> hoặc <code>/api/v1/employees</code><br>
          • Đã mở CORS và tích hợp sẵn DTO chuẩn <code>ApiResponse&lt;T&gt;</code>.
        </p>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '../components/layout/AppLayout.vue'
</script>

