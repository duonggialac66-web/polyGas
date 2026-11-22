import apiClient from './api'

/**
 * Service giao tiếp API Chi phí vận hành (/api/v1/expenses)
 */
export const expenseService = {
  /**
   * Shipper ghi nhận chi phí phát sinh trong ngày (amount > 0)
   * @param {Object} expenseData { employeeId, expenseDate, description, amount, notes }
   */
  create(expenseData) {
    return apiClient.post('/expenses', expenseData)
  },

  /**
   * Xem các khoản chi trong ngày của shipper
   * @param {string} employeeId ID nhân viên
   * @param {string} date Ngày (YYYY-MM-DD, tùy chọn)
   */
  getDaily(employeeId, date) {
    return apiClient.get('/expenses/daily', {
      params: { employeeId, date }
    })
  }
}

export default expenseService
