package school.faang.user_service.client.payment;

public interface PaymentServiceClient {
    boolean sendPaymentRequest(Long userId, int days);
}
