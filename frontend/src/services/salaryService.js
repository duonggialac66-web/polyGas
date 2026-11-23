import apiClient from './api'

// Dữ liệu mẫu dự phòng (fallback khi backend chưa khởi động)
const MOCK_SALARY_FORMULA = {
  id: 'f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01',
  name: 'Công thức lương chuẩn OriginGas 2026',
  formulaExpression: '(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus',
  isActive: true,
  effectiveDate: '2026-01-01',
  description: 'Công thức chuẩn: (Lương cơ bản / 26 * Ngày công) + (Hoa hồng * Hệ số tăng ca) + Thưởng/Phụ cấp'
}

const MOCK_SALARY_CONFIGS = [
  { containerType: 'Bình 12kg Xám', commissionPerUnit: 15000, milestoneBonusThreshold: 100, milestoneBonusAmount: 500000 },
  { containerType: 'Bình 12kg Đỏ', commissionPerUnit: 15000, milestoneBonusThreshold: 100, milestoneBonusAmount: 500000 },
  { containerType: 'Bình 45kg Công Nghiệp', commissionPerUnit: 50000, milestoneBonusThreshold: 30, milestoneBonusAmount: 1000000 },
  { containerType: 'Gas Lon Mini', commissionPerUnit: 2000, milestoneBonusThreshold: null, milestoneBonusAmount: null }
]

// Bảng dữ liệu lương mẫu theo tháng
let MOCK_MONTHLY_SALARIES = [
  {
    id: 's1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01',
    employeeId: 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
    employeeName: 'Nguyễn Văn Giao',
    username: 'shipper01',
    phone: '0987654321',
    role: 'EMPLOYEE',
    month: '2026-11',
    baseSalary: 7000000,
    workDays: 26.0,
    overtime: 1.2,
    bonus: 500000,
    deliveredCylinders: 115,
    commissionTotal: 2225000, // 115 bình * 15.000 + 500.000 thưởng mốc 100 bình
    calculatedSalary: 10170000, // (7tr/26*26) + (2.225tr * 1.2) + 500k
    formulaName: 'Công thức lương chuẩn OriginGas 2026',
    formulaExpression: '(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus',
    status: 'CALCULATED',
    updatedAt: '2026-11-21T10:00:00'
  },
  {
    id: 's2eebc99-9c0b-4ef8-bb6d-6bb9bd380b02',
    employeeId: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    employeeName: 'Quản trị viên OriginGas',
    username: 'admin',
    phone: '0901234567',
    role: 'ADMIN',
    month: '2026-11',
    baseSalary: 15000000,
    workDays: 26.0,
    overtime: 1.0,
    bonus: 2000000,
    deliveredCylinders: 0,
    commissionTotal: 0,
    calculatedSalary: 17000000, // (15tr/26*26) + 0 + 2tr
    formulaName: 'Công thức lương chuẩn OriginGas 2026',
    formulaExpression: '(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus',
    status: 'CALCULATED',
    updatedAt: '2026-11-21T10:00:00'
  },
  {
    id: 's3eebc99-9c0b-4ef8-bb6d-6bb9bd380b03',
    employeeId: 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33',
    employeeName: 'Trần Thị Hạnh (Kế toán)',
    username: 'accountant01',
    phone: '0912345678',
    role: 'ADMIN',
    month: '2026-11',
    baseSalary: 10000000,
    workDays: 24.0,
    overtime: 1.0,
    bonus: 500000,
    deliveredCylinders: 0,
    commissionTotal: 0,
    calculatedSalary: 9730769, // (10tr/26*24) + 0 + 500k
    formulaName: 'Công thức lương chuẩn OriginGas 2026',
    formulaExpression: '(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus',
    status: 'CALCULATED',
    updatedAt: '2026-11-21T10:00:00'
  },
  {
    id: 's4eebc99-9c0b-4ef8-bb6d-6bb9bd380b04',
    employeeId: 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44',
    employeeName: 'Lê Hoàng Nam (Giao nhận)',
    username: 'shipper02',
    phone: '0933445566',
    role: 'EMPLOYEE',
    month: '2026-11',
    baseSalary: 6500000,
    workDays: 20.0,
    overtime: 1.0,
    bonus: 0,
    deliveredCylinders: 60,
    commissionTotal: 900000, // 60 * 15k
    calculatedSalary: 5900000, // (6.5tr/26*20) + 900k + 0
    formulaName: 'Công thức lương chuẩn OriginGas 2026',
    formulaExpression: '(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus',
    status: 'CALCULATED',
    updatedAt: '2026-11-21T10:00:00'
  }
]

export const salaryService = {
  /**
   * Lấy công thức tính lương SpEL đang kích hoạt
   */
  async getActiveFormula() {
    try {
      const res = await apiClient.get('/salaries/formula')
      if (res && res.data) {
        return { data: res.data, isMock: false }
      }
      return { data: MOCK_SALARY_FORMULA, isMock: true }
    } catch (err) {
      console.warn('Backend chưa phản hồi /salaries/formula, dùng Mock:', err.message)
      return { data: MOCK_SALARY_FORMULA, isMock: true }
    }
  },

  /**
   * Lấy cấu hình đơn giá hoa hồng gas
   */
  async getSalaryConfigs() {
    try {
      const res = await apiClient.get('/salaries/configs')
      if (res && res.data) {
        return { data: res.data, isMock: false }
      }
      return { data: MOCK_SALARY_CONFIGS, isMock: true }
    } catch (err) {
      return { data: MOCK_SALARY_CONFIGS, isMock: true }
    }
  },

  /**
   * Lấy bảng lương / lịch sử lương theo tháng (YYYY-MM)
   * GET /api/v1/salaries/history?month=YYYY-MM
   */
  async getSalariesByMonth(month = '2026-11') {
    try {
      const res = await apiClient.get('/salaries/history', { params: { month } })
      if (res && res.data) {
        return { data: res.data, isMock: false }
      }
      return { data: [], isMock: false }
    } catch (err) {
      console.warn('Backend chưa phản hồi /salaries/history, dùng Mock:', err.message)
      const filtered = MOCK_MONTHLY_SALARIES.filter(s => s.month === month)
      return { 
        data: filtered.length > 0 ? filtered : MOCK_MONTHLY_SALARIES, 
        isMock: true 
      }
    }
  },

  /**
   * Chạy thuật toán tính lương tháng SpEL Engine
   * POST /api/v1/salaries/calculate?month=YYYY-MM
   */
  async calculateSalary(month = '2026-11', employeeId = null) {
    try {
      const params = { month }
      if (employeeId) params.employeeId = employeeId
      const res = await apiClient.post('/salaries/calculate', null, { params })
      return { data: res.data, isMock: false }
    } catch (err) {
      console.warn('Tính lương bằng mô phỏng SpEL Client:', err.message)
      
      // Mô phỏng tính toán SpEL Engine chuẩn trên Client:
      // (#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus
      MOCK_MONTHLY_SALARIES = MOCK_MONTHLY_SALARIES.map(item => {
        if (!employeeId || item.employeeId === employeeId) {
          const baseComponent = (item.baseSalary / 26) * item.workDays
          const commissionComponent = item.commissionTotal * item.overtime
          const total = Math.round(baseComponent + commissionComponent + item.bonus)
          return {
            ...item,
            month,
            calculatedSalary: total,
            status: 'CALCULATED',
            updatedAt: new Date().toISOString()
          }
        }
        return item
      })

      return {
        data: MOCK_MONTHLY_SALARIES,
        message: 'Tính toán SpEL Engine hoàn tất thành công (Chế độ mô phỏng trực tiếp)',
        isMock: true
      }
    }
  },

  /**
   * Cập nhật biến số chấm công của nhân viên theo tháng
   * PUT /api/v1/salaries/{employeeId}/{month}
   */
  async updateSalaryInput(employeeId, month, inputData) {
    try {
      const res = await apiClient.put(`/salaries/${employeeId}/${month}`, inputData)
      return { data: res.data, isMock: false }
    } catch (err) {
      console.warn('Cập nhật biến số chấm công bằng Mock:', err.message)
      const index = MOCK_MONTHLY_SALARIES.findIndex(
        s => s.employeeId === employeeId && s.month === month
      )
      if (index !== -1) {
        const item = MOCK_MONTHLY_SALARIES[index]
        const workDays = inputData.workDays !== undefined ? Number(inputData.workDays) : item.workDays
        const overtime = inputData.overtime !== undefined ? Number(inputData.overtime) : item.overtime
        const bonus = inputData.bonus !== undefined ? Number(inputData.bonus) : item.bonus

        // Tự động tính lại luôn sau khi sửa biến số
        const baseComponent = (item.baseSalary / 26) * workDays
        const commissionComponent = item.commissionTotal * overtime
        const calculatedSalary = Math.round(baseComponent + commissionComponent + bonus)

        MOCK_MONTHLY_SALARIES[index] = {
          ...item,
          workDays,
          overtime,
          bonus,
          calculatedSalary,
          updatedAt: new Date().toISOString()
        }
        return { data: MOCK_MONTHLY_SALARIES[index], isMock: true }
      }
      return { data: inputData, isMock: true }
    }
  }
}

export default salaryService
