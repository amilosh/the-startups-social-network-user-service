package school.faang.user_service.handler;

import jakarta.validation.ValidationException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.ExceptionBaseStructure;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorCode;
import school.faang.user_service.utilities.ServiceMethods;
import school.faang.user_service.utilities.UrlServiceParameters;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ExceptionBaseStructure> handleElementNotFindException(Exception exception) {
        return new ResponseEntity<>(new ExceptionBaseStructure(exception.getMessage(),
                ErrorCode.INVALID_FOLLOW,
                ServiceMethods.getTimeIsoOffsetDateTime(),
                UrlServiceParameters.SYSTEM_ID
        ), HttpStatus.BAD_REQUEST);
    }
}