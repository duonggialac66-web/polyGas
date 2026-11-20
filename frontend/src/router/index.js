import { createRouter, createWebHistory } from 'vue-router'
import EmploymentView from '../views/EmploymentView.vue'
import DashboardView from '../views/DashboardView.vue'

const routes = [
  {
    path: '/',
    redirect: '/employment'
  },
  {
    path: '/employment',
    name: 'Employment',
    component: EmploymentView,
    meta: {
      title: 'Báo Cáo Giao Gas Hôm Nay - OriginGas',
      requiresAuth: true
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: {
      title: 'Tổng Quan Hệ Thống - OriginGas',
      requiresAuth: true
    }
  },
  // Alias đường dẫn cho thuận tiện
  {
    path: '/employees',
    redirect: '/employment'
  },
  // Bắt tất cả các route khác chuyển về /employment
  {
    path: '/:pathMatch(.*)*',
    redirect: '/employment'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active'
})

// Navigation Guard (Bộ điều hướng Router)
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router

