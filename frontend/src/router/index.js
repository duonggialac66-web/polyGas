import { createRouter, createWebHistory } from 'vue-router'
import EmploymentView from '../views/EmploymentView.vue'
import CustomersView from '../views/CustomersView.vue'
import DebtsView from '../views/DebtsView.vue'
import ExpensesView from '../views/ExpensesView.vue'

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
      title: 'Danh Bạ Khách Hàng (GPS) - OriginGas',
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
  {
    path: '/expenses',
    name: 'Expenses',
    component: ExpensesView,
    meta: {
      title: 'Chi Phí Vận Hành - OriginGas',
      requiresAuth: true
    }
  },
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
