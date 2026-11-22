package poly.duantn.exception;

<<<<<<< HEAD
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Lớp Exception gốc của toàn hệ thống OriginGas Enterprise
 */
@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;

    public AppException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
=======
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
>>>>>>> 88a6459825e200b053e54832e6c889ccf80126a6
    }
}
