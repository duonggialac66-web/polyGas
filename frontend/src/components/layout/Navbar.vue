<template>
  <header class="top-navbar">
    <!-- Breadcrumb / Route Title -->
    <div style="display: flex; align-items: center; gap: 0.75rem;">
      <span style="font-size: 1.1rem; color: #64748b;">🏢</span>
      <span style="font-weight: 700; font-size: 1rem; color: #1e293b;">
        {{ currentRouteTitle }}
      </span>
    </div>

    <!-- Status & Right Actions -->
    <div style="display: flex; align-items: center; gap: 1rem;">
      <!-- Backend Status Badge -->
      <div 
        :style="{
          display: 'flex',
          alignItems: 'center',
          gap: '0.4rem',
          padding: '0.3rem 0.75rem',
          borderRadius: '9999px',
          fontSize: '0.78rem',
          fontWeight: '600',
          background: isBackendConnected ? '#d1fae5' : '#fef3c7',
          color: isBackendConnected ? '#065f46' : '#92400e'
        }"
      >
        <span :style="{
          width: '8px',
          height: '8px',
          borderRadius: '50%',
          background: isBackendConnected ? '#10b981' : '#f59e0b',
          display: 'inline-block'
        }"></span>
        <span>{{ isBackendConnected ? 'Backend: 8080 (Online)' : 'Backend: Chế độ Xem trước (Mock)' }}</span>
      </div>

      <!-- Notification Bell -->
      <button class="btn-icon" title="Thông báo hệ thống">
        🔔
      </button>

      <!-- Refresh Page -->
      <button class="btn-icon" title="Làm mới dữ liệu" @click="$emit('refresh')">
        🔄
      </button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

defineProps({
  isBackendConnected: {
    type: Boolean,
    default: true
  }
})

defineEmits(['refresh'])

const route = useRoute()
const currentRouteTitle = computed(() => {
  return route.meta?.title ? route.meta.title.split(' - ')[0] : 'OriginGas ERP'
})
</script>

