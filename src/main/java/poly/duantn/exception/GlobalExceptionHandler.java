package poly.duantn.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import poly.duantn.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp bắt và chuẩn hóa toàn bộ lỗi ngoại lệ trong ứng dụng (Global Exception Handler)
 * Tự động trích xuất lỗi Bean Validation (@Valid) và ném về ApiResponse chuẩn cho Frontend.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Bắt toàn bộ lỗi vi phạm Bean Validation (@Valid trên DTOs)
     * Trả về HTTP 400 Bad Request kèm chi tiết từng trường bị lỗi
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Lấy thông báo lỗi đầu tiên để hiển thị nhanh trên Alert / Toast của Frontend
        String firstMessage = errors.values().stream().findFirst().orElse("Dữ liệu gửi lên không hợp lệ");

        log.warn(">>> [Bean Validation Error] {}: {}", firstMessage, errors);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message(firstMessage)
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Bắt lỗi nghiệp vụ BadRequestException (ví dụ: Thiếu bình trong kho, dữ liệu không hợp lệ)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex) {
        log.warn(">>> [Bad Request Exception]: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bắt lỗi không tìm thấy tài nguyên (ResourceNotFoundException - 404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn(">>> [Resource Not Found]: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bắt lỗi AppException nội bộ hệ thống
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        log.error(">>> [App Exception]: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bắt các lỗi không xác định còn lại (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        log.error(">>> [Uncaught System Error]: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống máy chủ: " + ex.getMessage()));
    }
}
