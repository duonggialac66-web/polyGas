import apiClient from './api'

// 4 loại bình gas tiêu chuẩn nạp mẫu theo CSDL DataInitializer
const DEFAULT_MOCK_INVENTORIES = [
  {
    id: 'inv-001',
    containerType: 'Bình 12kg Xám',
    fullQuantity: 100,
    emptyQuantity: 30,
    warningThreshold: 15,
    isWarning: false,
    updatedAt: new Date().toISOString()
  },
  {
    id: 'inv-002',
    containerType: 'Bình 12kg Đỏ',
    fullQuantity: 80,
    emptyQuantity: 25,
    warningThreshold: 15,
    isWarning: false,
    updatedAt: new Date().toISOString()
  },
  {
    id: 'inv-003',
    containerType: 'Bình 45kg Công Nghiệp',
    fullQuantity: 8, // Giả lập cảnh báo sắp hết hàng để test UI
    emptyQuantity: 18,
    warningThreshold: 10,
    isWarning: true,
    updatedAt: new Date().toISOString()
  },
  {
    id: 'inv-004',
    containerType: 'Gas Lon Mini',
    fullQuantity: 200,
    emptyQuantity: 50,
    warningThreshold: 30,
    isWarning: false,
    updatedAt: new Date().toISOString()
  }
]

// Khởi tạo lưu trữ mock trong localStorage để test thao tác trên trình duyệt mượt mà
function getStoredMockInventories() {
  try {
    const raw = localStorage.getItem('polygas_mock_inventories')
    if (raw) {
      return JSON.parse(raw)
    }
  } catch (e) {
    console.warn('Lỗi đọc mock inventories:', e)
  }
  return DEFAULT_MOCK_INVENTORIES
}

function saveStoredMockInventories(list) {
  try {
    localStorage.setItem('polygas_mock_inventories', JSON.stringify(list))
  } catch (e) {
    console.warn('Lỗi lưu mock inventories:', e)
  }
}

export const inventoryService = {
  /**
   * Lấy danh sách tồn kho các loại bình
   * GET /api/v1/inventories
   */
  async getInventories() {
    try {
      const res = await apiClient.get('/inventories')
      // ApiResponse format: { success, message, data }
      const list = res.data || res || []
      const formatted = list.map(item => ({
        ...item,
        isWarning: item.fullQuantity <= item.warningThreshold
      }))
      return { data: formatted, isMock: false }
    } catch (err) {
      console.warn('Backend chưa bật, dùng dữ liệu Mock tồn kho:', err.message)
      const mockList = getStoredMockInventories().map(item => ({
        ...item,
        isWarning: item.fullQuantity <= item.warningThreshold
      }))
      return { data: mockList, isMock: true }
    }
  },

  /**
   * Cập nhật điều chỉnh tồn kho theo ID
   * PUT /api/v1/inventories/{id}/stock
   * Body: { quantity: number, type: 'IMPORT_FULL' | 'EXPORT_FULL' | 'IMPORT_EMPTY' | 'EXPORT_EMPTY' }
   */
  async updateStock(id, payload) {
    try {
      const res = await apiClient.put(`/inventories/${id}/stock`, payload)
      return { data: res.data || res, isMock: false }
    } catch (err) {
      console.warn('Backend chưa bật, cập nhật tồn kho ở chế độ Mock:', err.message)
      const list = getStoredMockInventories()
      const item = list.find(i => i.id === id)
      if (item) {
        const qty = Number(payload.quantity) || 0
        switch (payload.type) {
          case 'IMPORT_FULL':
            item.fullQuantity += qty
            break
          case 'EXPORT_FULL':
            if (item.fullQuantity < qty) {
              throw new Error('Số lượng bình đầy trong kho không đủ để xuất!')
            }
            item.fullQuantity -= qty
            break
          case 'IMPORT_EMPTY':
            item.emptyQuantity += qty
            break
          case 'EXPORT_EMPTY':
            if (item.emptyQuantity < qty) {
              throw new Error('Số lượng vỏ rỗng trong kho không đủ để xuất!')
            }
            item.emptyQuantity -= qty
            break
          default:
            item.fullQuantity += qty
        }
        item.updatedAt = new Date().toISOString()
        item.isWarning = item.fullQuantity <= item.warningThreshold
        saveStoredMockInventories(list)
        return { data: item, isMock: true }
      }
      throw new Error('Không tìm thấy loại bình gas với id: ' + id)
    }
  },

  /**
   * Nhập kho linh hoạt: Tự động phát hiện loại bình cũ (để cộng dồn)
   * hoặc tự động thêm mới vào danh mục tồn kho nếu là loại bình mới!
   */
  async importStockFlexible({ containerType, quantity, type, warningThreshold }) {
    const trimmedName = (containerType || '').trim()
    if (!trimmedName) {
      throw new Error('Tên loại bình gas không được để trống!')
    }

    try {
      // Thử gọi API Backend nếu có
      const res = await apiClient.post('/inventories/import-flexible', {
        containerType: trimmedName,
        quantity: Number(quantity) || 0,
        type: type || 'IMPORT_FULL',
        warningThreshold: Number(warningThreshold) || 10
      })
      return { data: res.data || res, isMock: false }
    } catch (err) {
      console.warn('Backend chưa bật hoặc chưa có API import-flexible, xử lý ở chế độ Mock:', err.message)
      const list = getStoredMockInventories()
      const existing = list.find(i => i.containerType.toLowerCase() === trimmedName.toLowerCase())

      const qty = Number(quantity) || 0
      const threshold = Number(warningThreshold) || 10

      if (existing) {
        // Loại bình đã có sẵn -> cộng dồn
        if (type === 'IMPORT_EMPTY') {
          existing.emptyQuantity += qty
        } else {
          existing.fullQuantity += qty
        }
        existing.updatedAt = new Date().toISOString()
        existing.isWarning = existing.fullQuantity <= existing.warningThreshold
        saveStoredMockInventories(list)
        return { data: existing, isNew: false, isMock: true }
      } else {
        // Loại bình MỚI HOÀN TOÀN -> Tự động thêm vào danh mục tồn kho
        const newItem = {
          id: 'inv-' + Date.now(),
          containerType: trimmedName,
          fullQuantity: type === 'IMPORT_EMPTY' ? 0 : qty,
          emptyQuantity: type === 'IMPORT_EMPTY' ? qty : 0,
          warningThreshold: threshold,
          isWarning: (type === 'IMPORT_EMPTY' ? 0 : qty) <= threshold,
          updatedAt: new Date().toISOString()
        }
        list.push(newItem)
        saveStoredMockInventories(list)
        return { data: newItem, isNew: true, isMock: true }
      }
    }
  },

  /**
   * Reset mock data về mặc định nếu cần kiểm thử lại từ đầu
   */
  resetMockData() {
    saveStoredMockInventories(DEFAULT_MOCK_INVENTORIES)
  }
}
