import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import EmploymentView from '../views/EmploymentView.vue'
import CustomersView from '../views/CustomersView.vue'
import DebtsView from '../views/DebtsView.vue'
import ExpensesView from '../views/ExpensesView.vue'
import { isLoggedIn } from '../services/api.js'

const routes = [
  // ===== PUBLIC ROUTES (không cần đăng nhập) =====
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { title: 'Đăng Nhập - PolyGas Enterprise', requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: { title: 'Đăng Ký - PolyGas Enterprise', requiresGuest: true }
  },

  // ===== PROTECTED ROUTES (cần đăng nhập) =====
  {
    path: '/',
    redirect: () => {
      return isLoggedIn() ? '/employment' : '/login'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { title: 'Tổng Quan Hệ Thống - OriginGas Enterprise', requiresAuth: true }
  },
  {
    path: '/employment',
    name: 'Employment',
    component: EmploymentView,
    meta: { title: 'Báo Cáo Giao Gas - OriginGas Enterprise', requiresAuth: true }
  },
  {
    path: '/customers',
    name: 'Customers',
    component: CustomersView,
    meta: { title: 'Danh Bạ Khách Hàng - OriginGas', requiresAuth: true }
  },
  {
    path: '/debts',
    name: 'Debts',
    component: DebtsView,
    meta: { title: 'Sổ Nợ Cần Thu - OriginGas', requiresAuth: true }
  },
  {
    path: '/expenses',
    name: 'Expenses',
    component: ExpensesView,
    meta: { title: 'Chi Phí Vận Hành - OriginGas', requiresAuth: true }
  },

  // ===== CATCH ALL =====
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active'
})

// ===== Navigation Guard =====
router.beforeEach((to, from, next) => {
  // Cập nhật title tab
  if (to.meta && to.meta.title) {
    document.title = to.meta.title
  }

  const loggedIn = isLoggedIn()

  // Route cần đăng nhập mà chưa login → về trang login
  if (to.meta.requiresAuth && !loggedIn) {
    return next({ name: 'Login' })
  }

  // Route chỉ dành cho guest (login/register) mà đã login → về trang chính
  if (to.meta.requiresGuest && loggedIn) {
    return next({ name: 'Employment' })
  }

  next()
})

export default router
