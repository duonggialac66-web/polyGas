import apiClient from './api'

export const debtService = {
  /**
   * Lấy danh sách toàn bộ các đơn hàng đang bị nợ (payment_status = 'DEBT')
   * GET /api/v1/debts?customer=...&employeeId=...
   */
  async getDebts(params = {}) {
    try {
      const res = await apiClient.get('/debts', { params })
      return res.data || []
    } catch (err) {
      console.warn('Lỗi gọi API /debts, fallback sang mock/empty:', err.message)
      return []
    }
  },

  /**
   * Thu tiền nợ từ khách hàng
   * POST /api/v1/debts/{reportId}/pay
   */
  async payDebt(reportId, data) {
    return apiClient.post(`/debts/${reportId}/pay`, data)
  }
}
