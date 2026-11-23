import { createRouter, createWebHistory } from 'vue-router'
import EmploymentView from '../views/EmploymentView.vue'
import CustomersView from '../views/CustomersView.vue'
import DebtsView from '../views/DebtsView.vue'
import SalaryUserManagementView from '../views/SalaryUserManagementView.vue'

const routes = [
  {
    path: '/',
    redirect: '/salary'
  },
  {
    path: '/salary',
    name: 'SalaryManagement',
    component: SalaryUserManagementView,
    meta: {
      title: 'Quản Lý Lương & SpEL Engine - OriginGas',
      requiresAuth: true
    }
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: SalaryUserManagementView,
    meta: {
      title: 'Quản Lý Nhân Sự & Lương Cơ Bản - OriginGas',
      requiresAuth: true
    }
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
    path: '/customers',
    name: 'Customers',
    component: CustomersView,
    meta: {
      title: 'Danh Bạ Khách Hàng - OriginGas',
      requiresAuth: true
    }
  },
  {
    path: '/debts',
    name: 'Debts',
    component: DebtsView,
    meta: {
      title: 'Sổ Nợ Cần Thu - OriginGas',
      requiresAuth: true
    }
  },
  // Bắt tất cả các route khác chuyển về /salary
  {
    path: '/:pathMatch(.*)*',
    redirect: '/salary'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active'
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router
