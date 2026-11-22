import apiClient from './api'

export const debtService = {
  /**
   * Lấy danh sách toàn bộ các đơn hàng đang bị nợ (payment_status = 'DEBT')
   * GET /api/v1/debts?customer=...&employeeId=...
   */
  async getDebts(params = {}) {
    const res = await apiClient.get('/debts', { params })
    return res?.data !== undefined ? res.data : (Array.isArray(res) ? res : [])
  },

  /**
   * Thu tiền nợ từ khách hàng
   * POST /api/v1/debts/{reportId}/pay
   */
  async payDebt(reportId, data) {
    const res = await apiClient.post(`/debts/${reportId}/pay`, data)
    return res?.data !== undefined ? res.data : res
  }
}

export default debtService
