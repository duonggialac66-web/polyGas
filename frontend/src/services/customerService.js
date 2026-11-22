import apiClient from './api'

/**
 * Service giao tiếp API Khách hàng (/api/v1/customers)
 */
export const customerService = {
  /**
   * Lấy danh bạ toàn bộ khách hàng
   */
  getAll() {
    return apiClient.get('/customers')
  },

  /**
   * Tìm kiếm khách hàng siêu tốc (sử dụng NameHash chuẩn hóa)
   */
  search(keyword) {
    return apiClient.get('/customers/search', {
      params: { keyword }
    })
  },

  /**
   * Thêm khách hàng mới kèm tọa độ GPS
   * @param {Object} customerData { name, phone, address, latitude, longitude, notes }
   */
  create(customerData) {
    return apiClient.post('/customers', customerData)
  },

  /**
   * Lấy chi tiết khách hàng theo ID
   */
  getById(id) {
    return apiClient.get(`/customers/${id}`)
  }
}

export default customerService
