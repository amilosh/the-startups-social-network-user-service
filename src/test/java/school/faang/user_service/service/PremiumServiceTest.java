package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserContext userContext;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    ResponseEntity<PaymentResponse> response;

    @Test
    public void testUserAlreadyHasPremium() {
        setupMocks(true);

        assertThrows(IllegalStateException.class,
                () -> premiumService.buyPremium(PremiumPeriod.YEAR));

    }

    @Test
    public void testResponseBodyIsNull() {
        setupMocks(false);

        when(paymentServiceClient.sendPayment(any())).thenReturn(response);
        when(response.getBody()).thenReturn(null);

        assertThrows(RestClientException.class,
                () -> premiumService.buyPremium(PremiumPeriod.YEAR));
    }

    @Test
    public void testBuyPremiumSuccessful() {
        PaymentResponse paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                0,
                0,
                BigDecimal.valueOf(1),
                Currency.USD,
                "message"
        );

        setupMocks(false);

        when(paymentServiceClient.sendPayment(any())).thenReturn(response);
        when(response.getBody()).thenReturn(paymentResponse);
        when(userService.findById(1L)).thenReturn(Optional.of(new User()));

        premiumService.buyPremium(PremiumPeriod.YEAR);

        verify(premiumRepository).save(any());

    }

    private void setupMocks(boolean existsByUserId) {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(premiumRepository.existsByUserId(userId)).thenReturn(existsByUserId);
    }
}
