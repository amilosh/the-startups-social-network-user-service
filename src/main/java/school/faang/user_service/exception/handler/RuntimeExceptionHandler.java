package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RuntimeExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handRuntimeException(RuntimeException e) {
        var error = new HashMap<String, String>();

        log.info("handled runtime exception: {}", e.getMessage(), e);
        error.put("status", "500");
        error.put("error", "Internal Server Error");
        error.put("message", e.getMessage());
        return error;
    }
}
