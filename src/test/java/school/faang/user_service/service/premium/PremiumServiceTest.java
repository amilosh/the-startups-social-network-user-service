package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserPremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.premium.PremiumMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserService userService;

    @Spy
    private PremiumMapperImpl premiumMapper;

    private PremiumDto premiumDto;
    private UserPremiumPeriod userPremiumPeriod;

    @BeforeEach
    void setUp() {
        premiumDto = PremiumDto.builder()
                .userId(1L)
                .currency(Currency.USD)
                .days(30L)
                .build();
        userPremiumPeriod = UserPremiumPeriod.ONE_MONTH_BASIC;
    }

    @Test
    public void buyPremium_WithErrorUserId_ThrowIllegalArgumentException() {
        when(userService.findById(premiumDto.getUserId()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> premiumService.buyPremium(premiumDto, userPremiumPeriod));
        verify(userService, times(1)).findById(premiumDto.getUserId());
    }

    @Test
    public void buyPremium_AlreadyHavePremiumSubscription_ThrowIllegalArgumentException() {
        whenCorrectUserId(User.builder().build());
        alreadyHavePremiumSubscription(true);

        assertThrows(IllegalArgumentException.class, () -> premiumService.buyPremium(premiumDto, userPremiumPeriod));
        verify(userService, times(1)).findById(premiumDto.getUserId());
        verify(premiumRepository, times(1)).existsByUserId(premiumDto.getUserId());
    }
    
    @Test
    public void buyPremium_WithErrorResponseToPaymentRequest_ThrowPaymentFailedException() {
        whenCorrectUserId(User.builder().build());
        alreadyHavePremiumSubscription(false);
        ArgumentCaptor<PaymentRequest> paymentRequestArgumentCaptor = ArgumentCaptor.forClass(PaymentRequest.class);
        when(paymentServiceClient.sendPayment(paymentRequestArgumentCaptor.capture()))
                .thenReturn(getPaymentResponse(PaymentStatus.ERROR, HttpStatus.BAD_REQUEST));

        assertThrows(PaymentFailedException.class, () -> premiumService.buyPremium(premiumDto, userPremiumPeriod));
        verify(userService, times(1)).findById(premiumDto.getUserId());
        verify(premiumRepository, times(1)).existsByUserId(premiumDto.getUserId());
        verify(paymentServiceClient, times(1)).sendPayment(paymentRequestArgumentCaptor.capture());
    }

    @Test
    public void buyPremium_WithCorrectResponseToPaymentRequest_ReturnPremiumDto () {
        whenCorrectUserId(User.builder().id(1L).build());
        alreadyHavePremiumSubscription(false);
        ArgumentCaptor<PaymentRequest> paymentRequestArgumentCaptor = ArgumentCaptor.forClass(PaymentRequest.class);
        when(paymentServiceClient.sendPayment(paymentRequestArgumentCaptor.capture()))
                .thenReturn(getPaymentResponse(PaymentStatus.SUCCESS, HttpStatus.OK));

        ResponseEntity<PremiumDto> premiumDtoResponseEntity = premiumService.buyPremium(premiumDto, userPremiumPeriod);
        assertEquals(premiumDtoResponseEntity.getStatusCode(), HttpStatus.CREATED);
        assertNotNull(premiumDtoResponseEntity.getBody());
        ArgumentCaptor<Premium> premiumArgumentCaptor = ArgumentCaptor.forClass(Premium.class);
        verify(premiumRepository, times(1)).save(premiumArgumentCaptor.capture());
    }

    private void alreadyHavePremiumSubscription(boolean flag) {
        when(premiumRepository.existsByUserId(premiumDto.getUserId()))
                .thenReturn(flag);
    }

    private void whenCorrectUserId(User user) {
        when(userService.findById(premiumDto.getUserId()))
                .thenReturn(Optional.of(user));
    }

    private ResponseEntity<PaymentResponse> getPaymentResponse(PaymentStatus status, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .body(PaymentResponse.builder().status(status).build());
    }
}
