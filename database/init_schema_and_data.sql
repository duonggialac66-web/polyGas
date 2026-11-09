-- =========================================================================
-- HỆ THỐNG ORIGINGAS ENTERPRISE - GAS DELIVERY MANAGEMENT SYSTEM
-- KỊCH BẢN KHỞI TẠO CSDL MICROSOFT SQL SERVER CHUẨN 3NF & KHÓA NGOẠI HOÀN CHỈNH
-- =========================================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'OriginGasDB')
BEGIN
    CREATE DATABASE [OriginGasDB];
END
GO

USE [OriginGasDB];
GO

-- 1. BẢNG USERS (Người dùng & Nhân viên)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
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
END
GO

-- 2. BẢNG CUSTOMERS (Danh bạ Khách hàng)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'customers')
BEGIN
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
END
GO

-- 3. BẢNG INVENTORIES (Kho hàng bình gas)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'inventories')
BEGIN
    CREATE TABLE inventories (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        container_type NVARCHAR(100) NOT NULL UNIQUE,
        full_quantity INT NOT NULL DEFAULT 0 CHECK (full_quantity >= 0),
        empty_quantity INT NOT NULL DEFAULT 0 CHECK (empty_quantity >= 0),
        warning_threshold INT NOT NULL DEFAULT 10,
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

-- 4. BẢNG DELIVERY_REPORTS (Báo cáo Giao hàng - Liên kết Khóa ngoại với USERS, CUSTOMERS, INVENTORIES)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'delivery_reports')
BEGIN
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
END
GO

-- 5. BẢNG EXPENSES (Chi phí phát sinh hàng ngày - Khóa ngoại USERS)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'expenses')
BEGIN
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
END
GO

-- 6. BẢNG SALARY_FORMULAS (Công thức tính lương động SpEL)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'salary_formulas')
BEGIN
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
END
GO

-- 7. BẢNG MONTHLY_SALARY_INPUTS (Biến số & Bảng Lương Tháng - Khóa ngoại USERS & SALARY_FORMULAS)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'monthly_salary_inputs')
BEGIN
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
END
GO

-- 8. BẢNG SALARY_CONFIGS (Cấu hình hoa hồng - Khóa ngoại 1-1 với INVENTORIES)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'salary_configs')
BEGIN
    CREATE TABLE salary_configs (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        inventory_id UNIQUEIDENTIFIER NOT NULL UNIQUE FOREIGN KEY REFERENCES inventories(id),
        commission_per_unit FLOAT NOT NULL DEFAULT 0,
        milestone_bonus_threshold INT NULL,
        milestone_bonus_amount FLOAT NULL,
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

-- =========================================================================
-- DỮ LIỆU BAN ĐẦU MẪU (INITIAL SEED DATA VỚI KHÓA NGOẠI CHUẨN)
-- =========================================================================

-- TÀI KHOẢN ADMIN & SHIPPER
IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (id, name, username, password, role, phone, start_date, status, base_salary)
    VALUES (NEWID(), N'Quản trị viên OriginGas', 'admin', '$2a$10$7zBfWq2R9jA7K7k1rMv.yeJ1a4L1nO6hE9x2V3c4bYyR9Qc1O6789', 'ADMIN', '0901234567', '2026-01-01', 'ACTIVE', 15000000);
END

IF NOT EXISTS (SELECT * FROM users WHERE username = 'shipper01')
BEGIN
    INSERT INTO users (id, name, username, password, role, phone, start_date, status, base_salary)
    VALUES (NEWID(), N'Nguyễn Văn Giao', 'shipper01', '$2a$10$7zBfWq2R9jA7K7k1rMv.yeJ1a4L1nO6hE9x2V3c4bYyR9Qc1O6789', 'EMPLOYEE', '0987654321', '2026-02-01', 'ACTIVE', 7000000);
END

-- KHO HÀNG & CẤU HÌNH HOA HỒNG (LIÊN KẾT KHÓA NGOẠI INVENTORY_ID)
DECLARE @idXam UNIQUEIDENTIFIER = NEWID();
DECLARE @idDo UNIQUEIDENTIFIER = NEWID();
DECLARE @id45kg UNIQUEIDENTIFIER = NEWID();
DECLARE @idMini UNIQUEIDENTIFIER = NEWID();

IF NOT EXISTS (SELECT * FROM inventories WHERE container_type = N'Bình 12kg Xám')
BEGIN
    INSERT INTO inventories (id, container_type, full_quantity, empty_quantity, warning_threshold)
    VALUES (@idXam, N'Bình 12kg Xám', 100, 30, 15);

    INSERT INTO salary_configs (id, inventory_id, commission_per_unit, milestone_bonus_threshold, milestone_bonus_amount)
    VALUES (NEWID(), @idXam, 15000, 100, 500000);
END

IF NOT EXISTS (SELECT * FROM inventories WHERE container_type = N'Bình 12kg Đỏ')
BEGIN
    INSERT INTO inventories (id, container_type, full_quantity, empty_quantity, warning_threshold)
    VALUES (@idDo, N'Bình 12kg Đỏ', 80, 25, 15);

    INSERT INTO salary_configs (id, inventory_id, commission_per_unit, milestone_bonus_threshold, milestone_bonus_amount)
    VALUES (NEWID(), @idDo, 15000, 100, 500000);
END

IF NOT EXISTS (SELECT * FROM inventories WHERE container_type = N'Bình 45kg Công Nghiệp')
BEGIN
    INSERT INTO inventories (id, container_type, full_quantity, empty_quantity, warning_threshold)
    VALUES (@id45kg, N'Bình 45kg Công Nghiệp', 30, 10, 5);

    INSERT INTO salary_configs (id, inventory_id, commission_per_unit, milestone_bonus_threshold, milestone_bonus_amount)
    VALUES (NEWID(), @id45kg, 50000, 30, 1000000);
END

IF NOT EXISTS (SELECT * FROM inventories WHERE container_type = N'Gas Lon Mini')
BEGIN
    INSERT INTO inventories (id, container_type, full_quantity, empty_quantity, warning_threshold)
    VALUES (@idMini, N'Gas Lon Mini', 200, 50, 30);

    INSERT INTO salary_configs (id, inventory_id, commission_per_unit)
    VALUES (NEWID(), @idMini, 2000);
END

-- CÔNG THỨC LƯƠNG CHUẨN SPEL
IF NOT EXISTS (SELECT * FROM salary_formulas WHERE name = N'Công thức lương chuẩn OriginGas 2026')
BEGIN
    INSERT INTO salary_formulas (id, name, formula_expression, is_active, effective_date, description)
    VALUES (NEWID(), N'Công thức lương chuẩn OriginGas 2026', 
            N'(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus', 
            1, '2026-01-01', 
            N'Công thức chuẩn: (Lương cơ bản / 26 * Ngày công) + (Hoa hồng * Hệ số tăng ca) + Thưởng/Phụ cấp');
END
GO
