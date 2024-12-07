package school.faang.user_service.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.client.payment.PaymentRequest;
import school.faang.user_service.client.payment.PaymentResponse;

@FeignClient(url = "${payment-service.host}:${payment-service.port}", name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sentPayment(@Valid @RequestBody PaymentRequest paymentRequest);
}
