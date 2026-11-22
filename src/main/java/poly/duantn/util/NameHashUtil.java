package poly.duantn.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Tiện ích băm và chuẩn hóa tiếng Việt cho khách hàng
 */
public class NameHashUtil {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Chuẩn hóa tên (bỏ dấu tiếng Việt, chữ thường, chuyển khoảng trắng thành gạch nối)
     * Ví dụ: "Quán Ăn Cô Ba" -> "quan-an-co-ba"
     */
    public static String generateNameHash(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(name.trim(), Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICS.matcher(normalized).replaceAll("");
        return withoutDiacritics
                .toLowerCase()
                .replace("đ", "d")
                .replace("Đ", "d")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    /**
     * Chuẩn hóa từ khóa tìm kiếm không dấu
     */
    public static String normalize(String keyword) {
        return generateNameHash(keyword);
    }
}
