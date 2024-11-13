package school.faang.user_service.client.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@Component
public class PaymentServiceClientImpl implements PaymentServiceClient {
    private final RestTemplate restTemplate;

    @Override
    public boolean sendPaymentRequest(Long userId, int days) {
        String url = "http://localhost:9080/payment";
        PaymentRequest paymentRequest = new PaymentRequest(userId, days);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, paymentRequest, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}