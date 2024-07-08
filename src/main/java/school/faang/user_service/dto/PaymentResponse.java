package school.faang.user_service.dto;

import java.math.BigDecimal;

public record PaymentResponse(
    PaymentStatus status,
    int verificationCode,
    long paymentNumber,
    BigDecimal amount,
    Currency currency,
    String message
) {
}
