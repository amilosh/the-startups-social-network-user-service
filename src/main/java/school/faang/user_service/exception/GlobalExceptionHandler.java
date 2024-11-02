package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.response.GoalResponse;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<GoalResponse> handleInvalidException(InvalidException ex) {
        GoalResponse response = new GoalResponse("Validation failed", 400);
        response.setErrors(List.of(ex.getMessage()));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GoalResponse> handleBadRequestException(BadRequestException ex) {
        GoalResponse response = new GoalResponse("Bad request", 404);
        response.setErrors(List.of(ex.getMessage()));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GoalResponse> handleGenericException(Exception ex) {
        GoalResponse response = new GoalResponse("An error occurred", 500);
        response.setErrors(List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
