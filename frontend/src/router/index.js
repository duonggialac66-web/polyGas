import { createRouter, createWebHistory } from 'vue-router'
import EmploymentView from '../views/EmploymentView.vue'
import CustomersView from '../views/CustomersView.vue'
import DebtsView from '../views/DebtsView.vue'

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

// Navigation Guard
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router
