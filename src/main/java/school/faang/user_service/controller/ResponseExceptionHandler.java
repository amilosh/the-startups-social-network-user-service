package school.faang.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.dto.ExceptionBaseStructure;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorCode;
import school.faang.user_service.util.ServiceMethods;
import school.faang.user_service.util.ServiceParameters;

@RestControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ExceptionBaseStructure> handleElementNotFindException(Exception exception) {
        return new ResponseEntity<>(new ExceptionBaseStructure(exception.getMessage(),
                ErrorCode.INVALID_FOLLOW,
                ServiceMethods.getTimeIsoOffsetDateTime(),
                ServiceParameters.SYSTEM_ID
        ), HttpStatus.NOT_FOUND);
    }
}