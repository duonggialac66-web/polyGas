package poly.duantn.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import poly.duantn.entity.Inventory;
import poly.duantn.entity.SalaryConfig;
import poly.duantn.entity.SalaryFormula;
import poly.duantn.entity.User;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.repository.*;

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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedInventories();
        seedSalaryConfigs();
        seedSalaryFormula();
        log.info(">>> [OriginGas Enterprise] Đã nạp thành công toàn bộ dữ liệu nền tảng với Khóa Ngoại chuẩn vào CSDL!");
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .name("Quản trị viên OriginGas")
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
    }

    private void seedInventories() {
        seedInventoryItem("Bình 12kg Xám", 100, 30, 15);
        seedInventoryItem("Bình 12kg Đỏ", 80, 25, 15);
        seedInventoryItem("Bình 45kg Công Nghiệp", 30, 10, 5);
        seedInventoryItem("Gas Lon Mini", 200, 50, 30);
    }

    private void seedInventoryItem(String type, int full, int empty, int warning) {
        if (!inventoryRepository.existsByContainerType(type)) {
            Inventory item = Inventory.builder()
                    .containerType(type)
                    .fullQuantity(full)
                    .emptyQuantity(empty)
                    .warningThreshold(warning)
                    .build();
            inventoryRepository.save(item);
            log.info("-> Đã nạp tồn kho mẫu cho loại: {}", type);
        }
    }

    private void seedSalaryConfigs() {
        seedSalaryConfigItem("Bình 12kg Xám", 15000.0, 100, 500000.0);
        seedSalaryConfigItem("Bình 12kg Đỏ", 15000.0, 100, 500000.0);
        seedSalaryConfigItem("Bình 45kg Công Nghiệp", 50000.0, 30, 1000000.0);
        seedSalaryConfigItem("Gas Lon Mini", 2000.0, null, null);
    }

    private void seedSalaryConfigItem(String type, double commission, Integer threshold, Double bonus) {
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
    }

    private void seedSalaryFormula() {
        if (salaryFormulaRepository.findByIsActiveTrue().isEmpty()) {
            SalaryFormula formula = SalaryFormula.builder()
                    .name("Công thức lương chuẩn OriginGas 2026")
                    .formulaExpression("(#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus")
                    .isActive(true)
                    .effectiveDate(LocalDate.of(2026, 1, 1))
                    .description("Công thức chuẩn: (Lương cơ bản / 26 * Ngày công) + (Hoa hồng * Hệ số tăng ca) + Thưởng/Phụ cấp")
                    .build();
            salaryFormulaRepository.save(formula);
            log.info("-> Đã nạp công thức tính lương SpEL chuẩn vào CSDL");
        }
    }
}
