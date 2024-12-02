package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    @InjectMocks
    private PremiumService premiumService;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserContext userContext;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    ResponseEntity<PaymentResponse> response;

    private PremiumDto firstPremium;

    private Premium secondPremium;

    @BeforeEach
    void setUp() {
        firstPremium = new PremiumDto();
        firstPremium.setActive(false);
        firstPremium.setId(2L);

        secondPremium = new Premium();
        secondPremium.setId(2);
        secondPremium.setActive(true);
        secondPremium.setStartDate(LocalDateTime.of(2024, 10, 20, 15, 30));
        secondPremium.setEndDate(LocalDateTime.of(2024, 11, 20, 15, 30));
    }

    @Test
    void testSuccessfulGetActivePremium() {
        when(premiumRepository.getAllActivePremium())
                .thenReturn(new ArrayList<>(List.of(secondPremium)));

        List<PremiumDto> premiumDtos = premiumService.getActivePremium();

        verify(premiumRepository).getAllActivePremium();

        PremiumDto result = premiumDtos.get(0);
        assertEquals(premiumDtos.size(), 1);
        assertEquals(result.getId(), secondPremium.getId());
        assertEquals(result.getStartDate(), secondPremium.getStartDate());
        assertEquals(result.getEndDate(), secondPremium.getEndDate());
        assertEquals(result.isActive(), secondPremium.isActive());
    }

    @Test
    void testNullListForUpdatePremium(){
        assertThrows(IllegalArgumentException.class,
                ()-> premiumService.updatePremium(null));
    }

    @Test
    void testSuccessfulUpdatePremium() {
        when(premiumRepository.findById(2L)).thenReturn(Optional.of(secondPremium));

        List<PremiumDto> premiumDtos = premiumService.updatePremium(List.of(firstPremium));

        verify(premiumRepository).save(secondPremium);

        PremiumDto result = premiumDtos.get(0);
        assertEquals(premiumDtos.size(), 1);
        assertEquals(result.getId(), firstPremium.getId());
        assertEquals(result.isActive(), firstPremium.isActive());
    }

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
        when(userService.getUserById(1L)).thenReturn(new User());

        premiumService.buyPremium(PremiumPeriod.YEAR);

        verify(premiumRepository).save(any());
    }

    private void setupMocks(boolean existsByUserId) {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(premiumRepository.existsByUserId(userId)).thenReturn(existsByUserId);
    }
}