package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final static String PROMOTION_TARGET = "profile";

    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final PromotionRepository promotionRepository;
    private final List<UserFilter> userFilters;

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(premiumUsers,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto deactivateUser(UserDto userDto) {
        log.info("Деактивация пользователя с ID: {}", userDto.getId());
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new NoSuchElementException("Пользователь с ID " + userDto.getId() + " не найден"));
        stopUserActivities(user);
        user.setActive(false);
        mentorshipService.stopMentorship(user);
        userRepository.save(user);
        log.info("Пользователь с ID: {} был успешно деактивирован", userDto.getId());
        return userMapper.toDto(user);
    }

    public User stopUserActivities(User user) {
        log.info("Останавливаем активности для пользователя с ID: {}", user.getId());
        stopGoals(user);
        stopEvents(user);
        log.info("Активности пользователя с ID: {} остановлены", user.getId());
        return user;
    }

    public User stopGoals(User user) {
        List<Goal> goals = user.getGoals();
        log.info("Останавливаем цели для пользователя с ID: {}. Количество целей: {}", user.getId(), goals.size());
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1 && goal.getUsers().contains(user)) {
                log.info("Удаляем цель с ID: {} для пользователя с ID: {}", goal.getId(), user.getId());
                goalRepository.deleteById(goal.getId());
            }
        }
        user.setGoals(new ArrayList<>());
        log.info("Цели пользователя с ID: {} были очищены", user.getId());
        return user;
    }

    public User stopEvents(User user) {
        List<Event> ownedEvents = user.getOwnedEvents();
        List<Long> eventsIdList = new ArrayList<>();
        log.info("Останавливаем события для пользователя с ID: {}. Количество собственных событий: {}", user.getId(), ownedEvents.size());
        if (ownedEvents != null || !ownedEvents.isEmpty()) {
            for (Event event : ownedEvents) {
                eventsIdList.add(event.getId());
                log.info("Удаляем событие с ID: {} для пользователя с ID: {}", event.getId(), user.getId());
            }
            eventRepository.deleteAllById(eventsIdList);
        }
        user.setOwnedEvents(new ArrayList<>());

        List<Event> participatedEvents = user.getParticipatedEvents();
        log.info("Останавливаем участие в событиях для пользователя с ID: {}. Количество участвующих событий: {}", user.getId(), participatedEvents.size());
        if (participatedEvents != null || !participatedEvents.isEmpty()) {
            for (Event event : participatedEvents) {
                List<User> attendees = new ArrayList<>(event.getAttendees());
                attendees.remove(user);
                event.setAttendees(attendees);
                log.info("Удаляем пользователя с ID: {} из участников события с ID: {}", user.getId(), event.getId());
            }
            user.setParticipatedEvents(new ArrayList<>());
        }
        log.info("Участие пользователя с ID: {} в событиях было очищено", user.getId());
        return user;
    }

    public UserDto getUser(long userId) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {

        final List<User> users = userRepository.findAllById(ids);

        return userMapper.toListUserDto(users);
    }

    @Transactional
    public List<UserDto> getFilteredUsers(UserFilterDto filterDto, Long callingUserId) {
        User callingUser = userRepository.findById(callingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<User> filteredUsers = getFilteredUsersFromRepository(filterDto);
        List<User> priorityFilteredUsers = getPriorityFilteredUsers(filteredUsers, callingUser);

        decrementRemainingShows(priorityFilteredUsers);
        deleteExpiredProfilePromotions();

        return priorityFilteredUsers.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private List<User> getFilteredUsersFromRepository(UserFilterDto filterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(spec -> userRepository.findAll((Specification<User>) spec))
                .orElseGet(Collections::emptyList);
    }

    private List<User> getPriorityFilteredUsers(List<User> filteredUsers, User callingUser) {
        return filteredUsers.stream()
                .sorted((Comparator
                        .comparing((User user) -> calculateCountryPriority(user, callingUser))
                        .thenComparing(this::calculatePriorityLevel)))
                .toList();
    }

    private void decrementRemainingShows(List<User> priorityFilteredUsers) {
        List<Long> promotionIds = priorityFilteredUsers.stream()
                .flatMap(user -> {
                    List<Promotion> promotions = user.getPromotions();
                    if (promotions == null) {
                        return Stream.empty();
                    }
                    return promotions.stream()
                            .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()) &&
                                    promotion.getRemainingShows() > 0)
                            .map(Promotion::getId);
                })
                .toList();

        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseRemainingShows(promotionIds, PROMOTION_TARGET);
        }
    }

    private void deleteExpiredProfilePromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions(UserService.PROMOTION_TARGET);
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }

    private Promotion getTargetPromotion(User user) {
        return user.getPromotions().stream()
                .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()))
                .findFirst()
                .orElse(null);
    }

    private int calculateCountryPriority(User user, User callingUser) {
        if (user.getPromotions() == null || user.getPromotions().isEmpty()) {
            return 1;
        }

        Promotion targetPromotion = getTargetPromotion(user);

        if (targetPromotion != null &&
                targetPromotion.getPriorityLevel() == 3 &&
                !user.getCountry().equals(callingUser.getCountry())) {
            return 1;
        }

        if (targetPromotion == null) {
            return 1;
        }

        return 0;
    }

    private int calculatePriorityLevel(User user) {
        if (user.getPromotions() == null || user.getPromotions().isEmpty()) {
            return 0;
        }

        Promotion targetPromotion = getTargetPromotion(user);

        return targetPromotion != null ? -targetPromotion.getPriorityLevel() : 0;
    }
}
