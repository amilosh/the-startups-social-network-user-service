package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.PaymentFailedException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class PaymentFailedExceptionHandler {

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<Map<String, Object>> paymentFailedExceptionHandler(PaymentFailedException e) {
        Map<String, Object> errorResponse = new HashMap<>();

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("code", "PAYMENT_FAILED");
        errorDetails.put("message", "Payment was unsuccessful");
        errorDetails.put("details", e.getMessage());

        errorResponse.put("error", errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYMENT_REQUIRED);
    }
}
