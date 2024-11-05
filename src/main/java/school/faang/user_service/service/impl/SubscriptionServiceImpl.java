package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorMessage;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ErrorMessage.M_FOLLOW_EXIST);
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ErrorMessage.M_FOLLOW_DOES_NOT_EXIST);
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}