package school.faang.user_service.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.EntityNotFoundExceptionWithID;

import java.util.Map;

@RestControllerAdvice
public class EntityNotFoundExceptionHandler {

    @ExceptionHandler(EntityNotFoundExceptionWithID.class)
    public ResponseEntity<Map<String, ?>> handleEntityNotFoundExceptionHandler(EntityNotFoundExceptionWithID e) {
        return ResponseEntity.status(404).body(Map.of(
                "message:", e.getMessage(),
                "id", e.getEntityId()
        ));
    }
}
