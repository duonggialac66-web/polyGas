import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000
})

// ===== REQUEST INTERCEPTOR =====
// Tự động đính kèm Bearer Token JWT nếu có và còn hạn
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      // Kiểm tra token hết hạn phía client trước khi gửi request
      const expiresAt = localStorage.getItem('tokenExpiresAt')
      if (expiresAt && Date.now() > parseInt(expiresAt)) {
        // Token đã hết hạn — xóa và redirect login
        clearAuthStorage()
        window.location.href = '/login'
        return Promise.reject(new Error('Phiên đăng nhập đã hết hạn'))
      }
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ===== RESPONSE INTERCEPTOR =====
// Trích xuất ApiResponse chuẩn, giữ fieldErrors để hiển thị dưới ô input
apiClient.interceptors.response.use(
  (response) => {
    // Backend trả về ApiResponse<T> → trả thẳng response.data cho caller
    return response.data
  },
  (error) => {
    const resData = error.response?.data
    const message = resData?.message || error.message || 'Lỗi kết nối máy chủ'
    const customError = new Error(message)
    customError.response = error.response
    customError.status = error.response?.status
    // fieldErrors: map {field: message} từ GlobalExceptionHandler (validation lỗi)
    customError.fieldErrors =
      (resData && typeof resData.data === 'object' && !Array.isArray(resData.data))
        ? resData.data
        : {}

    // JWT hết hạn hoặc không hợp lệ → 401 → tự logout
    if (error.response?.status === 401) {
      clearAuthStorage()
      console.warn('[JWT] Token hết hạn hoặc không hợp lệ → đã xóa, chuyển về /login')
      // Chỉ redirect nếu không đang ở trang login/register
      if (!window.location.pathname.startsWith('/login') &&
          !window.location.pathname.startsWith('/register')) {
        window.location.href = '/login'
      }
    }

    console.error('[API Error]', error.response?.status, message, customError.fieldErrors)
    return Promise.reject(customError)
  }
)

// ===== HELPER =====
export function saveAuthData(loginResponse) {
  // loginResponse = { token, username, name, role, expiresIn (ms) }
  localStorage.setItem('token', loginResponse.token)
  localStorage.setItem('username', loginResponse.username)
  localStorage.setItem('name', loginResponse.name)
  localStorage.setItem('role', loginResponse.role)
  if (loginResponse.expiresIn) {
    const expiresAt = Date.now() + loginResponse.expiresIn
    localStorage.setItem('tokenExpiresAt', expiresAt.toString())
  }
}

export function clearAuthStorage() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('name')
  localStorage.removeItem('role')
  localStorage.removeItem('tokenExpiresAt')
}

export function isLoggedIn() {
  const token = localStorage.getItem('token')
  if (!token) return false
  const expiresAt = localStorage.getItem('tokenExpiresAt')
  if (expiresAt && Date.now() > parseInt(expiresAt)) {
    clearAuthStorage()
    return false
  }
  return true
}

export default apiClient
