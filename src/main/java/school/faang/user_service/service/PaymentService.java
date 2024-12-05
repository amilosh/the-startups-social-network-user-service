package school.faang.user_service.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.client.payment.PaymentRequest;
import school.faang.user_service.client.payment.PaymentResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    @Retryable(
            value = {RuntimeException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public PaymentResponse sentPayment(PaymentRequest paymentRequest) {
        try {
            return paymentServiceClient.sentPayment(paymentRequest);
        } catch (FeignException e) {
            log.error("Failed operation: ", e);
            throw new RuntimeException("Failed operation: " + e);
        }
    }
}
