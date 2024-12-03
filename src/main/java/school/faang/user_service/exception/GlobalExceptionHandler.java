package school.faang.user_service.exception;

import io.minio.errors.MinioException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<String> handleSkillNotFoundException(SkillNotFoundException exception) {
        log.error("SkillNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {
        log.error("UserNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("IllegalArgumentException: {}", exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
        log.error("IllegalStateException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        log.error("DataValidationException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RecommendationRequestNotFoundException.class)
    public ResponseEntity<String> handleRecommendationRequestNotFoundException(
            RecommendationRequestNotFoundException exception) {
        log.error("RecommendationRequestNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidMentorshipRequestException.class)
    public ResponseEntity<String> handleInvalidMentorshipRequestException(InvalidMentorshipRequestException ex) {
        log.error("InvalidMentorshipRequestException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestFilterException.class)
    public ResponseEntity<String> handleInvalidRequestFilterException(InvalidRequestFilterException ex) {
        log.error("InvalidRequestFilterException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SkillDuplicateException.class)
    public ResponseEntity<String> handleSkillDuplicateException(SkillDuplicateException ex) {
        log.error("SkillDuplicateException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception exception) {
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("ConstraintViolationException: {}", errorMessage, ex);
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<String> handleFileSizeExceededException(FileSizeExceededException exception) {
        log.error("FileSizeExceededException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException exception) {
        log.error("IOException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while file processing");
    }

    @ExceptionHandler({
            MinioException.class,
            NoSuchAlgorithmException.class,
            InvalidKeyException.class
    })

    public ResponseEntity<String> handleMinioExceptions(Exception exception) {
        log.error("Minio-related exception: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the file to Minio. Please try again later.");
    }

    @ExceptionHandler(MinioUploadException.class)
    public ResponseEntity<String> handleMinioUploadException(MinioUploadException exception) {
        log.error("MinioUploadException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file to Minio. Please try again later.");
    }

    @ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<String> handleAvatarNotFoundException(AvatarNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<String> handleInvalidFileFormatException(InvalidFileFormatException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(AvatarProcessingException.class)
    public ResponseEntity<String> handleAvatarProcessingException(AvatarProcessingException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handeAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
