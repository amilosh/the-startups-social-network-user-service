package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.exception.skill.SkillAcquireDtoNullObjectValidationException;
import school.faang.user_service.exception.skill.SkillDtoNullObjectValidationException;
import school.faang.user_service.exception.skill.SkillDtoFieldConstraintValidationException;
import school.faang.user_service.exception.skill.SkillDuplicateException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SkillDtoNullObjectValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleSkillDtoNullObjectValidationException(SkillDtoNullObjectValidationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }

    @ExceptionHandler(SkillDtoFieldConstraintValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleSkillDtoFieldConstraintValidationException(SkillDtoFieldConstraintValidationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }

    @ExceptionHandler(SkillAcquireDtoNullObjectValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleSkillAcquireDtoNullObjectValidationException(SkillAcquireDtoNullObjectValidationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }

    @ExceptionHandler(SkillDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleSkillDuplicateException(SkillDuplicateException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleAllOtherExceptions(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Ошибка сервера: " + ex.getMessage());
    }
}