-- =========================================================================
-- HỆ THỐNG POLYGAS ENTERPRISE - GAS DELIVERY MANAGEMENT SYSTEM
-- KỊCH BẢN KHỞI TẠO CSDL MICROSOFT SQL SERVER CHUẨN 3NF & KHÓA NGOẠI HOÀN CHỈNH
-- TỰ ĐỘNG XÓA BẢNG CŨ (DROP TABLES) ĐỂ ĐẢM BẢO CÁC CỘT ĐƯỢC TẠO CHUẨN NVARCHAR
-- =========================================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'polyGasDB')
BEGIN
    CREATE DATABASE [polyGasDB];
END
GO

USE [polyGasDB];
GO

-- =========================================================================
-- BƯỚC 1: XÓA BẢNG CŨ THEO THỨ TỰ RÀNG BUỘC KHÓA NGOẠI (CLEANUP CỘT VARCHAR CŨ)
-- =========================================================================
DROP TABLE IF EXISTS monthly_salary_inputs;
DROP TABLE IF EXISTS delivery_reports;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS salary_configs;
DROP TABLE IF EXISTS salary_formulas;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS inventories;
DROP TABLE IF EXISTS users;
GO

-- =========================================================================
-- BƯỚC 2: TẠO MỚI TOÀN BỘ CÁC BẢNG VỚI CỘT NVARCHAR CHO TIẾNG VIỆT CÓ DẤU
-- =========================================================================

-- 1. BẢNG USERS (Người dùng & Nhân viên)
CREATE TABLE users (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    name NVARCHAR(255) NOT NULL,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE',
    phone NVARCHAR(20) NULL,
    start_date DATE NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    base_salary FLOAT NOT NULL DEFAULT 0,
    avatar_url NVARCHAR(500) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

-- 2. BẢNG CUSTOMERS (Danh bạ Khách hàng)
CREATE TABLE customers (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(20) NULL,
    address NVARCHAR(500) NULL,
    latitude FLOAT NULL,
    longitude FLOAT NULL,
    notes NVARCHAR(MAX) NULL,
    name_hash NVARCHAR(255) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
CREATE UNIQUE NONCLUSTERED INDEX IX_Customers_NameHash ON customers(name_hash) WHERE name_hash IS NOT NULL;
GO

-- 3. BẢNG INVENTORIES (Kho hàng bình gas)
CREATE TABLE inventories (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    container_type NVARCHAR(100) NOT NULL UNIQUE,
    full_quantity INT NOT NULL DEFAULT 0 CHECK (full_quantity >= 0),
    empty_quantity INT NOT NULL DEFAULT 0 CHECK (empty_quantity >= 0),
    warning_threshold INT NOT NULL DEFAULT 10,
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

-- 4. BẢNG DELIVERY_REPORTS (Báo cáo Giao hàng - Khóa ngoại USERS, CUSTOMERS, INVENTORIES)
CREATE TABLE delivery_reports (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    employee_id UNIQUEIDENTIFIER NOT NULL FOREIGN KEY REFERENCES users(id),
    report_date DATE NOT NULL,
    customer_id UNIQUEIDENTIFIER NULL FOREIGN KEY REFERENCES customers(id),
    customer_name NVARCHAR(255) NOT NULL,
    inventory_id UNIQUEIDENTIFIER NOT NULL FOREIGN KEY REFERENCES inventories(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price FLOAT NOT NULL DEFAULT 0,
    total FLOAT NOT NULL DEFAULT 0,
    actual_received FLOAT NOT NULL DEFAULT 0,
    payment_status NVARCHAR(20) NOT NULL DEFAULT 'PAID',
    notes NVARCHAR(MAX) NULL,
    receipt_url NVARCHAR(500) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
CREATE NONCLUSTERED INDEX IX_DeliveryReports_Employee_Date ON delivery_reports(employee_id, report_date);
CREATE NONCLUSTERED INDEX IX_DeliveryReports_PaymentStatus ON delivery_reports(payment_status);
CREATE NONCLUSTERED INDEX IX_DeliveryReports_Inventory ON delivery_reports(inventory_id);
GO

-- 5. BẢNG EXPENSES (Chi phí phát sinh hàng ngày - Khóa ngoại USERS)
CREATE TABLE expenses (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    employee_id UNIQUEIDENTIFIER NOT NULL FOREIGN KEY REFERENCES users(id),
    expense_date DATE NOT NULL,
    description NVARCHAR(500) NOT NULL,
    amount FLOAT NOT NULL CHECK (amount > 0),
    notes NVARCHAR(MAX) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
CREATE NONCLUSTERED INDEX IX_Expenses_Employee_Date ON expenses(employee_id, expense_date);
GO

-- 6. BẢNG SALARY_FORMULAS (Công thức tính lương động SpEL)
CREATE TABLE salary_formulas (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    name NVARCHAR(255) NOT NULL,
    formula_expression NVARCHAR(1000) NOT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    effective_date DATE NOT NULL,
    description NVARCHAR(1000) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

-- 7. BẢNG MONTHLY_SALARY_INPUTS (Biến số & Bảng Lương Tháng - Khóa ngoại USERS & SALARY_FORMULAS)
CREATE TABLE monthly_salary_inputs (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    employee_id UNIQUEIDENTIFIER NOT NULL FOREIGN KEY REFERENCES users(id),
    salary_formula_id UNIQUEIDENTIFIER NULL FOREIGN KEY REFERENCES salary_formulas(id),
    month NVARCHAR(7) NOT NULL,
    work_days FLOAT NOT NULL DEFAULT 26.0,
    overtime FLOAT NOT NULL DEFAULT 1.0,
    bonus FLOAT NOT NULL DEFAULT 0,
    calculated_salary FLOAT NOT NULL DEFAULT 0,
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT UK_Salary_Employee_Month UNIQUE (employee_id, month)
);
CREATE NONCLUSTERED INDEX IX_Salary_Formula ON monthly_salary_inputs(salary_formula_id);
GO

-- 8. BẢNG SALARY_CONFIGS (Cấu hình hoa hồng - Khóa ngoại 1-1 với INVENTORIES)
CREATE TABLE salary_configs (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    inventory_id UNIQUEIDENTIFIER NOT NULL UNIQUE FOREIGN KEY REFERENCES inventories(id),
    commission_per_unit FLOAT NOT NULL DEFAULT 0,
    milestone_bonus_threshold INT NULL,
    milestone_bonus_amount FLOAT NULL,
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

-- =========================================================================
-- BƯỚC 3: NẠP 100% DỮ LIỆU MỚI (CÓ TIỀN TỐ N'...' CHO TIẾNG VIỆT CÓ DẤU CHUẨN)
-- =========================================================================

-- 1. TÀI KHOẢN NGƯỜI DÙNG (Mật khẩu mặc định: 123456 - Đã mã hóa BCrypt)
DECLARE @idAdmin UNIQUEIDENTIFIER = NEWID();
DECLARE @idShipper1 UNIQUEIDENTIFIER = NEWID();
DECLARE @idShipper2 UNIQUEIDENTIFIER = NEWID();
DECLARE @idAccountant UNIQUEIDENTIFIER = NEWID();

INSERT INTO users (id, name, username, password, role, phone, start_date, status, base_salary)
VALUES 
(@idAdmin, N'Quản trị viên PolyGas', 'admin', '$2a$10$7zBfWq2R9jA7K7k1rMv.yeJ1a4L1nO6hE9x2V3c4bYyR9Qc1O6789', 'ADMIN', '0901234567', '2026-01-01', 'ACTIVE', 15000000),
(@idShipper1, N'Nguyễn Văn Giao', 'shipper01', '$2a$10$7zBfWq2R9jA7K7k1rMv.yeJ1a4L1nO6hE9x2V3c4bYyR9Qc1O6789', 'EMPLOYEE', '0987654321', '2026-02-01', 'ACTIVE', 7000000),
(@idShipper2, N'Trần Văn Tốc', 'shipper02', '$2a$10$7zBfWq2R9jA7K7k1rMv.yeJ1a4L1nO6hE9x2V3c4bYyR9Qc1O6789', 'EMPLOYEE', '0933445566', '2026-03-01', 'ACTIVE', 6800000),
(@idAccountant, N'Trần Thị Hạnh', 'accountant01', '$2a$10$7zBfWq2R9jA7K7k1rMv.yeJ1a4L1nO6hE9x2V3c4bYyR9Qc1O6789', 'ADMIN', '0912345678', '2026-02-15', 'ACTIVE', 10000000);
GO

-- 2. KHO HÀNG & CẤU HÌNH HOA HỒNG (LIÊN KẾT KHÓA NGOẠI INVENTORY_ID)
DECLARE @idXam UNIQUEIDENTIFIER = NEWID();
DECLARE @idDo UNIQUEIDENTIFIER = NEWID();
DECLARE @id45kg UNIQUEIDENTIFIER = NEWID();
DECLARE @idMini UNIQUEIDENTIFIER = NEWID();

INSERT INTO inventories (id, container_type, full_quantity, empty_quantity, warning_threshold)
VALUES 
(@idXam, N'Bình 12kg Xám', 100, 30, 15),
(@idDo, N'Bình 12kg Đỏ', 80, 25, 15),
(@id45kg, N'Bình 45kg Công Nghiệp', 30, 10, 5),
(@idMini, N'Gas Lon Mini', 200, 50, 30);

INSERT INTO salary_configs (id, inventory_id, commission_per_unit, milestone_bonus_threshold, milestone_bonus_amount)
VALUES 
(NEWID(), @idXam, 15000, 100, 500000),
(NEWID(), @idDo, 15000, 100, 500000),
(NEWID(), @id45kg, 50000, 30, 1000000),
(NEWID(), @idMini, 2000, NULL, NULL);
GO

-- 3. CÔNG THỨC LƯƠNG CHUẨN SPEL
INSERT INTO salary_formulas (id, name, formula_expression, is_active, effective_date, description)
VALUES 
(NEWID(), N'Công thức lương chuẩn PolyGas 2026', 
 N'(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus', 
 1, '2026-01-01', 
 N'Công thức chuẩn: (Lương cơ bản / 26 * Ngày công) + (Hoa hồng * Hệ số tăng ca) + Thưởng/Phụ cấp');
GO

-- 4. DANH BẠ KHÁCH HÀNG THẬT (TỌA ĐỘ GPS & ĐỊA CHỈ TIẾNG VIỆT CÓ DẤU)
DECLARE @idCust1 UNIQUEIDENTIFIER = NEWID();
DECLARE @idCust2 UNIQUEIDENTIFIER = NEWID();
DECLARE @idCust3 UNIQUEIDENTIFIER = NEWID();
DECLARE @idCust4 UNIQUEIDENTIFIER = NEWID();

INSERT INTO customers (id, name, phone, address, latitude, longitude, notes, name_hash)
VALUES 
(@idCust1, N'Đại Lý Gas Hoàng Gia', '0912345678', N'128 Đường Lê Lợi, Quận 1, TP.HCM', 10.776889, 106.700806, N'Đại lý bán buôn, thanh toán chuyển khoản', 'dai-ly-gas-hoang-gia'),
(@idCust2, N'Nhà Hàng Biển Đông', '0908765432', N'54 Nguyễn Thị Minh Khai, Quận 3, TP.HCM', 10.781234, 106.694567, N'Giao trước 10h sáng hàng ngày, nợ vỏ bình 45kg', 'nha-hang-bien-dong'),
(@idCust3, N'Quán Cơm Niêu Sài Gòn', '0934567890', N'27 Tú Xương, Võ Thị Sáu, Quận 3, TP.HCM', 10.783456, 106.689012, N'Giao bình 12kg xám vào cửa sau khu bếp', 'quan-com-nieu-sai-gon'),
(@idCust4, N'Hộ Gia Đình Chị Mai', '0977889900', N'15/4 Cách Mạng Tháng 8, Phường 10, Quận 10, TP.HCM', 10.772345, 106.678901, N'Khách hộ gia đình, trả tiền mặt', 'ho-gia-dinh-chi-mai');
GO

-- 5. PHIẾU BÁO CÁO GIAO GAS HÔM NAY (LIÊN KẾT NHÂN VIÊN, KHO, KHÁCH HÀNG)
DECLARE @empShipper1 UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM users WHERE username = 'shipper01');
DECLARE @invXam UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM inventories WHERE container_type = N'Bình 12kg Xám');
DECLARE @invDo UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM inventories WHERE container_type = N'Bình 12kg Đỏ');
DECLARE @inv45 UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM inventories WHERE container_type = N'Bình 45kg Công Nghiệp');

DECLARE @cComNieu UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM customers WHERE name_hash = 'quan-com-nieu-sai-gon');
DECLARE @cBienDong UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM customers WHERE name_hash = 'nha-hang-bien-dong');
DECLARE @cMai UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM customers WHERE name_hash = 'ho-gia-dinh-chi-mai');

INSERT INTO delivery_reports (id, employee_id, report_date, customer_id, customer_name, inventory_id, quantity, unit_price, total, actual_received, payment_status, notes)
VALUES 
(NEWID(), @empShipper1, CAST(GETDATE() AS DATE), @cComNieu, N'Quán Cơm Niêu Sài Gòn', @invXam, 2, 350000, 700000, 700000, 'PAID', N'Giao tại bếp tầng trệt, khách trả tiền mặt đủ'),
(NEWID(), @empShipper1, CAST(GETDATE() AS DATE), @cBienDong, N'Nhà Hàng Biển Đông', @inv45, 1, 1250000, 1250000, 0, 'DEBT', N'Khách hẹn chiều 17h ghé lấy tiền mặt, nợ 1 vỏ 45kg chưa trả'),
(NEWID(), @empShipper1, CAST(GETDATE() AS DATE), @cMai, N'Hộ Gia Đình Chị Mai', @invDo, 1, 360000, 360000, 360000, 'PAID', N'Khách thanh toán qua mã QR');
GO

-- 6. CHI PHÍ PHÁT SINH CA TRỰC HÔM NAY
DECLARE @empShipper1_exp UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM users WHERE username = 'shipper01');

INSERT INTO expenses (id, employee_id, expense_date, description, amount, notes)
VALUES 
(NEWID(), @empShipper1_exp, CAST(GETDATE() AS DATE), N'Đổ xăng xe máy giao gas ca sáng', 65000, N'Cây xăng Petrolimex số 3'),
(NEWID(), @empShipper1_exp, CAST(GETDATE() AS DATE), N'Vá săm xe máy đường Lê Lợi', 30000, N'Cán đinh khi đang chở bình 45kg');
GO

PRINT N'>>> [POLYGAS ENTERPRISE] ĐÃ KHỞI TẠO THÀNH CÔNG 100% CSDL VỚI CỘT NVARCHAR VÀ DỮ LIỆU CÓ DẤU CHUẨN!';
