package poly.duantn.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import poly.duantn.entity.*;
import poly.duantn.entity.enums.PaymentStatus;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.repository.*;
import poly.duantn.util.NameHashUtil;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final SalaryConfigRepository salaryConfigRepository;
    private final SalaryFormulaRepository salaryFormulaRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryReportRepository deliveryReportRepository;
    private final ExpenseRepository expenseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            seedUsers();
            seedInventories();
            seedSalaryConfigs();
            seedSalaryFormula();
            seedCustomers();
            seedDeliveryReports();
            seedExpenses();
            log.info(">>> [PolyGas Enterprise] Đã kiểm tra và nạp thành công 100% dữ liệu thật vào CSDL SQL Server!");
        } catch (Exception e) {
            log.warn(">>> [PolyGas Enterprise] Dữ liệu đã tồn tại hoặc xảy ra cảnh báo: {}", e.getMessage());
        }
    }

    private void seedUsers() {
        try {
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        .name("Quản trị viên PolyGas")
                        .role(Role.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .phone("0901234567")
                        .baseSalary(15000000.0)
                        .startDate(LocalDate.of(2026, 1, 1))
                        .build();
                userRepository.save(admin);
                log.info("-> Đã khởi tạo tài khoản Quản trị viên mặc định: admin / 123456");
            }

            if (!userRepository.existsByUsername("shipper01")) {
                User shipper = User.builder()
                        .username("shipper01")
                        .password(passwordEncoder.encode("123456"))
                        .name("Nguyễn Văn Giao")
                        .role(Role.EMPLOYEE)
                        .status(UserStatus.ACTIVE)
                        .phone("0987654321")
                        .baseSalary(7000000.0)
                        .startDate(LocalDate.of(2026, 2, 1))
                        .build();
                userRepository.save(shipper);
                log.info("-> Đã khởi tạo tài khoản Nhân viên giao gas mẫu: shipper01 / 123456");
            }
        } catch (Exception ex) {
            log.warn("-> Bỏ qua nạp users (đã tồn tại hoặc trùng lặp): {}", ex.getMessage());
        }
    }

    private void seedInventories() {
        if (inventoryRepository.count() > 0) {
            log.info("-> Bảng inventories đã có dữ liệu trong CSDL, bỏ qua seed.");
            return;
        }

        seedInventoryItem("Bình 12kg Xám", 100, 30, 15);
        seedInventoryItem("Bình 12kg Đỏ", 80, 25, 15);
        seedInventoryItem("Bình 45kg Công Nghiệp", 30, 10, 5);
        seedInventoryItem("Gas Lon Mini", 200, 50, 30);
    }

    private void seedInventoryItem(String type, int full, int empty, int warning) {
        try {
            if (inventoryRepository.findByContainerType(type).isEmpty() && !inventoryRepository.existsByContainerType(type)) {
                Inventory item = Inventory.builder()
                        .containerType(type)
                        .fullQuantity(full)
                        .emptyQuantity(empty)
                        .warningThreshold(warning)
                        .build();
                inventoryRepository.save(item);
                log.info("-> Đã nạp tồn kho mẫu cho loại: {}", type);
            }
        } catch (Exception ex) {
            log.warn("-> Tồn kho cho loại '{}' đã tồn tại trong CSDL, bỏ qua.", type);
        }
    }

    private void seedSalaryConfigs() {
        if (salaryConfigRepository.count() > 0) {
            log.info("-> Bảng salary_configs đã có dữ liệu trong CSDL, bỏ qua seed.");
            return;
        }

        seedSalaryConfigItem("Bình 12kg Xám", 15000.0, 100, 500000.0);
        seedSalaryConfigItem("Bình 12kg Đỏ", 15000.0, 100, 500000.0);
        seedSalaryConfigItem("Bình 45kg Công Nghiệp", 50000.0, 30, 1000000.0);
        seedSalaryConfigItem("Gas Lon Mini", 2000.0, null, null);
    }

    private void seedSalaryConfigItem(String type, double commission, Integer threshold, Double bonus) {
        try {
            Optional<Inventory> optInv = inventoryRepository.findByContainerType(type);
            if (optInv.isPresent()) {
                Inventory inv = optInv.get();
                if (!salaryConfigRepository.existsByInventoryId(inv.getId())) {
                    SalaryConfig config = SalaryConfig.builder()
                            .inventory(inv)
                            .commissionPerUnit(commission)
                            .milestoneBonusThreshold(threshold)
                            .milestoneBonusAmount(bonus)
                            .build();
                    salaryConfigRepository.save(config);
                    log.info("-> Đã nạp cấu hình hoa hồng liên kết khóa ngoại với kho cho: {}", type);
                }
            }
        } catch (Exception ex) {
            log.warn("-> Cấu hình hoa hồng cho '{}' đã tồn tại, bỏ qua.", type);
        }
    }

    private void seedSalaryFormula() {
        try {
            if (salaryFormulaRepository.findByIsActiveTrue().isEmpty()) {
                SalaryFormula formula = SalaryFormula.builder()
                        .name("Công thức lương chuẩn PolyGas 2026")
                        .formulaExpression("(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus")
                        .isActive(true)
                        .effectiveDate(LocalDate.of(2026, 1, 1))
                        .description("Công thức chuẩn: (Lương cơ bản / 26 * Ngày công) + (Hoa hồng * Hệ số tăng ca) + Thưởng/Phụ cấp")
                        .build();
                salaryFormulaRepository.save(formula);
                log.info("-> Đã nạp công thức tính lương SpEL chuẩn vào CSDL");
            }
        } catch (Exception ex) {
            log.warn("-> Công thức tính lương đã tồn tại, bỏ qua.");
        }
    }

    private void seedCustomers() {
        if (customerRepository.count() > 0) {
            return;
        }

        seedCustomerItem("Đại Lý Gas Hoàng Gia", "0912345678", "128 Đường Lê Lợi, Quận 1, TP.HCM", 10.776889, 106.700806, "Đại lý bán buôn, thanh toán chuyển khoản");
        seedCustomerItem("Nhà Hàng Biển Đông", "0908765432", "54 Nguyễn Thị Minh Khai, Quận 3, TP.HCM", 10.781234, 106.694567, "Giao trước 10h sáng hàng ngày, nợ vỏ bình 45kg");
        seedCustomerItem("Quán Cơm Niêu Sài Gòn", "0934567890", "27 Tú Xương, Võ Thị Sáu, Quận 3, TP.HCM", 10.783456, 106.689012, "Giao bình 12kg xám vào cửa sau khu bếp");
        seedCustomerItem("Hộ Gia Đình Chị Mai", "0977889900", "15/4 Cách Mạng Tháng 8, Phường 10, Quận 10, TP.HCM", 10.772345, 106.678901, "Khách hộ gia đình, trả tiền mặt");
    }

    private void seedCustomerItem(String name, String phone, String address, Double lat, Double lng, String notes) {
        try {
            String nameHash = NameHashUtil.generateNameHash(name);
            if (!customerRepository.existsByNameHash(nameHash)) {
                Customer c = Customer.builder()
                        .name(name)
                        .nameHash(nameHash)
                        .phone(phone)
                        .address(address)
                        .latitude(lat)
                        .longitude(lng)
                        .notes(notes)
                        .build();
                customerRepository.save(c);
                log.info("-> Đã nạp khách hàng thật: {}", name);
            }
        } catch (Exception e) {
            log.warn("-> Lỗi nạp khách hàng '{}': {}", name, e.getMessage());
        }
    }

    private void seedDeliveryReports() {
        if (deliveryReportRepository.count() > 0) {
            return;
        }

        User shipper = userRepository.findByUsername("shipper01").orElse(null);
        if (shipper == null) return;

        Inventory inv12Grey = inventoryRepository.findByContainerType("Bình 12kg Xám").orElse(null);
        Inventory inv45 = inventoryRepository.findByContainerType("Bình 45kg Công Nghiệp").orElse(null);
        Inventory inv12Red = inventoryRepository.findByContainerType("Bình 12kg Đỏ").orElse(null);

        Customer cComNieu = customerRepository.findByNameContainingIgnoreCase("Cơm Niêu").stream().findFirst().orElse(null);
        Customer cBienDong = customerRepository.findByNameContainingIgnoreCase("Biển Đông").stream().findFirst().orElse(null);
        Customer cMai = customerRepository.findByNameContainingIgnoreCase("Chị Mai").stream().findFirst().orElse(null);

        if (inv12Grey != null && cComNieu != null) {
            deliveryReportRepository.save(DeliveryReport.builder()
                    .employee(shipper)
                    .customer(cComNieu)
                    .customerName(cComNieu.getName())
                    .inventory(inv12Grey)
                    .quantity(2)
                    .unitPrice(350000.0)
                    .total(700000.0)
                    .actualReceived(700000.0)
                    .paymentStatus(PaymentStatus.PAID)
                    .reportDate(LocalDate.now())
                    .notes("Giao tại bếp tầng trệt, khách trả tiền mặt đủ")
                    .build());
        }

        if (inv45 != null && cBienDong != null) {
            deliveryReportRepository.save(DeliveryReport.builder()
                    .employee(shipper)
                    .customer(cBienDong)
                    .customerName(cBienDong.getName())
                    .inventory(inv45)
                    .quantity(1)
                    .unitPrice(1250000.0)
                    .total(1250000.0)
                    .actualReceived(0.0)
                    .paymentStatus(PaymentStatus.DEBT)
                    .reportDate(LocalDate.now())
                    .notes("Khách hẹn chiều 17h ghé lấy tiền mặt, nợ 1 vỏ 45kg chưa trả")
                    .build());
        }

        if (inv12Red != null && cMai != null) {
            deliveryReportRepository.save(DeliveryReport.builder()
                    .employee(shipper)
                    .customer(cMai)
                    .customerName(cMai.getName())
                    .inventory(inv12Red)
                    .quantity(1)
                    .unitPrice(360000.0)
                    .total(360000.0)
                    .actualReceived(360000.0)
                    .paymentStatus(PaymentStatus.PAID)
                    .reportDate(LocalDate.now())
                    .notes("Khách thanh toán qua mã QR")
                    .build());
        }
        log.info("-> Đã nạp các phiếu báo cáo giao gas thật trong ngày vào CSDL");
    }

    private void seedExpenses() {
        if (expenseRepository.count() > 0) {
            return;
        }

        User shipper = userRepository.findByUsername("shipper01").orElse(null);
        if (shipper == null) return;

        expenseRepository.save(Expense.builder()
                .employee(shipper)
                .expenseDate(LocalDate.now())
                .description("Đổ xăng xe máy giao gas ca sáng")
                .amount(65000.0)
                .notes("Cây xăng Petrolimex số 3")
                .build());

        expenseRepository.save(Expense.builder()
                .employee(shipper)
                .expenseDate(LocalDate.now())
                .description("Vá săm xe máy đường Lê Lợi")
                .amount(30000.0)
                .notes("Cán đinh khi đang chở bình 45kg")
                .build());

        log.info("-> Đã nạp chi phí vận hành ca trực thật vào CSDL");
    }
}
