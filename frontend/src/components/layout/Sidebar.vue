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
      <div class="nav-section-title">Phân Hệ Của Phúc (PC11019)</div>

      <router-link to="/salary" class="nav-link">
        <span class="nav-icon">💰</span>
        <span>Lương & Tính SpEL</span>
      </router-link>

      <router-link to="/users" class="nav-link">
        <span class="nav-icon">👥</span>
        <span>Quản Lý Nhân Sự</span>
      </router-link>

      <div class="nav-section-title">Nhân Viên Giao Hàng</div>

      <router-link to="/employment" class="nav-link">
        <span class="nav-icon">🚚</span>
        <span>Báo Cáo Giao Gas</span>
      </router-link>

      <div class="nav-section-title">Quản Lý & Khách Hàng</div>

      <router-link to="/customers" class="nav-link">
        <span class="nav-icon">📇</span>
        <span>Danh Bạ Khách Hàng</span>
      </router-link>

      <router-link to="/debts" class="nav-link">
        <span class="nav-icon">📝</span>
        <span>Sổ Nợ Cần Thu</span>
      </router-link>

      <div class="nav-section-title">Nghiệp Vụ Kho & Cài Đặt</div>

      <a href="#inventory" class="nav-link" @click.prevent="alertFeature('Quản lý Kho gas')">
        <span class="nav-icon">📦</span>
        <span>Quản Lý Kho Gas</span>
      </a>

      <a href="#settings" class="nav-link" @click.prevent="alertFeature('Cài đặt hệ thống')">
        <span class="nav-icon">⚙️</span>
        <span>Cài Đặt Hệ Thống</span>
      </a>
    </nav>

    <!-- Footer Profile -->
    <div class="sidebar-footer">
      <div class="user-avatar-sm">HP</div>
      <div style="flex: 1; min-width: 0;">
        <div style="font-weight: 700; font-size: 0.85rem; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
          Hà Trọng Phúc
        </div>
        <div style="font-size: 0.75rem; color: #94a3b8;">
          Quản lý Lương & Nhân sự
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
