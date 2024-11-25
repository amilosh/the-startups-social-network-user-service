package school.faang.user_service.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataNotMatchException;

import java.util.Map;

@RestControllerAdvice
public class DataNotMatchExceptionHandler {

    @ExceptionHandler(DataNotMatchException.class)
    public ResponseEntity<Map<String, ?>> illegalArgumentExceptionHandler(school.faang.user_service.exception.DataNotMatchException e) {
        return ResponseEntity.status(400).body(Map.of(
                "message", e.getMessage(),
                "object", e.getObject()
        ));
    }
}
