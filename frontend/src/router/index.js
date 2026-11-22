import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import EmploymentView from '../views/EmploymentView.vue'
import InventoryView from '../views/InventoryView.vue'
import CustomersView from '../views/CustomersView.vue'
import DebtsView from '../views/DebtsView.vue'
import ExpensesView from '../views/ExpensesView.vue'
import AdminViewReport from '../views/AdminViewReport.vue'
import UsersView from '../views/UsersView.vue'
import { isLoggedIn } from '../services/api.js'

function getRole() {
  return localStorage.getItem('role') || 'EMPLOYEE'
}

function isAdminRole() {
  const role = getRole()
  return role === 'ADMIN' || role === 'ROLE_ADMIN'
}

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
      if (!isLoggedIn()) return '/login'
      return isAdminRole() ? '/dashboard' : '/employment'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { 
      title: 'Trung Tâm Điều Hành - PolyGas Enterprise', 
      requiresAuth: true,
      roles: ['ADMIN', 'ROLE_ADMIN']
    }
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: InventoryView,
    meta: {
      title: 'Quản Lý Kho Gas & Vỏ Rỗng - PolyGas',
      requiresAuth: true
    }
  },
  {
    path: '/admin/view_report',
    alias: '/admin/view-report',
    name: 'AdminViewReport',
    component: AdminViewReport,
    meta: {
      title: 'Đối Soát Thu Chi & Báo Cáo Ca - PolyGas',
      requiresAuth: true
    }
  },
  {
    path: '/users',
    name: 'Users',
    component: UsersView,
    meta: {
      title: 'Quản Lý Nhân Sự & Phân Quyền - PolyGas Enterprise',
      requiresAuth: true,
      roles: ['ADMIN', 'ROLE_ADMIN']
    }
  },
  {
    path: '/employment',
    name: 'Employment',
    component: EmploymentView,
    meta: { title: 'Báo Cáo Giao Gas - PolyGas Enterprise', requiresAuth: true }
  },
  {
    path: '/customers',
    name: 'Customers',
    component: CustomersView,
    meta: { title: 'Danh Bạ Khách Hàng - PolyGas', requiresAuth: true }
  },
  {
    path: '/debts',
    name: 'Debts',
    component: DebtsView,
    meta: { title: 'Sổ Nợ Cần Thu - PolyGas', requiresAuth: true }
  },
  {
    path: '/expenses',
    name: 'Expenses',
    component: ExpensesView,
    meta: { title: 'Chi Phí Vận Hành - PolyGas', requiresAuth: true }
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

  // Route chỉ dành cho guest (login/register) mà đã login → điều hướng theo role
  if (to.meta.requiresGuest && loggedIn) {
    return next(isAdminRole() ? { name: 'Dashboard' } : { name: 'Employment' })
  }

  // Route giới hạn vai trò (ví dụ: chỉ ADMIN)
  if (to.meta && to.meta.roles && !to.meta.roles.includes(getRole())) {
    return next(isAdminRole() ? { name: 'Dashboard' } : { name: 'Employment' })
  }

  next()
})

export default router
