package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.payment.Currency;
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
import school.faang.user_service.validator.UserValidator;

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
    private PaymentService paymentService;

    @Mock
    UserValidator userValidator;

    @InjectMocks
    private PremiumService premiumService;

    private final long userId = 1L;

    @Test
    void testBuyPremiumSuccessful() {
        when(userService.findUserById(userId)).thenReturn(new User());
        when(paymentService.sentPayment(any(PremiumPeriod.class)))
                .thenReturn(new PaymentResponse(PaymentStatus.SUCCESS, 1234, 123456789L,
                        BigDecimal.valueOf(9.99), Currency.USD, "Payment Successful"));
        when(premiumMapper.toDto(any(Premium.class))).thenReturn(new PremiumDto());
        when(premiumRepository.save(any(Premium.class))).thenReturn(setUpPremium());

        PremiumDto result = premiumService.buyPremium(userId, setUpPeriodMonth());

        assertNotNull(result);
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verify(paymentService).sentPayment(any(PremiumPeriod.class));
        verify(paymentValidator).checkIfPaymentSuccess(any(PaymentResponse.class));
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toDto(any(Premium.class));
    }

    @Test
    void testBuyPremiumUserAlreadyPremium() {
        doThrow(new IllegalArgumentException("User with userId: " + userId + " already has premium"))
                .when(premiumValidator).validateUserIsNotPremium(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                premiumService.buyPremium(userId, setUpPeriodMonth()));

        assertEquals("User with userId: " + userId + " already has premium", exception.getMessage());
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verifyNoInteractions(paymentService, paymentValidator, premiumRepository, premiumMapper);
    }

    @Test
    void testBuyPremiumPaymentFailed() {
        PaymentResponse failedResponse = new PaymentResponse(PaymentStatus.FAILED,
                1234, 123456789L, BigDecimal.valueOf(9.99),
                Currency.USD, "Payment Failed");

        when(paymentService.sentPayment(any())).thenReturn(failedResponse);

        doThrow(new PaymentFailedException("Payment status:" + failedResponse.message()))
                .when(paymentValidator).checkIfPaymentSuccess(failedResponse);

        Exception exception = assertThrows(PaymentFailedException.class, () ->
                premiumService.buyPremium(userId, setUpPeriodMonth()));

        assertEquals("Payment status:" + failedResponse.message(), exception.getMessage());
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verify(paymentService).sentPayment(any(PremiumPeriod.class));
        verify(paymentValidator).checkIfPaymentSuccess(failedResponse);
        verifyNoInteractions(premiumRepository, premiumMapper);
    }

    private PremiumPeriod setUpPeriodMonth() {
        return PremiumPeriod.MONTH;
    }

    private Premium setUpPremium() {
        return Premium.builder()
                .user(new User())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
    }
}
