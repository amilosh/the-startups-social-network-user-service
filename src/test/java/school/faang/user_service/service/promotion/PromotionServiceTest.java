package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.promotion.EventPromotionMapper;
import school.faang.user_service.mapper.promotion.UserPromotionMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.validator.event.EventServiceValidator;
import school.faang.user_service.validator.promotion.PromotionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.UNSUCCESSFUL_USER_PROMOTION_PAYMENT;
import static school.faang.user_service.service.premium.util.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildActiveEventPromotions;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildActiveUserPromotions;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildEventsWithActivePromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildUsersWithActivePromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getEvent;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getEventPromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getEventPromotionResponseDto;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getEvents;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getPromotedEventResponseDto;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUser;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUserPromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUserPromotionResponseDto;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUserResponseDto;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUsers;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {
    private static final long USER_ID = 1;
    private static final long EVENT_ID = 1;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final String MESSAGE = "test message";
    private static final int NUMBER_OF_USERS = 3;
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;
    private static final long EVENT_PROMOTION_ID = 1;
    private static final int NUMBER_OF_VIEWS = PromotionTariff.STANDARD.getNumberOfViews();
    private static final int AUDIENCE_REACH = PromotionTariff.STANDARD.getAudienceReach();
    private static final LocalDateTime DATE = LocalDateTime.now();
    private static final String USER_NAME = "User name";
    private static final String TITLE = "Some title";

    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PromotionTaskService promotionTaskService;

    @Mock
    private PromotionValidator promotionValidator;

    @Mock
    private EventServiceValidator eventServiceValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PromotionBuilder promotionBuilder;

    @Mock
    private UserPromotionMapper userPromotionMapper;

    @Mock
    private EventPromotionMapper eventPromotionMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    @DisplayName("Successful buy user promotion")
    void testBuyUserPromotionSuccessful() {
        User user = getUser(USER_ID);
        PaymentResponseDto successResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        UserPromotion userPromotion = getUserPromotion(EVENT_PROMOTION_ID, user, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);
        var expectedResponseDto = getUserPromotionResponseDto(EVENT_PROMOTION_ID, USER_ID, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);

        when(userValidator.validateUser(USER_ID)).thenReturn(user);
        when(paymentService.sendPayment(TARIFF)).thenReturn(successResponse);
        when(promotionBuilder.buildUserPromotion(user, TARIFF)).thenReturn(userPromotion);
        when(userPromotionRepository.save(any(UserPromotion.class))).thenReturn(userPromotion);
        when(userPromotionMapper.toDto(userPromotion)).thenReturn(expectedResponseDto);

        UserPromotionResponseDto actualResponseDto = promotionService.buyUserPromotion(USER_ID, TARIFF);

        verify(userValidator).validateUser(USER_ID);
        verify(promotionValidator).checkUserForPromotion(user);
        verify(paymentService).sendPayment(TARIFF);
        verify(promotionValidator).checkPromotionPaymentResponse(successResponse, USER_ID, TARIFF, UNSUCCESSFUL_USER_PROMOTION_PAYMENT);
        verify(promotionBuilder).buildUserPromotion(user, TARIFF);
        verify(userPromotionRepository).save(any(UserPromotion.class));
        verify(userPromotionMapper).toDto(userPromotion);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("Buy event promotion successful")
    void testBuyEventPromotionSuccessful() {
        User user = getUser(USER_ID);
        Event event = getEvent(user);
        PaymentResponseDto successResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        EventPromotion eventPromotion = getEventPromotion(EVENT_PROMOTION_ID, event, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);
        var expectedResponseDto = getEventPromotionResponseDto(EVENT_PROMOTION_ID, EVENT_ID, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);

        when(eventServiceValidator.validateEventId(EVENT_ID)).thenReturn(event);
        when(paymentService.sendPayment(TARIFF)).thenReturn(successResponse);
        when(promotionBuilder.buildEventPromotion(event, TARIFF)).thenReturn(eventPromotion);
        when(eventPromotionRepository.save(any(EventPromotion.class))).thenReturn(eventPromotion);
        when(eventPromotionMapper.toDto(eventPromotion)).thenReturn(expectedResponseDto);

        EventPromotionResponseDto actualResponseDto = promotionService.buyEventPromotion(USER_ID, EVENT_ID, TARIFF);

        verify(eventServiceValidator).validateEventId(EVENT_ID);
        verify(promotionValidator).checkEventForUserAndPromotion(USER_ID, event);
        verify(paymentService).sendPayment(TARIFF);
        verify(promotionValidator).checkPromotionPaymentResponse(successResponse, USER_ID, TARIFF, UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT);
        verify(promotionBuilder).buildEventPromotion(event, TARIFF);
        verify(eventPromotionRepository).save(any(EventPromotion.class));
        verify(eventPromotionMapper).toDto(eventPromotion);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("Get promoted users per page success")
    void testGetPromotedUsersBeforeAllPerPageSuccessful() {
        List<User> users = buildUsersWithActivePromotion(NUMBER_OF_USERS);
        List<UserPromotion> activePromotions = buildActiveUserPromotions(NUMBER_OF_USERS);
        List<UserResponseDto> expectedResponseDtos = users.stream()
                .map(user -> getUserResponseDto(user.getId(), USER_NAME, TARIFF.toString(), NUMBER_OF_VIEWS, DATE))
                .toList();

        when(userRepository.findAllSortedByPromotedUsersPerPage(OFFSET, LIMIT)).thenReturn(users);
        when(promotionValidator.getActiveUserPromotions(users)).thenReturn(activePromotions);
        when(userMapper.toUserResponseDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return getUserResponseDto(user.getId(), USER_NAME, TARIFF.toString(), NUMBER_OF_VIEWS, DATE);
        });

        assertThat(promotionService.getPromotedUsersBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(expectedResponseDtos);

        verify(promotionTaskService).incrementUserPromotionViews(activePromotions);
    }

    @Test
    @DisplayName("Get promoted users per page with no active promotion when check then no call decrement")
    void testGetPromotedUsersBeforeAllPerPageEmptyActivePromotions() {
        List<User> users = getUsers(NUMBER_OF_USERS);
        List<UserPromotion> activePromotions = List.of();
        List<UserResponseDto> expectedResponseDtos = users.stream()
                .map(user -> getUserResponseDto(user.getId(), USER_NAME, TARIFF.toString(), NUMBER_OF_VIEWS, DATE))
                .toList();

        when(userRepository.findAllSortedByPromotedUsersPerPage(OFFSET, LIMIT)).thenReturn(users);
        when(promotionValidator.getActiveUserPromotions(users)).thenReturn(activePromotions);
        when(userMapper.toUserResponseDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return getUserResponseDto(user.getId(), USER_NAME, TARIFF.toString(), NUMBER_OF_VIEWS, DATE);
        });

        assertThat(promotionService.getPromotedUsersBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(expectedResponseDtos);

        verify(promotionTaskService, never()).incrementUserPromotionViews(activePromotions);
    }


    @Test
    @DisplayName("Get promoted events per page success")
    void testGetPromotedEventsBeforeAllPerPageSuccessful() {
        List<Event> events = buildEventsWithActivePromotion(NUMBER_OF_USERS);
        List<EventPromotion> eventPromotions = buildActiveEventPromotions(NUMBER_OF_USERS);
        List<PromotedEventResponseDto> expectedResponseDtos = events.stream()
                .map(event -> getPromotedEventResponseDto(event.getId(), TITLE, USER_ID, TARIFF.toString(), NUMBER_OF_VIEWS, DATE))
                .toList();

        when(eventRepository.findAllSortedByPromotedEventsPerPage(OFFSET, LIMIT)).thenReturn(events);
        when(promotionValidator.getActiveEventPromotions(events)).thenReturn(eventPromotions);
        when(eventMapper.toPromotedEventResponseDto(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            return getPromotedEventResponseDto(event.getId(), TITLE, USER_ID, TARIFF.toString(), NUMBER_OF_VIEWS, DATE);
        });

        assertThat(promotionService.getPromotedEventsBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(expectedResponseDtos);

        verify(promotionTaskService).batchDecrementEventPromotionViews(eventPromotions);
    }

    @Test
    @DisplayName("Get promoted events per page with no active promotion when check then no call decrement")
    void testGetPromotedEventsBeforeAllPerPageEmptyActivePromotions() {
        List<Event> events = getEvents(NUMBER_OF_USERS);
        List<EventPromotion> activePromotions = List.of();
        List<PromotedEventResponseDto> expectedResponseDtos = events.stream()
                .map(event -> getPromotedEventResponseDto(event.getId(), TITLE, USER_ID, TARIFF.toString(), NUMBER_OF_VIEWS, DATE))
                .toList();

        when(eventRepository.findAllSortedByPromotedEventsPerPage(OFFSET, LIMIT)).thenReturn(events);
        when(promotionValidator.getActiveEventPromotions(events)).thenReturn(activePromotions);
        when(eventMapper.toPromotedEventResponseDto(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            return getPromotedEventResponseDto(event.getId(), TITLE, USER_ID, TARIFF.toString(), NUMBER_OF_VIEWS, DATE);
        });

        assertThat(promotionService.getPromotedEventsBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(expectedResponseDtos);
        verify(promotionTaskService, never()).batchDecrementEventPromotionViews(activePromotions);
    }
}
