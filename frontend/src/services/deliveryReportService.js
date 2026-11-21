import apiClient from './api'

// Danh sách các loại bình gas tiêu chuẩn và giá niêm yết
export const GAS_TYPES = [
  { type: 'Bình 12kg Xám', defaultPrice: 350000, description: 'Bình gia đình thông dụng' },
  { type: 'Bình 12kg Đỏ', defaultPrice: 360000, description: 'Bình gas cao cấp Petro' },
  { type: 'Bình 45kg Công Nghiệp', defaultPrice: 1250000, description: 'Bình công nghiệp nhà hàng/khách sạn' },
  { type: 'Gas Lon Mini', defaultPrice: 15000, description: 'Lon gas du lịch' }
]

// Dữ liệu mẫu ban đầu để nhân viên test ngay khi backend chưa bật
const MOCK_REPORTS = [
  {
    id: 'rep-001',
    customerName: 'Quán Ăn Cô Ba',
    containerType: 'Bình 12kg Xám',
    quantity: 2,
    unitPrice: 350000,
    total: 700000,
    paymentStatus: 'PAID',
    notes: 'Giao trực tiếp tại bếp tầng trệt',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    createdAt: new Date().toISOString()
  },
  {
    id: 'rep-002',
    customerName: 'Bún Bò Huế 36',
    containerType: 'Bình 45kg Công Nghiệp',
    quantity: 1,
    unitPrice: 1250000,
    total: 1250000,
    paymentStatus: 'DEBT',
    notes: 'Khách hẹn chiều 17h ghé lấy tiền mặt',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    createdAt: new Date().toISOString()
  },
  {
    id: 'rep-003',
    customerName: 'Anh Nam - Chung cư Flora',
    containerType: 'Bình 12kg Đỏ',
    quantity: 1,
    unitPrice: 360000,
    total: 360000,
    paymentStatus: 'PAID',
    notes: 'Chuyển khoản qua mã QR',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    createdAt: new Date().toISOString()
  }
]

export const deliveryReportService = {
  /**
   * Lấy danh sách phiếu giao gas trong ngày của nhân viên
   */
  async getTodayReports(date) {
    try {
      const res = await apiClient.get('/employment/today', { params: { date } })
      if (res && res.data) {
        return { data: res.data, isMock: false }
      }
      return { data: [], isMock: false }
    } catch (err) {
      console.warn('Backend chưa phản hồi, chuyển sang chế độ Mock Data:', err.message)
      const targetDate = date || new Date().toISOString().split('T')[0]
      const filtered = MOCK_REPORTS.filter(r => r.reportDate === targetDate)
      return { data: filtered, isMock: true }
    }
  },

  /**
   * Nhân viên tạo phiếu báo cáo giao gas mới
   */
  async createReport(reportData) {
    try {
      const res = await apiClient.post('/employment', reportData)
      return res.data
    } catch (err) {
      console.warn('Tạo phiếu ở chế độ Mock:', err.message)
      const total = (reportData.quantity || 1) * (reportData.unitPrice || 0)
      const mockItem = {
        ...reportData,
        id: 'rep-' + Date.now(),
        total,
        reportDate: reportData.reportDate || new Date().toISOString().split('T')[0],
        employeeName: 'Nhân viên giao gas',
        createdAt: new Date().toISOString()
      }
      MOCK_REPORTS.unshift(mockItem)
      return mockItem
    }
  },

  /**
   * Cập nhật phiếu giao gas
   */
  async updateReport(id, reportData) {
    try {
      const res = await apiClient.put(`/employment/${id}`, reportData)
      return res.data
    } catch (err) {
      console.warn('Cập nhật phiếu ở chế độ Mock:', err.message)
      const index = MOCK_REPORTS.findIndex(r => r.id === id)
      if (index !== -1) {
        const total = (reportData.quantity || 1) * (reportData.unitPrice || 0)
        MOCK_REPORTS[index] = { ...MOCK_REPORTS[index], ...reportData, total }
        return MOCK_REPORTS[index]
      }
      return reportData
    }
  },

  /**
   * Đổi nhanh trạng thái: Đã thanh toán (PAID) <-> Ghi nợ (DEBT)
   */
  async togglePaymentStatus(id) {
    try {
      const res = await apiClient.patch(`/employment/${id}/toggle-payment`)
      return res.data
    } catch (err) {
      const item = MOCK_REPORTS.find(r => r.id === id)
      if (item) {
        item.paymentStatus = item.paymentStatus === 'PAID' ? 'DEBT' : 'PAID'
        return item
      }
      return null
    }
  },

  /**
   * Xóa phiếu giao gas
   */
  async deleteReport(id) {
    try {
      const res = await apiClient.delete(`/employment/${id}`)
      return res
    } catch (err) {
      const index = MOCK_REPORTS.findIndex(r => r.id === id)
      if (index !== -1) {
        MOCK_REPORTS.splice(index, 1)
      }
      return { success: true }
    }
  },

  /**
   * Lấy lịch sử giao hàng theo khoảng ngày (fromDate, toDate)
   * GET /api/v1/delivery-reports/history
   */
  async getDeliveryHistory(params = {}) {
    try {
      const res = await apiClient.get('/delivery-reports/history', { params })
      return res.data || []
    } catch (err) {
      console.warn('Lỗi gọi API /delivery-reports/history:', err.message)
      return []
    }
  },

  /**
   * Lấy số liệu đối soát nộp tiền mặt cuối ngày của shipper
   * GET /api/v1/delivery-reports/daily-reconciliation
   */
  async getDailyReconciliation(params = {}) {
    try {
      const res = await apiClient.get('/delivery-reports/daily-reconciliation', { params })
      return res.data || null
    } catch (err) {
      console.warn('Lỗi gọi API /delivery-reports/daily-reconciliation:', err.message)
      return null
    }
  }
}

