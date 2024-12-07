package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentStatus;
import school.faang.user_service.exception.PaymentFailedException;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final PaymentServiceClient paymentServiceClient;

    public void checkIfPaymentSuccess(PaymentResponse response) {
        if (response.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException("Payment status:" + response.status());
        }
    }
}
