import apiClient from './api'
import { inventoryService } from './inventoryService'

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
    returnedShells: 2, // Đã trả đủ 2/2 vỏ
    debtShells: 0,
    shellStatus: 'RETURNED',
    unitPrice: 350000,
    total: 700000,
    actualReceived: 700000,
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
    returnedShells: 0, // Trả thiếu: nợ 1 vỏ
    debtShells: 1,
    shellStatus: 'OWING',
    unitPrice: 1250000,
    total: 1250000,
    actualReceived: 0,
    paymentStatus: 'DEBT',
    notes: 'Khách hẹn chiều 17h ghé lấy tiền mặt, nợ 1 vỏ 45kg chưa trả',
    reportDate: new Date().toISOString().split('T')[0],
    employeeName: 'Nguyễn Văn Giao (Shipper 01)',
    createdAt: new Date().toISOString()
  },
  {
    id: 'rep-003',
    customerName: 'Anh Nam - Chung cư Flora',
    containerType: 'Bình 12kg Đỏ',
    quantity: 1,
    returnedShells: 1, // Đã trả đủ 1/1 vỏ
    debtShells: 0,
    shellStatus: 'RETURNED',
    unitPrice: 360000,
    total: 360000,
    actualReceived: 360000,
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
      let res
      try {
        res = await apiClient.get('/delivery-reports/today', { params: { date } })
      } catch {
        res = await apiClient.get('/employment/today', { params: { date } })
      }

      if (res && (res.data || Array.isArray(res))) {
        return { data: res.data || res, isMock: false }
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
   * Tạo phiếu báo cáo giao gas mới (POST /api/v1/delivery-reports)
   * Tự động kiểm tra trừ tồn kho và xác định PAID/DEBT
   */
  async createReport(reportData) {
    try {
      let res
      try {
        res = await apiClient.post('/delivery-reports', reportData)
      } catch (e) {
        if (e.message && e.message.includes('404')) {
          res = await apiClient.post('/employment', reportData)
        } else {
          throw e
        }
      }
      return res.data || res
    } catch (err) {
      // Nếu là lỗi nghiệp vụ từ backend ném về (400 Bad Request: Số lượng bình đầy trong kho không đủ để giao!)
      if (err.message && err.message.includes('không đủ để giao')) {
        throw err
      }

      console.warn('Backend chưa bật, tạo phiếu ở chế độ Mock:', err.message)
      const qty = Number(reportData.quantity) || 1
      const total = qty * (Number(reportData.unitPrice) || 0)
      const actualReceived = reportData.actualReceived !== undefined ? Number(reportData.actualReceived) : total
      const paymentStatus = actualReceived >= total ? 'PAID' : 'DEBT'

      // Xử lý logic trả vỏ: Nếu bỏ trống (null, undefined, '') thì coi như trả đủ vỏ
      let returnedShells = reportData.returnedShells
      if (returnedShells === undefined || returnedShells === null || returnedShells === '') {
        returnedShells = qty
      } else {
        returnedShells = Math.max(0, Math.min(qty, Number(returnedShells)))
      }
      const debtShells = Math.max(0, qty - returnedShells)
      const shellStatus = debtShells === 0 ? 'RETURNED' : 'OWING'

      // Mô phỏng trừ tồn kho bình đầy và cộng vỏ rỗng khách đã trả
      try {
        const invListRes = await inventoryService.getInventories()
        const matchedInv = invListRes.data.find(i => i.containerType === reportData.containerType)
        if (matchedInv) {
          if (matchedInv.fullQuantity < qty) {
            throw new Error('Số lượng bình đầy trong kho không đủ để giao!')
          }
          // Xuất bình đầy
          await inventoryService.updateStock(matchedInv.id, {
            quantity: qty,
            type: 'EXPORT_FULL'
          })
          // Nhập số vỏ rỗng mà khách thực sự trả vào kho
          if (returnedShells > 0) {
            await inventoryService.updateStock(matchedInv.id, {
              quantity: returnedShells,
              type: 'IMPORT_EMPTY'
            })
          }
        }
      } catch (mockStockErr) {
        throw mockStockErr
      }

      const mockItem = {
        ...reportData,
        id: 'rep-' + Date.now(),
        quantity: qty,
        returnedShells,
        debtShells,
        shellStatus,
        total,
        actualReceived,
        paymentStatus,
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
      const res = await apiClient.put(`/delivery-reports/${id}`, reportData)
      return res.data || res
    } catch (err) {
      console.warn('Cập nhật phiếu ở chế độ Mock:', err.message)
      const index = MOCK_REPORTS.findIndex(r => r.id === id)
      if (index !== -1) {
        const qty = Number(reportData.quantity) || MOCK_REPORTS[index].quantity || 1
        const total = qty * (Number(reportData.unitPrice) || 0)
        const actualReceived = reportData.actualReceived !== undefined ? Number(reportData.actualReceived) : total
        const paymentStatus = actualReceived >= total ? 'PAID' : 'DEBT'

        let returnedShells = reportData.returnedShells
        if (returnedShells === undefined || returnedShells === null || returnedShells === '') {
          returnedShells = qty
        } else {
          returnedShells = Math.max(0, Math.min(qty, Number(returnedShells)))
        }
        const debtShells = Math.max(0, qty - returnedShells)
        const shellStatus = debtShells === 0 ? 'RETURNED' : 'OWING'

        MOCK_REPORTS[index] = { 
          ...MOCK_REPORTS[index], 
          ...reportData, 
          quantity: qty,
          returnedShells,
          debtShells,
          shellStatus,
          total, 
          actualReceived, 
          paymentStatus 
        }
        return MOCK_REPORTS[index]
      }
      return reportData
    }
  },

  /**
   * Hoàn trả vỏ bình gas nợ về sau (Khi khách đem trả lại vỏ thiếu)
   */
  async returnShells(id, returnCount) {
    try {
      const res = await apiClient.post(`/delivery-reports/${id}/return-shells`, { count: returnCount })
      return res.data || res
    } catch (err) {
      const item = MOCK_REPORTS.find(r => r.id === id)
      if (item && item.debtShells > 0) {
        const count = returnCount !== undefined ? Math.min(item.debtShells, Number(returnCount)) : item.debtShells
        item.returnedShells += count
        item.debtShells = Math.max(0, item.quantity - item.returnedShells)
        item.shellStatus = item.debtShells === 0 ? 'RETURNED' : 'OWING'

        // Cộng vỏ rỗng vừa hoàn trả vào kho
        try {
          const invListRes = await inventoryService.getInventories()
          const matchedInv = invListRes.data.find(i => i.containerType === item.containerType)
          if (matchedInv) {
            await inventoryService.updateStock(matchedInv.id, {
              quantity: count,
              type: 'IMPORT_EMPTY'
            })
          }
        } catch (e) {
          console.warn('Lỗi cộng kho vỏ rỗng:', e)
        }

        return item
      }
      return null
    }
  },

  /**
   * Đổi nhanh trạng thái: Đã thanh toán (PAID) <-> Ghi nợ (DEBT)
   */
  async togglePaymentStatus(id) {
    try {
      const res = await apiClient.patch(`/delivery-reports/${id}/toggle-payment`)
      return res.data || res
    } catch (err) {
      const item = MOCK_REPORTS.find(r => r.id === id)
      if (item) {
        item.paymentStatus = item.paymentStatus === 'PAID' ? 'DEBT' : 'PAID'
        if (item.paymentStatus === 'PAID') {
          item.actualReceived = item.total
        } else {
          item.actualReceived = 0
        }
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
      const res = await apiClient.delete(`/delivery-reports/${id}`)
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

export default deliveryReportService
