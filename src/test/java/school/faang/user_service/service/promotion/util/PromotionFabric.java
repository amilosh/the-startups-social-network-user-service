package school.faang.user_service.service.promotion.util;

import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

public class PromotionFabric {
    public static final int ACTIVE_NUMBER_OF_VIEWS = 1;
    public static final int NON_ACTIVE_NUMBER_OF_VIEWS = 0;

    public static EventPromotion getEventPromotion(long id, Event event, int numberOfViews, int audienceReach,
                                                   LocalDateTime creationDate) {
        return EventPromotion
                .builder()
                .id(id)
                .event(event)
                .numberOfViews(numberOfViews)
                .audienceReach(audienceReach)
                .creationDate(creationDate)
                .build();
    }

    public static EventPromotion getEventPromotion(PromotionTariff tariff, int numberOfViews) {
        return EventPromotion
                .builder()
                .promotionTariff(tariff)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static EventPromotion getEventPromotion(long id, int numberOfViews) {
        return EventPromotion
                .builder()
                .id(id)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static Event getEvent(long id, String title, User owner, EventPromotion promotion) {
        return Event
                .builder()
                .id(id)
                .title(title)
                .owner(owner)
                .promotion(promotion)
                .build();
    }

    public static Event getEvent(long id) {
        return Event
                .builder()
                .id(id)
                .build();
    }

    public static Event getEvent(User owner) {
        return Event
                .builder()
                .owner(owner)
                .build();
    }

    public static Event getEvent(Long eventId, User owner) {
        return Event
                .builder()
                .id(eventId)
                .owner(owner)
                .build();
    }

    public static Event getEvent(long id, EventPromotion eventPromotion) {
        return Event
                .builder()
                .id(id)
                .promotion(eventPromotion)
                .build();
    }

    public static Event getEvent(User owner, EventPromotion eventPromotion) {
        return Event
                .builder()
                .owner(owner)
                .promotion(eventPromotion)
                .build();
    }

    public static List<Event> getEvents(int number, EventPromotion eventPromotion) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> getEvent(i, eventPromotion))
                .toList();
    }

    public static List<Event> getEvents(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::getEvent)
                .toList();
    }

    public static UserPromotion getUserPromotion(long id, User user, int numberOfViews, int audienceReach,
                                                 LocalDateTime creationDate) {
        return UserPromotion
                .builder()
                .id(id)
                .user(user)
                .numberOfViews(numberOfViews)
                .audienceReach(audienceReach)
                .creationDate(creationDate)
                .build();
    }

    public static UserPromotion getUserPromotion(PromotionTariff tariff, int numberOfViews) {
        return UserPromotion
                .builder()
                .promotionTariff(tariff)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static UserPromotion getUserPromotion(long id) {
        return UserPromotion
                .builder()
                .id(id)
                .build();
    }

    public static UserPromotion getUserPromotion(long id, int numberOfViews) {
        return UserPromotion
                .builder()
                .id(id)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static User getUser(long id, String username, UserPromotion promotion) {
        return User
                .builder()
                .id(id)
                .username(username)
                .promotion(promotion)
                .build();
    }

    public static User getUser(long id, String username, UserPromotion promotion, LocalDateTime createdAt) {
        return User
                .builder()
                .id(id)
                .username(username)
                .promotion(promotion)
                .createdAt(createdAt)
                .build();
    }

    public static User getUser(long id, UserPromotion promotion) {
        return User
                .builder()
                .id(id)
                .promotion(promotion)
                .build();
    }

    public static User getUser(long id) {
        return User
                .builder()
                .id(id)
                .build();
    }

    public static List<User> getUsers(int number, UserPromotion userPromotion) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> getUser(i, userPromotion))
                .toList();
    }

    public static List<User> getUsers(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::getUser)
                .toList();
    }

    public static List<User> buildUsersWithActivePromotion(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildUserWithActivePromotion)
                .toList();
    }

    public static User buildUserWithActivePromotion(Long id) {
        return User
                .builder()
                .id(id)
                .promotion(buildActiveUserPromotion(id))
                .build();
    }

    public static List<UserPromotion> buildActiveUserPromotions(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildActiveUserPromotion)
                .toList();
    }

    public static UserPromotion buildActiveUserPromotion(Long id) {
        return UserPromotion
                .builder()
                .id(id)
                .numberOfViews(ACTIVE_NUMBER_OF_VIEWS)
                .build();
    }

    public static UserPromotion buildNonActiveUserPromotion(Long id) {
        return UserPromotion
                .builder()
                .id(id)
                .numberOfViews(NON_ACTIVE_NUMBER_OF_VIEWS)
                .build();
    }

    public static List<Event> buildEventsWithActivePromotion(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildEventWithActivePromotion)
                .toList();
    }

    public static Event buildEventWithActivePromotion(Long id) {
        return Event
                .builder()
                .id(id)
                .promotion(buildActiveEventPromotion(id))
                .build();
    }

    public static List<EventPromotion> buildActiveEventPromotions(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildActiveEventPromotion)
                .toList();
    }

    public static EventPromotion buildActiveEventPromotion(Long id) {
        return EventPromotion
                .builder()
                .id(id)
                .numberOfViews(ACTIVE_NUMBER_OF_VIEWS)
                .build();
    }

    public static UserPromotionResponseDto getUserPromotionResponseDto(Long id, Long userId, int numberOfViews,
                                                                       int audienceReach, LocalDateTime creationDate) {
        return UserPromotionResponseDto
                .builder()
                .id(id)
                .userId(userId)
                .numberOfViews(numberOfViews)
                .audienceReach(audienceReach)
                .creationDate(creationDate)
                .build();
    }

    public static EventPromotionResponseDto getEventPromotionResponseDto(Long id, Long eventId, int numberOfViews,
                                                                         int audienceReach, LocalDateTime creationDate) {
        return EventPromotionResponseDto
                .builder()
                .id(id)
                .eventId(eventId)
                .numberOfViews(numberOfViews)
                .audienceReach(audienceReach)
                .creationDate(creationDate)
                .build();
    }

    public static UserResponseDto getUserResponseDto(Long id, String userName, String promotionTariff,
                                                     int numberOfViews, LocalDateTime createdAt) {
        return UserResponseDto
                .builder()
                .id(id)
                .username(userName)
                .promotionTariff(promotionTariff)
                .numberOfViews(numberOfViews)
                .createdAt(createdAt)
                .build();
    }

    public static PromotedEventResponseDto getPromotedEventResponseDto(Long id, String title, Long ownerId, String promotionTariff,
                                                                       int numberOfViews, LocalDateTime createdAt) {
        return PromotedEventResponseDto
                .builder()
                .id(id)
                .title(title)
                .ownerId(ownerId)
                .promotionTariff(promotionTariff)
                .numberOfViews(numberOfViews)
                .createdAt(createdAt)
                .build();
    }
}