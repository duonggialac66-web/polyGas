# OriginGas Enterprise — Gas Delivery Management System

Hệ thống Quản lý Phân phối & Giao hàng Gas chuyên nghiệp cho Doanh nghiệp (LPG).

## 🚀 Công nghệ sử dụng
- **Ngôn ngữ**: Java 21 LTS
- **Framework**: Spring Boot 3.2.5 (Spring Web RESTful API, Spring Data JPA, Spring Security, Validation)
- **Hệ quản trị CSDL**: Microsoft SQL Server
- **Công cụ & Thư viện**: Lombok, HikariCP Connection Pool

## 📂 Kiến trúc Thực thể (JPA Entities)
Hệ thống được thiết kế theo chuẩn Clean Architecture 3NF với 8 thực thể chính:
1. `User`: Quản trị viên (ADMIN) và Nhân viên giao hàng (EMPLOYEE).
2. `Customer`: Quản lý khách hàng, tọa độ GPS (latitude, longitude), tìm kiếm siêu tốc với `name_hash`.
3. `DeliveryReport`: Báo cáo giao gas lớn/lon, trừ kho tự động, quản lý công nợ (PAID / DEBT).
4. `Inventory`: Quản lý kho bình gas đầy và vỏ bình rỗng.
5. `Expense`: Quản lý chi phí phát sinh hàng ngày (xăng, ăn trưa, sửa xe) để đối soát tiền mặt.
6. `MonthlySalaryInput`: Biến số ngày công, tăng ca, thưởng phạt và tính lương tháng.
7. `SalaryConfig`: Cấu hình đơn giá hoa hồng theo từng loại bình.
8. `SalaryFormula`: Biểu thức công thức lương SpEL (Spring Expression Language) linh hoạt.

## 🛠️ Hướng dẫn cài đặt & chạy dự án cho Developer
1. Clone dự án về máy:
   ```bash
   git clone https://github.com/duonggialac66-web/polyGas.git
   ```
2. Mở dự án trong Eclipse / Spring Tool Suite (STS) hoặc IntelliJ IDEA dưới dạng **Existing Maven Project**.
3. Cấu hình cơ sở dữ liệu Microsoft SQL Server trong file `src/main/resources/application.properties`.
4. Chạy file `DuAnTnApplication.java` hoặc chạy lệnh:
   ```bash
   mvn spring-boot:run
   ```

