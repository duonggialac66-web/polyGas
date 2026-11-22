<template>
  <div class="app-container">
    <Sidebar />
    <div class="main-wrapper">
      <Navbar :is-backend-connected="isBackendConnected" @refresh="$emit('refresh')" />
      <main class="page-content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

defineProps({
  isBackendConnected: {
    type: Boolean,
    default: true
  }
})

defineEmits(['refresh'])
</script>

