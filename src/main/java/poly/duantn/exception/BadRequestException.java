package poly.duantn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ ném ra khi dữ liệu yêu cầu không hợp lệ (HTTP 400 Bad Request)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}