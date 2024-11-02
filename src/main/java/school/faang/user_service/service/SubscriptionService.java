package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.validation.DataValidationException;

@Service
@AllArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(String.format("Subscribing to yourself. FollowerId = followeeId = %s", followerId));
        }

        boolean existFollowing = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (existFollowing) {
            throw new DataValidationException(String.format("Subscribing exist. FollowerId = %s, followeeId = %s ", followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(String.format("Unsubscribing to yourself. FollowerId = followeeId = %s", followerId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}
