package school.faang.user_service.exception;

import school.faang.user_service.exception.responses.ResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseError runtimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseError iOExceptionException(IOException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError("Error processing the file. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseError methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);

        AtomicReference<String> message = new AtomicReference<>();
        exception.getBindingResult().getAllErrors().forEach((error) ->
                message.set(error.getDefaultMessage()));

        return new ResponseError(message.get(), HttpStatus.BAD_REQUEST);
    }
}
