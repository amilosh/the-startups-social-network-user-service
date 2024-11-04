package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(Long followerId, Long followeeId) throws DataValidationException {
        Boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (exists) {
            throw new DataValidationException("The subscription already exists");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }
}
