package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.ExceptionDto;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleDataValidationException(DataValidationException e) {
        log.error("Data Validation Exception", e);
        return bulidExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handlerIllegalStateException(IllegalStateException e) {
        log.error("Illegal State Exception", e);
        return bulidExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handlerIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal Argument Exception", e);
        return bulidExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handlerEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity Not Found Exception", e);
        return bulidExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Method Argument Not Valid Exception", e);

        List<String> errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return bulidExceptionDto(HttpStatus.BAD_REQUEST, "Failed validation", errors);
    }

    @ExceptionHandler(ServiceConnectionFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handlerPaymentFailedException(ServiceConnectionFailedException e) {
        log.error("Service Connection Failed Exception", e);
        return bulidExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ExceptionDto bulidExceptionDto(HttpStatus status, String description, List<String> errors) {
        return ExceptionDto.builder()
                .statusCode(status.value())
                .status(status)
                .description(description)
                .errorMessages(errors)
                .build();
    }

    private ExceptionDto bulidExceptionDto(HttpStatus status, String description) {
        return ExceptionDto.builder()
                .statusCode(status.value())
                .status(status)
                .description(description)
                .build();
    }
}
