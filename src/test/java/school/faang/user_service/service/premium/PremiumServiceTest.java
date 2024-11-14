package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.validator.premium.PremiumValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.premium.util.PremiumFabric.buildPremiums;
import static school.faang.user_service.service.premium.util.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.service.premium.util.PremiumFabric.getPremium;
import static school.faang.user_service.service.premium.util.PremiumFabric.getResponsePremiumDto;
import static school.faang.user_service.service.premium.util.PremiumFabric.getUser;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    private static final long USER_ID = 1L;
    private static final PremiumPeriod PERIOD = PremiumPeriod.ONE_MONTH;
    private static final String MESSAGE = "test message";
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final int NUMBER_OF_PREMIUMS = 3;
    private static final long PREMIUM_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.now();
    private static final LocalDateTime END_DATE = START_DATE.plusDays(30);

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PremiumBuilder premiumBuilder;

    @Mock
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    @DisplayName("Buy premium successful")
    void testBuyPremiumSuccessful() {
        User user = getUser(USER_ID);
        PaymentResponseDto successResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        Premium premium = getPremium(PREMIUM_ID, user, START_DATE, END_DATE);
        ResponsePremiumDto expectedResponseDto = getResponsePremiumDto(PREMIUM_ID, USER_ID, START_DATE, END_DATE);

        when(userValidator.validateUser(USER_ID)).thenReturn(user);
        when(paymentService.sendPayment(PERIOD)).thenReturn(successResponse);
        when(premiumBuilder.buildPremium(user, PERIOD)).thenReturn(premium);
        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);
        when(premiumMapper.toDto(premium)).thenReturn(expectedResponseDto);

        ResponsePremiumDto actualResponseDto = premiumService.buyPremium(USER_ID, PERIOD);

        verify(userValidator).validateUser(USER_ID);
        verify(premiumValidator).validateUserForSubPeriod(user);
        verify(paymentService).sendPayment(PERIOD);
        verify(premiumValidator).checkPaymentResponse(successResponse, USER_ID, PERIOD);
        verify(premiumBuilder).buildPremium(user, PERIOD);
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toDto(premium);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("Find all premium by end date before successful")
    void testFindAllByEndDateBeforeSuccessful() {
        premiumService.findAllByEndDateBefore(LOCAL_DATE_TIME);

        verify(premiumRepository).findAllByEndDateBefore(LOCAL_DATE_TIME);
    }

    @Test
    @DisplayName("Delete all premiums by id successful")
    void testDeleteAllPremiumsByIdAsyncSuccessful() {
        List<Premium> premiums = buildPremiums(NUMBER_OF_PREMIUMS);
        premiumService.deleteAllPremiumsById(premiums);

        verify(premiumRepository).deleteAllInBatch(anyList());
    }
}
