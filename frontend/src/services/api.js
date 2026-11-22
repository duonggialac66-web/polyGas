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

// Request Interceptor: Tự động đính kèm Bearer Token nếu có
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response Interceptor: Trích xuất ApiResponse chuẩn và giữ lại danh sách lỗi trường (fieldErrors)
apiClient.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    const resData = error.response?.data
    const message = resData?.message || error.message || 'Lỗi kết nối máy chủ'
    const customError = new Error(message)
    customError.response = error.response
    customError.status = error.response?.status
    customError.fieldErrors = (resData && typeof resData.data === 'object' && !Array.isArray(resData.data)) ? resData.data : {}

    // Bắt lỗi chuẩn JWT 401: Token hết hạn hoặc không hợp lệ
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      console.warn('JWT Token hết hạn hoặc không hợp lệ, đã xóa token khỏi localStorage!')
    }

    console.error('API Error:', message, customError.fieldErrors)
    return Promise.reject(customError)
  }
)

export default apiClient
