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
import school.faang.user_service.exception.partiсipation.EventNotFoundException;
import school.faang.user_service.exception.partiсipation.ParticipationException;
import school.faang.user_service.exception.partiсipation.UserNotFoundException;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.premium.ExistingPurchaseException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.exception.promotion.PromotionValidationException;
import school.faang.user_service.exception.recommendation.RequestStatusException;

import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
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

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setProperty("errors", errors);

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ProblemDetail> handleDataValidationException(DataValidationException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(InvitationEntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleInvitationEntityNotFoundException(InvitationEntityNotFoundException exception) {
        log.error("invitation to a goal not found: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception exception) {
        log.error("Unexpected error occurred: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException exception) {
        log.error("NullPointerException: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Null values are not allowed in the request."));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException exception) {
        log.warn("User not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEventNotFoundException(EventNotFoundException exception) {
        log.warn("Event not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(ParticipationException.class)
    public ResponseEntity<ProblemDetail> handleParticipationException(ParticipationException exception) {
        log.warn("Participation error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(ExistingPurchaseException.class)
    public ResponseEntity<ProblemDetail> handleExistingPurchaseException(ExistingPurchaseException exception) {
        log.warn("Existing premium purchase error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(UnSuccessPaymentException.class)
    public ResponseEntity<ProblemDetail> handleUnSuccessPaymentException(UnSuccessPaymentException exception) {
        log.error("Unsuccessful payment error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(PremiumNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePremiumNotFoundException(PremiumNotFoundException exception) {
        log.warn("Premium not found: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler({SQLException.class, UncategorizedSQLException.class})
    public ResponseEntity<ProblemDetail> handleSqlExceptions(Exception exception) {
        log.error("SQL Exception occurred: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "A database error occurred."));
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateRequestException(DuplicateRequestException exception) {
        log.warn("Duplicate request: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler(PromotionNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePromotionNotFoundException(PromotionNotFoundException exception) {
        log.warn("Promotion not found: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(PromotionValidationException.class)
    public ResponseEntity<ProblemDetail> handlePromotionValidationException(PromotionValidationException exception) {
        log.warn("Promotion validation error: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(RequestStatusException.class)
    public ResponseEntity<ProblemDetail> handleRequestStatusException(RequestStatusException exception) {
        log.warn("Invalid request status: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }
}
