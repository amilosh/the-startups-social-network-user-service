package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.premium.PaymentResponse;

@FeignClient(url = "http://${payment-service.host}:${payment-service.port}", name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sentPayment();
}
