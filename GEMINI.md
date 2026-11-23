# 📌 NHIỆM VỤ DỰ ÁN ORIGINGAS ENTERPRISE — HÀ TRỌNG PHÚC

## 👤 Thông tin Thành viên & Phân hệ
* **Thành viên:** Hà Trọng Phúc (PC11019)
* **Vai trò:** Phụ trách Phân hệ **Lương & Quản lý Nhân sự** (Salary & User Management)
* **Nhánh Git làm việc:** `feature/salary-user-management_Phuc` (hoặc `feature/salary-user-management`)
* **Kiến trúc:** Spring Boot 3.2.5 (Java 21 LTS), Spring Data JPA, Spring Security 6, SpEL Engine

---

## 📂 Các File & Entity Liên Quan
1. `src/main/java/poly/duantn/entity/User.java`: Thực thể người dùng & nhân sự (role, status, baseSalary, phone, startDate).
2. `src/main/java/poly/duantn/entity/MonthlySalaryInput.java`: Biến số chấm công và kết quả lương tháng (employee, salaryFormula, month, workDays, overtime, bonus, calculatedSalary).
3. `src/main/java/poly/duantn/entity/SalaryConfig.java`: Cấu hình đơn giá hoa hồng theo loại bình gas (inventory, commissionPerUnit, milestoneBonusThreshold, milestoneBonusAmount).
4. `src/main/java/poly/duantn/entity/SalaryFormula.java`: Công thức tính lương động SpEL (formulaExpression, isActive, effectiveDate).
5. `src/main/java/poly/duantn/entity/DeliveryReport.java`: Báo cáo giao gas hàng ngày (dùng để đếm sản lượng bình giao trong tháng tính hoa hồng).

---

## 📦 DTO Cần Triển Khai (`poly.duantn.dto`)
1. **`CreateEmployeeRequest`**:
   * `name`: Họ và tên nhân viên
   * `username`: Tên đăng nhập (duy nhất)
   * `password`: Mật khẩu thô (băm BCrypt trước khi lưu)
   * `phone`: Số điện thoại
   * `baseSalary`: Lương cơ bản
   * `startDate`: Ngày bắt đầu làm việc
2. **`UpdateSalaryInputRequest`**:
   * `workDays`: Số ngày công thực tế (mặc định 26.0)
   * `overtime`: Hệ số tăng ca (mặc định 1.0)
   * `bonus`: Thưởng thêm / phụ cấp phát sinh
3. **`SalaryResultResponse`**:
   * Thông tin chi tiết bảng lương tháng của nhân viên sau khi tính toán (thông tin nhân viên, tháng, công thức áp dụng, ngày công, tăng ca, thưởng, hoa hồng giao gas, tổng lương thực nhận).

---

## 🚀 Danh Sách API Cần Viết

### 1. Quản lý Nhân sự (`UserController.java`)
* `GET /api/v1/users`: Quản lý danh sách nhân sự công ty, cập nhật lương cơ bản, bật/tắt trạng thái làm việc (ACTIVE / INACTIVE).
* `POST /api/v1/users`: Thêm nhân viên mới (bắt buộc mã hóa mật khẩu bằng `passwordEncoder.encode()`).
* `PUT /api/v1/users/{id}`: Cập nhật thông tin nhân viên.
* `PATCH /api/v1/users/{id}/toggle-status`: Bật/tắt trạng thái làm việc.

### 2. Quản lý & Tính Lương SpEL (`SalaryController.java`)
* `POST /api/v1/salaries/calculate`: Chạy thuật toán tính lương tháng cho nhân viên theo tháng chỉ định (`YYYY-MM`).
* `GET /api/v1/salaries/history`: Xem lịch sử bảng lương các tháng trước.
* `PUT /api/v1/salaries/{employeeId}/{month}`: Cập nhật biến số chấm công (`workDays`, `overtime`, `bonus`) cho nhân viên.

---

## ⚙️ Logic Cốt Lõi: Bộ Máy Tính Lương SpEL (SpEL Salary Engine)
1. **Lấy công thức chuẩn:** Gọi `SalaryFormulaRepository.findByIsActiveTrue()`.
   * Công thức mẫu: `(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus`
2. **Tính tổng hoa hồng (`#commissionTotal`):**
   * Đếm tổng số bình gas mà shipper đã giao thành công trong tháng chỉ định từ `delivery_reports`.
   * Nhân với đơn giá hoa hồng tương ứng trong `salary_configs` theo từng loại bình gas (kèm thưởng mốc số lượng nếu đạt ngưỡng).
3. **Thực thi tính toán bằng Spring SpEL:**
   * Khởi tạo `SpelExpressionParser` và `StandardEvaluationContext`.
   * Đặt các biến số vào ngữ cảnh: `#baseSalary`, `#workDays`, `#overtime`, `#bonus`, `#commissionTotal`.
   * Đánh giá biểu thức công thức để ra kết quả `calculated_salary`.
4. **Lưu vết lịch sử (Audit Trail):**
   * Lưu hoặc cập nhật vào bảng `monthly_salary_inputs` với khóa ngoại `salary_formula_id`, `employee_id`, `month`, `calculated_salary`, `updated_at`.
