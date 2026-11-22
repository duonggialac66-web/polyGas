import apiClient from './api'

export const userService = {
  /**
   * Lấy danh sách nhân viên kèm tìm kiếm & lọc (100% dữ liệu thật từ SQL Server)
   * GET /api/v1/users
   * @param {Object} params { role, status, keyword }
   */
  async getUsers(params = {}) {
    const res = await apiClient.get('/users', { params })
    const list = res?.data !== undefined ? res.data : (Array.isArray(res) ? res : [])
    return { data: list }
  },

  /**
   * Lấy chi tiết nhân viên
   * GET /api/v1/users/{id}
   */
  async getUserById(id) {
    const res = await apiClient.get(`/users/${id}`)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Thêm mới nhân viên
   * POST /api/v1/users
   */
  async createUser(userData) {
    const res = await apiClient.post('/users', userData)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Cập nhật thông tin nhân viên
   * PUT /api/v1/users/{id}
   */
  async updateUser(id, userData) {
    const res = await apiClient.put(`/users/${id}`, userData)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Đổi trạng thái nhân viên (ACTIVE <-> INACTIVE)
   * PATCH /api/v1/users/{id}/toggle-status
   */
  async toggleStatus(id) {
    const res = await apiClient.patch(`/users/${id}/toggle-status`)
    return res?.data !== undefined ? res.data : res
  },

  /**
   * Xóa nhân viên
   * DELETE /api/v1/users/{id}
   */
  async deleteUser(id) {
    const res = await apiClient.delete(`/users/${id}`)
    return res
  }
}

export default userService
