package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void validateFollowUser(long followerId, long followeeId) {
        if (followerId <= 0 || followeeId <= 0) {
            throw new DataValidationException("ID пользователя должен быть положительным.");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует.");
        }
    }

    public void validateUnfollowUser(long followerId, long followeeId) {
        if (followerId <= 0 || followeeId <= 0) {
            throw new DataValidationException("ID пользователя должен быть положительным.");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка не существует.");
        }
    }

    public void validateUserExists(long userId) {
        if (userId <= 0) {
            throw new DataValidationException("ID пользователя должен быть положительным.");
        }
        if (!subscriptionRepository.existsById(userId)) {
            throw new DataValidationException("Пользователя не существует.");
        }
    }

    public void validateFilter(UserFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Фильтр не может быть null.");
        }
    }
}
