package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.client.payment.Currency;
import school.faang.user_service.client.payment.PaymentRequest;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private UserService userService;

    @Mock
    private PaymentValidator paymentValidator;

    @Spy
    private PremiumMapper premiumMapper;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    void testBuyPremiumSuccessful() {
        long userId = 1L;
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;

        when(userService.findUserById(userId)).thenReturn(new User());
        when(paymentServiceClient.sentPayment(any(PaymentRequest.class)))
                .thenReturn(new PaymentResponse(PaymentStatus.SUCCESS, 1234, 123456789L,
                        BigDecimal.valueOf(9.99), Currency.USD, "Payment Successful"));
        when(premiumMapper.toDto(any(Premium.class))).thenReturn(new PremiumDto());

        Premium premium = Premium.builder()
                .user(new User())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);

        PremiumDto result = premiumService.buyPremium(userId, premiumPeriod);

        assertNotNull(result);
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verify(paymentServiceClient).sentPayment(any(PaymentRequest.class));
        verify(paymentValidator).checkIfPaymentSuccess(any(PaymentResponse.class));
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toDto(any(Premium.class));
    }

    @Test
    void testBuyPremiumUserAlreadyPremium() {
        long userId = 1L;
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;

        doThrow(new IllegalArgumentException("User with userId: " + userId + " already has premium"))
                .when(premiumValidator).validateUserIsNotPremium(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                premiumService.buyPremium(userId, premiumPeriod));

        assertEquals("User with userId: " + userId + " already has premium", exception.getMessage());
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verifyNoInteractions(paymentServiceClient, paymentValidator, premiumRepository, premiumMapper);
    }

    @Test
    void testBuyPremiumPaymentFailed() {
        long userId = 1L;
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;

        PaymentResponse failedResponse = new PaymentResponse(PaymentStatus.FAILED,
                1234, 123456789L, BigDecimal.valueOf(9.99),
                Currency.USD, "Payment Failed");

        when(paymentServiceClient.sentPayment(any())).thenReturn(failedResponse);

        doThrow(new PaymentFailedException("Payment status:" + failedResponse.message()))
                .when(paymentValidator).checkIfPaymentSuccess(failedResponse);

        Exception exception = assertThrows(PaymentFailedException.class, () ->
                premiumService.buyPremium(userId, premiumPeriod));

        assertEquals("Payment status:" + failedResponse.message(), exception.getMessage());
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verify(paymentServiceClient).sentPayment(any(PaymentRequest.class));
        verify(paymentValidator).checkIfPaymentSuccess(failedResponse);
        verifyNoInteractions(premiumRepository, premiumMapper);
    }
}
