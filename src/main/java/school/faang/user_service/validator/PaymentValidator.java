package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.exception.PaymentFailedException;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final PaymentServiceClient paymentServiceClient;

    public void checkIfPaymentSuccess() {
        if (paymentServiceClient.sentPayment().status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException("Payment status:" + paymentServiceClient.sentPayment().status());
        }
    }
}
