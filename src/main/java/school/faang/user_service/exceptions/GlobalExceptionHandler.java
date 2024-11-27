package school.faang.user_service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String RESOURCE_NOT_FOUND = "ResourceNotFoundException occurred: {}";
    public static final String DATA_VALIDATION_ERROR = "DataValidationException occurred: {}";
    public static final String ILLEGAL_ARGUMENT = "IllegalArgumentException occurred: {}";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred: {}";
    public static final String PAYMENT_ERROR = "PaymentException occurred: {}";

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error(RESOURCE_NOT_FOUND, ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.error(DATA_VALIDATION_ERROR, ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ILLEGAL_ARGUMENT, ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ErrorResponse handlePaymentException(PaymentException ex) {
        log.error(PAYMENT_ERROR, ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error(UNEXPECTED_ERROR, ex.getMessage(), ex);
        return new ErrorResponse(ex.getLocalizedMessage());
    }
}

