package school.faang.user_service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Произошло исключение IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error("Ошибка доступа к базе данных: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка базы данных.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Произошла ошибка: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка.");
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<String> handleInvalidUserIdException(InvalidUserIdException ex) {
        log.warn("Произошло исключение InvalidUserIdException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SubscriptionAlreadyExistsException.class)
    public ResponseEntity<String> handleSubscriptionAlreadyExistsException(SubscriptionAlreadyExistsException ex) {
        log.warn("Произошло исключение SubscriptionAlreadyExistsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<String> handleSubscriptionNotFoundException(SubscriptionNotFoundException ex) {
        log.warn("Произошло исключение SubscriptionNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
