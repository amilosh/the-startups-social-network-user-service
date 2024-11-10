package school.faang.user_service.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.dto.ExceptionBaseStructure;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorCode;
import school.faang.user_service.utilities.ServiceMethods;
import school.faang.user_service.utilities.UrlServiceParameters;

@RestControllerAdvice
public class ExceptionApiHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ExceptionBaseStructure> handleElementNotFindException(Exception exception) {
        return new ResponseEntity<>(new ExceptionBaseStructure(exception.getMessage(),
                ErrorCode.INVALID_FOLLOW,
                ServiceMethods.getTimeIsoOffsetDateTime(),
                UrlServiceParameters.SYSTEM_ID
        ), HttpStatus.BAD_REQUEST);
    }
}