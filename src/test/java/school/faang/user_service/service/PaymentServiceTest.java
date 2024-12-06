package school.faang.user_service.service;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.client.payment.Currency;
import school.faang.user_service.client.payment.PaymentRequest;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentStatus;
import school.faang.user_service.entity.PremiumPeriod;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void sentPaymentSuccess() {
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;
        PaymentRequest expectedRequest = PaymentRequest.builder()
                .paymentNumber(anyLong())
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .build();
        PaymentResponse expectedResponse = new PaymentResponse(PaymentStatus.SUCCESS, 12345,
                54321, BigDecimal.TEN, Currency.USD, "SUCCESS");

        lenient().when(paymentServiceClient.sentPayment(expectedRequest))
                .thenReturn(expectedResponse);

        paymentService.sentPayment(premiumPeriod);

        verify(paymentServiceClient, times(1)).sentPayment(any(PaymentRequest.class));
    }

    @Test
    void sentPaymentFailsWithFeignException() {
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;
        FeignException feignException = mock(FeignException.class);
        when(paymentServiceClient.sentPayment(any(PaymentRequest.class)))
                .thenThrow(feignException);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> paymentService.sentPayment(premiumPeriod));

        assertTrue(thrown.getMessage().contains("Failed operation"));
        verify(paymentServiceClient, times(1)).sentPayment(any(PaymentRequest.class));
    }
}