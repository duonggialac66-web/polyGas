import apiClient from './api'

// Dữ liệu mẫu dự phòng (fallback khi backend chưa khởi động)
const MOCK_USERS = [
  {
    id: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    name: 'Quản trị viên OriginGas',
    username: 'admin',
    role: 'ADMIN',
    phone: '0901234567',
    startDate: '2026-01-01',
    status: 'ACTIVE',
    baseSalary: 15000000,
    avatarUrl: null,
    createdAt: '2026-01-01T08:00:00'
  },
  {
    id: 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
    name: 'Nguyễn Văn Giao',
    username: 'shipper01',
    role: 'EMPLOYEE',
    phone: '0987654321',
    startDate: '2026-02-01',
    status: 'ACTIVE',
    baseSalary: 7000000,
    avatarUrl: null,
    createdAt: '2026-02-01T08:00:00'
  },
  {
    id: 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33',
    name: 'Trần Thị Hạnh (Kế toán)',
    username: 'accountant01',
    role: 'ADMIN',
    phone: '0912345678',
    startDate: '2026-03-01',
    status: 'ACTIVE',
    baseSalary: 10000000,
    avatarUrl: null,
    createdAt: '2026-03-01T08:00:00'
  },
  {
    id: 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44',
    name: 'Lê Hoàng Nam (Giao nhận)',
    username: 'shipper02',
    role: 'EMPLOYEE',
    phone: '0933445566',
    startDate: '2026-03-15',
    status: 'INACTIVE',
    baseSalary: 6500000,
    avatarUrl: null,
    createdAt: '2026-03-15T08:00:00'
  }
]

export const userService = {
  /**
   * Lấy danh sách nhân viên kèm tìm kiếm & lọc
   * @param {Object} params { role, status, keyword }
   */
  async getUsers(params = {}) {
    try {
      const res = await apiClient.get('/users', { params })
      if (res && res.data) {
        return { data: res.data, isMock: false }
      }
      return { data: [], isMock: false }
    } catch (err) {
      console.warn('Backend chưa phản hồi hoặc gặp lỗi, sử dụng Mock Data dự phòng:', err.message)
      // Lọc dữ liệu mock phía client để giao diện test được ngay
      let filtered = [...MOCK_USERS]
      if (params.role) {
        filtered = filtered.filter(u => u.role === params.role)
      }
      if (params.status) {
        filtered = filtered.filter(u => u.status === params.status)
      }
      if (params.keyword && params.keyword.trim()) {
        const kw = params.keyword.toLowerCase().trim()
        filtered = filtered.filter(u => 
          (u.name && u.name.toLowerCase().includes(kw)) ||
          (u.username && u.username.toLowerCase().includes(kw)) ||
          (u.phone && u.phone.includes(kw))
        )
      }
      return { data: filtered, isMock: true }
    }
  },

  /**
   * Lấy chi tiết nhân viên
   */
  async getUserById(id) {
    try {
      const res = await apiClient.get(`/users/${id}`)
      return res.data
    } catch (err) {
      return MOCK_USERS.find(u => u.id === id) || null
    }
  },

  /**
   * Thêm mới nhân viên
   */
  async createUser(userData) {
    try {
      const res = await apiClient.post('/users', userData)
      return res.data
    } catch (err) {
      console.warn('Tạo bằng Mock:', err.message)
      const mockCreated = {
        ...userData,
        id: 'mock-' + Date.now(),
        createdAt: new Date().toISOString()
      }
      MOCK_USERS.unshift(mockCreated)
      return mockCreated
    }
  },

  /**
   * Cập nhật nhân viên
   */
  async updateUser(id, userData) {
    try {
      const res = await apiClient.put(`/users/${id}`, userData)
      return res.data
    } catch (err) {
      console.warn('Cập nhật bằng Mock:', err.message)
      const index = MOCK_USERS.findIndex(u => u.id === id)
      if (index !== -1) {
        MOCK_USERS[index] = { ...MOCK_USERS[index], ...userData }
        return MOCK_USERS[index]
      }
      return userData
    }
  },

  /**
   * Đổi trạng thái nhân viên (ACTIVE <-> INACTIVE)
   */
  async toggleStatus(id) {
    try {
      const res = await apiClient.patch(`/users/${id}/toggle-status`)
      return res.data
    } catch (err) {
      const user = MOCK_USERS.find(u => u.id === id)
      if (user) {
        user.status = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
        return user
      }
      return null
    }
  },

  /**
   * Xóa nhân viên
   */
  async deleteUser(id) {
    try {
      const res = await apiClient.delete(`/users/${id}`)
      return res
    } catch (err) {
      const index = MOCK_USERS.findIndex(u => u.id === id)
      if (index !== -1) {
        MOCK_USERS.splice(index, 1)
      }
      return { success: true }
    }
  }
}

