import apiClient from './api'

// Danh sách các loại bình gas tiêu chuẩn và giá niêm yết
export const GAS_TYPES = [
  { type: 'Bình 12kg Xám', defaultPrice: 350000, description: 'Bình gia đình thông dụng' },
  { type: 'Bình 12kg Đỏ', defaultPrice: 360000, description: 'Bình gas cao cấp Petro' },
  { type: 'Bình 45kg Công Nghiệp', defaultPrice: 1250000, description: 'Bình công nghiệp nhà hàng/khách sạn' },
  { type: 'Gas Lon Mini', defaultPrice: 15000, description: 'Lon gas du lịch' }
]

export const deliveryReportService = {
  /**
   * Lấy danh sách phiếu giao gas trong ngày của nhân viên (100% dữ liệu thật từ Backend)
   * GET /api/v1/delivery-reports/today
   */
  async getTodayReports(date) {
    const res = await apiClient.get('/delivery-reports/today', { params: { date } })
    const list = res?.data !== undefined ? res.data : (Array.isArray(res) ? res : [])
    return { data: list }
  },

  /**
   * Tạo phiếu báo cáo giao gas mới (POST /api/v1/delivery-reports)
   * Backend tự động kiểm tra trừ tồn kho, tính thành tiền và lưu vào SQL Server
   */
  async createReport(reportData) {
    const res = await apiClient.post('/delivery-reports', reportData)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Cập nhật thông tin phiếu giao gas
   * PUT /api/v1/delivery-reports/{id}
   */
  async updateReport(id, reportData) {
    const res = await apiClient.put(`/delivery-reports/${id}`, reportData)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Hoàn trả vỏ bình gas nợ về sau (Khi khách đem trả lại vỏ thiếu)
   * POST /api/v1/delivery-reports/{id}/return-shells
   */
  async returnShells(id, returnCount) {
    const res = await apiClient.post(`/delivery-reports/${id}/return-shells`, { count: returnCount })
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Đổi nhanh trạng thái: Đã thanh toán (PAID) <-> Ghi nợ (DEBT)
   * PATCH /api/v1/delivery-reports/{id}/toggle-payment
   */
  async togglePaymentStatus(id) {
    const res = await apiClient.patch(`/delivery-reports/${id}/toggle-payment`)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Xóa phiếu giao gas
   * DELETE /api/v1/delivery-reports/{id}
   */
  async deleteReport(id) {
    const res = await apiClient.delete(`/delivery-reports/${id}`)
    return res
  },

  /**
   * Lấy lịch sử giao hàng theo khoảng ngày (fromDate, toDate)
   * GET /api/v1/delivery-reports/history
   */
  async getDeliveryHistory(params = {}) {
    const res = await apiClient.get('/delivery-reports/history', { params })
    return res?.data !== undefined ? res.data : (Array.isArray(res) ? res : [])
  },

  /**
   * Lấy số liệu đối soát nộp tiền mặt cuối ngày của shipper
   * GET /api/v1/delivery-reports/daily-reconciliation
   */
  async getDailyReconciliation(params = {}) {
    const res = await apiClient.get('/delivery-reports/daily-reconciliation', { params })
    return res?.data !== undefined ? res.data : res
  }
}

export default deliveryReportService
