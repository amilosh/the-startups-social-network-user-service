package school.faang.user_service.controller.exceptionhandler;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.exception.participation.EventNotFoundException;
import school.faang.user_service.exception.participation.ParticipationException;
import school.faang.user_service.exception.participation.UserNotFoundException;
import school.faang.user_service.exception.premium.ExistingPurchaseException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.exception.promotion.PromotionValidationException;
import school.faang.user_service.exception.recommendation.RequestStatusException;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError ->
                                fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Validation error"
                ));

        log.warn("Validation failed: {}", errors);
        return buildProblemDetailResponse(HttpStatus.BAD_REQUEST, "Validation failed", Map.of("errors", errors));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            DataValidationException.class,
            RequestStatusException.class,
            PromotionValidationException.class,
            ParticipationException.class
    })
    public ResponseEntity<ProblemDetail> handleBadRequestExceptions(RuntimeException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildProblemDetailResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            UserNotFoundException.class,
            InvitationEntityNotFoundException.class,
            EventNotFoundException.class,
            PremiumNotFoundException.class,
            PromotionNotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleNotFoundExceptions(RuntimeException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildProblemDetailResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            DuplicateRequestException.class,
            ExistingPurchaseException.class
    })
    public ResponseEntity<ProblemDetail> handleConflictExceptions(RuntimeException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildProblemDetailResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({
            SQLException.class,
            UncategorizedSQLException.class
    })
    public ResponseEntity<ProblemDetail> handleDatabaseExceptions(Exception ex) {
        log.error("Database error occurred: {}", ex.getMessage(), ex);
        return buildProblemDetailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "A database error occurred.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildProblemDetailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(HttpStatus status, String detail, Map<String, Object> properties) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        properties.forEach(problemDetail::setProperty);
        return ResponseEntity.status(status).body(problemDetail);
    }
}
