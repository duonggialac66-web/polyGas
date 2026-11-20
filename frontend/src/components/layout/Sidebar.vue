<template>
  <aside class="sidebar">
    <!-- Brand -->
    <div class="sidebar-brand">
      <div class="brand-logo">
        🔥
      </div>
      <div class="brand-text">
        <h1>OriginGas</h1>
        <span>Enterprise v2.0</span>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">
      <div class="nav-section-title">Hệ Thống Chính</div>
      
      <router-link to="/dashboard" class="nav-link">
        <span class="nav-icon">📊</span>
        <span>Tổng Quan</span>
      </router-link>

      <router-link to="/employment" class="nav-link">
        <span class="nav-icon">👥</span>
        <span>Quản Lý Nhân Viên</span>
      </router-link>

      <div class="nav-section-title">Nghiệp Vụ Gas</div>

      <a href="#inventory" class="nav-link" @click.prevent="alertFeature('Quản lý Kho gas')">
        <span class="nav-icon">📦</span>
        <span>Quản Lý Kho Gas</span>
      </a>

      <a href="#delivery" class="nav-link" @click.prevent="alertFeature('Sổ nợ & Phiếu giao gas')">
        <span class="nav-icon">📝</span>
        <span>Sổ Nợ & Báo Cáo</span>
      </a>

      <a href="#salary" class="nav-link" @click.prevent="alertFeature('Quản lý & Tính lương SpEL')">
        <span class="nav-icon">💰</span>
        <span>Tính Lương Nhân Viên</span>
      </a>

      <div class="nav-section-title">Cấu Hình</div>

      <a href="#settings" class="nav-link" @click.prevent="alertFeature('Cài đặt hệ thống')">
        <span class="nav-icon">⚙️</span>
        <span>Cài Đặt Hệ Thống</span>
      </a>
    </nav>

    <!-- Footer Profile -->
    <div class="sidebar-footer">
      <div class="user-avatar-sm">AD</div>
      <div style="flex: 1; min-width: 0;">
        <div style="font-weight: 700; font-size: 0.85rem; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
          Admin OriginGas
        </div>
        <div style="font-size: 0.75rem; color: #94a3b8;">
          Quản trị cấp cao
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
const alertFeature = (moduleName) => {
  alert(`Module [${moduleName}] đang được các thành viên trong nhóm phát triển tiếp!`)
}
</script>

