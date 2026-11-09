# 🚀 ORIGINGAS ENTERPRISE — HỆ THỐNG QUẢN LÝ GIAO HÀNG & CHUỖI PHÂN PHỐI GAS

Dự án Đồ án Tốt nghiệp được xây dựng theo **Kiến trúc Tách biệt Frontend – Backend (Decoupled Architecture / SPA + RESTful Web API)**.

* **Backend:** Java 21 LTS • Spring Boot 3.2.5 • Spring Data JPA • Spring Security 6 (Stateless JWT)
* **Database:** Microsoft SQL Server (8 Bảng chuẩn hóa 3NF • Khóa ngoại UUID toàn diện)
* **Frontend:** React 18 • Vite • TailwindCSS (Single Page Application - SPA)

---

## 📋 1. YÊU CẦU MÔI TRƯỜNG PHÁT TRIỂN (PREREQUISITES)

Mỗi thành viên trong nhóm cần cài đặt sẵn trên máy tính:
1. **JDK 21 LTS** (Khuyến nghị Eclipse Temurin hoặc Oracle JDK 21).
2. **Microsoft SQL Server** (Bản 2019/2022 Developer hoặc Express) + **SSMS** (SQL Server Management Studio).
3. **IDE:** Eclipse IDE for Enterprise Java Developers, IntelliJ IDEA hoặc Visual Studio Code.
4. **Git:** Đã cài đặt Git Bash.

---

## 🗄️ 2. HƯỚNG DẪN KHỞI TẠO CƠ SỞ DỮ LIỆU (DATABASE SETUP)

1. Mở **SQL Server Management Studio (SSMS)** và kết nối tới SQL Server của bạn.
2. Mở file script có sẵn trong dự án: `database/init_schema_and_data.sql`.
3. Bấm **Execute (F5)** để tự động tạo cơ sở dữ liệu `polyGasDB`, 8 bảng quan hệ và nạp toàn bộ dữ liệu mẫu ban đầu.
4. Kiểm tra cấu hình kết nối trong file `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=polyGasDB;encrypt=true;trustServerCertificate=true
   spring.datasource.username=sa
   spring.datasource.password=123
   ```
   *(Nếu mật khẩu SQL Server của bạn khác `123`, vui lòng cập nhật lại cho đúng máy của bạn).*

### Tài khoản mẫu đã seed sẵn trong CSDL:
* **Quản trị viên (ADMIN):** `admin` / mật khẩu `123456`
* **Nhân viên giao gas (EMPLOYEE):** `shipper01` / mật khẩu `123456`

---

## 💻 3. HƯỚNG DẪN CLONE & IMPORT VÀO IDE

### Bước 1: Clone dự án về máy
```bash
git clone https://github.com/duonggialac66-web/polyGas.git
cd polyGas
```

### Bước 2: Import vào Eclipse IDE
1. Mở **Eclipse**, vào menu: `File` $\rightarrow$ `Import...`
2. Chọn `Maven` $\rightarrow$ `Existing Maven Projects` $\rightarrow$ Bấm `Next`.
3. Tại ô **Root Directory**, bấm `Browse...` và trỏ tới thư mục `polyGas` (hoặc `DuAnTN`) vừa clone về.
4. Tích chọn file `pom.xml` $\rightarrow$ Bấm `Finish`.
5. Đợi Eclipse tải các thư viện Maven (nhìn góc dưới bên phải màn hình khi nào chạy xong 100%).

### Bước 3: Chạy ứng dụng Backend
1. Tìm đến file: `src/main/java/poly/duantn/DuAnTnApplication.java`.
2. Chuột phải $\rightarrow$ `Run As` $\rightarrow$ `Spring Boot App` (hoặc `Java Application`).
3. Mở trình duyệt truy cập: `http://localhost:8080` (Cổng mặc định).

---

## 📁 4. CẤU TRÚC PACKAGE (QUY ƯỚC VIẾT CODE)

Toàn bộ mã nguồn backend đã được phân chia package rõ ràng theo mô hình **Clean Architecture**:

```
src/main/java/poly/duantn/
├── config/              # Cấu hình hệ thống (CORS, PasswordEncoder, Seeder CSDL)
├── controller/          # [DEV] Viết các REST Controller tiếp nhận API (@RestController)
├── dto/                 # [DEV] Tạo các Request / Response DTO (đã có sẵn ApiResponse<T>)
├── entity/              # [ĐÃ HOÀN TẤT] 8 Entity CSDL chuẩn 3NF và các Enum
├── exception/           # [NỀN TẢNG] Bắt lỗi tự động (GlobalExceptionHandler, BadRequest, NotFound)
├── repository/          # [ĐÃ HOÀN TẤT] 8 Repository JPA truy vấn dữ liệu
├── security/            # [NỀN TẢNG] Cấu hình bảo mật Spring Security 6 (Stateless JWT)
├── service/             # [DEV] Khai báo các Service Interface nghiệp vụ
│   └── impl/            # [DEV] Cài đặt logic nghiệp vụ chi tiết (@Service @Transactional)
├── util/                # [TIỆN ÍCH] NameHashUtil chuẩn hóa tiếng Việt, DateUtils
└── DuAnTnApplication.java
```

### Quy tắc khi dev viết code:
* **Trả về API:** Sử dụng lớp `ApiResponse<T>` chuẩn hóa:
  ```java
  return ResponseEntity.ok(ApiResponse.ok(data, "Lấy danh sách thành công"));
  ```
* **Báo lỗi nghiệp vụ:** Ném thẳng Exception, không cần try-catch tại Controller:
  ```java
  throw new BadRequestException("Số lượng bình tồn kho không đủ!");
  throw new ResourceNotFoundException("Khách hàng", "id", id);
  ```

---

## 🌿 5. QUY ƯỚC GIT ĐỂ TRÁNH XUNG ĐỘT (AVOID MERGE CONFLICTS)

Để tránh bị đè code hoặc xung đột khi nhiều thành viên cùng làm việc:

1. **TUYỆT ĐỐI KHÔNG COMMIT TRỰC TIẾP LÊN NHÁNH `main`**.
2. **Mỗi thành viên tạo một nhánh riêng cho module của mình:**
   ```bash
   # Cập nhật code mới nhất từ main
   git checkout main
   git pull origin main

   # Tạo nhánh mới làm việc (ví dụ Module 1 Auth)
   git checkout -b feature/auth-module
   ```
3. **Quy ước đặt tên nhánh:**
   * `feature/auth-user` (Module 1: Đăng nhập & Nhân sự)
   * `feature/customer-gps` (Module 2: Khách hàng & Tọa độ)
   * `feature/delivery-inventory` (Module 3: Báo cáo giao hàng & Kho)
   * `feature/expense-daily` (Module 4: Chi phí phát sinh)
   * `feature/salary-spel` (Module 5: Tính lương & Công thức SpEL)

4. **Trước khi đẩy code (Push) lên GitHub:**
   ```bash
   git add .
   git commit -m "feat(module): Mô tả ngắn gọn tính năng vừa hoàn thành"
   git pull origin main --rebase
   git push origin feature/<tên-nhánh-của-bạn>
   ```
5. Báo nhóm trưởng tạo Pull Request (PR) kiểm tra và merge vào nhánh `main`.

---

## 📊 6. TÀI LIỆU THAM KHẢO TRONG DỰ ÁN

* **Sơ đồ Thực thể Quan hệ & Kiến trúc:** Mở file `ERD_Viewer.html` trên trình duyệt.
* **Tài liệu Đặc tả Yêu cầu Kỹ thuật:** File `SRS_OriginGas.docx`.
* **Kế hoạch Phân công 12 Tuần:** File `KE_HOACH_PHAN_CONG_THEO_MODULE_VA_TUAN.xlsx`.
