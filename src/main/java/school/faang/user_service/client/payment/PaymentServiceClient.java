package school.faang.user_service.client.payment;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentServiceClient {
    @PostMapping("/payment")
    @Retryable(
            retryFor = FeignException.class,
            maxAttemptsExpression = "${app.retryable.payment_service.send_payment.max_attempts}",
            backoff = @Backoff(
                    delayExpression = "${app.retryable.payment_service.send_payment.delay}",
                    multiplierExpression = "${app.retryable.payment_service.send_payment.multiplier}"
            )
    )
    PaymentResponseDto sendPayment(@Valid @RequestBody PaymentRequestDto requestDto);
}