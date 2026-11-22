import apiClient from './api'

export const inventoryService = {
  /**
   * Lấy danh sách tồn kho các loại bình (100% dữ liệu thật từ SQL Server)
   * GET /api/v1/inventories
   */
  async getInventories() {
    const res = await apiClient.get('/inventories')
    // ApiResponse envelope: { success, message, data }
    const list = res?.data !== undefined ? res.data : (Array.isArray(res) ? res : [])
    const formatted = list.map(item => ({
      ...item,
      isWarning: item.fullQuantity <= item.warningThreshold
    }))
    return { data: formatted }
  },

  /**
   * Cập nhật điều chỉnh tồn kho theo ID
   * PUT /api/v1/inventories/{id}/stock
   * Body: { quantity: number, type: 'IMPORT_FULL' | 'EXPORT_FULL' | 'IMPORT_EMPTY' | 'EXPORT_EMPTY' }
   */
  async updateStock(id, payload) {
    const res = await apiClient.put(`/inventories/${id}/stock`, payload)
    return { data: res?.data !== undefined ? res.data : res }
  },

  /**
   * Nhập kho linh hoạt: Tự động cộng dồn loại bình cũ hoặc tạo mới nếu là loại bình mới
   * POST /api/v1/inventories/import-flexible
   */
  async importStockFlexible({ containerType, quantity, type, warningThreshold }) {
    const trimmedName = (containerType || '').trim()
    if (!trimmedName) {
      throw new Error('Tên loại bình gas không được để trống!')
    }

    const res = await apiClient.post('/inventories/import-flexible', {
      containerType: trimmedName,
      quantity: Number(quantity) || 0,
      type: type || 'IMPORT_FULL',
      warningThreshold: Number(warningThreshold) || 10
    })
    return { data: res?.data !== undefined ? res.data : res }
  }
}

export default inventoryService
