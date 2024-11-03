package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.InvalidUserIdException;
import school.faang.user_service.filter.ExperienceFilter;
import school.faang.user_service.filter.NameFilter;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        log.info("Пользователь {} пытается подписаться на пользователя {}", followerId, followeeId);
        validateUserIds(followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("Подписка между пользователями {} и {} уже существует.", followerId, followeeId);
            throw new IllegalArgumentException("Подписка уже существует.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Пользователь {} успешно подписался на пользователя {}.", followerId, followeeId);
    }

    public void unfollowUser(Long followerId, Long followeeId) {
        log.info("Пользователь {} пытается отписаться от пользователя {}", followerId, followeeId);
        validateUserIds(followerId, followeeId);
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("Подписка между пользователями {} и {} не существует.", followerId, followeeId);
            throw new IllegalArgumentException("Подписка не существует.");
        }

        try {
            subscriptionRepository.unfollowUser(followerId, followeeId);
            log.info("Пользователь {} успешно отписался от пользователя {}.", followerId, followeeId);
        } catch (Exception ex) {
            log.error("Произошла ошибка при отписке пользователя: followerId={}, followeeId={}", followerId, followeeId, ex);
            throw new RuntimeException("Не удалось отписаться от пользователя.", ex);
        }
    }

    public List<UserDTO> getFollowers(Long userId, UserFilterDTO filter) {
        log.info("Запрос на получение подписчиков пользователя {}", userId);
        if (filter == null) {
            log.warn("Фильтр не может быть null.");
            throw new IllegalArgumentException("Фильтр не может быть null.");
        }
        return filterUsers(subscriptionRepository.findByFolloweeId(userId).collect(Collectors.toList()), filter);
    }

    public boolean subscriptionExists(Long followerId, Long followeeId) {
        log.info("Проверка существования подписки между пользователями {} и {}", followerId, followeeId);
        boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        log.info("Подписка между пользователями {} и {} {}", followerId, followeeId, exists ? "существует." : "не существует.");
        return exists;
    }

    public List<UserDTO> filterUsers(List<User> users, UserFilterDTO filter) {
        log.info("Применение фильтров к пользователям. Количество пользователей: {}", users.size());

        return users.stream()
            .filter(user -> {
                boolean matches = true;
                if (filter.getNamePattern() != null) {
                    matches = matches && new NameFilter(filter.getNamePattern()).filter(user);
                    log.info("Фильтр по имени с паттерном: {}", filter.getNamePattern());
                }
                if (filter.getExperienceMin() != null && filter.getExperienceMax() != null) {
                    matches = matches && new ExperienceFilter(filter.getExperienceMin(), filter.getExperienceMax()).filter(user);
                    log.info("Фильтр по опыту: min={}, max={}", filter.getExperienceMin(), filter.getExperienceMax());
                } else if (filter.getExperienceMin() != null) {
                    matches = matches && user.getExperience() >= filter.getExperienceMin();
                } else if (filter.getExperienceMax() != null) {
                    matches = matches && user.getExperience() <= filter.getExperienceMax();
                }
                return matches;
            })
            .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail()))
            .peek(filteredUser -> log.info("Добавлен пользователь: {}", filteredUser.getUsername()))
            .collect(Collectors.toList());
    }

    private void validateUserIds(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null || followerId.equals(followeeId)) {
            throw new InvalidUserIdException("Некорректные ID: ID не должны быть null и не должны совпадать.");
        }
    }

    private boolean isValidFilter(UserFilterDTO filter) {
        return filter.getExperienceMin() == null || filter.getExperienceMax() == null ||
            filter.getExperienceMin() <= filter.getExperienceMax();
    }
}
