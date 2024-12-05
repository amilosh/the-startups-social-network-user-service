package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;

@FeignClient(name = "payment-service", url = "${services.payment-service.host}:${services.payment-service.port}", configuration = FeignConfig.class)
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest paymentRequest);

}
