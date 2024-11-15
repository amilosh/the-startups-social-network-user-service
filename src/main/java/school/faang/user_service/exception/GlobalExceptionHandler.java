package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle {@link DataValidationException} by returning a {@link ResponseEntity} with a 400 status code
     * and the error message.
     *
     * @param ex the {@link DataValidationException} thrown
     * @return a {@link ResponseEntity} with the error message
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handle {@link EntityNotFoundException} by returning a {@link ResponseEntity} with a 404 status code
     * and the error message.
     *
     * @param ex the {@link EntityNotFoundException} thrown
     * @return a {@link ResponseEntity} with the error message
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidMentorshipRequestException.class)
    public ResponseEntity<String> handleInvalidMentorshipRequestException(InvalidMentorshipRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestFilterException.class)
    public ResponseEntity<String> handleInvalidRequestFilterException(InvalidRequestFilterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SkillDuplicateException.class)
    public ResponseEntity<String> handleSkillDuplicateException(SkillDuplicateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}