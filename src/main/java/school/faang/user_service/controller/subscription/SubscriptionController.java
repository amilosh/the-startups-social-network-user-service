package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(Long followerId, Long followeeId) throws DataValidationException {
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("You cannot follow yourself");
        }

        subscriptionService.followUser(followerId, followeeId);
    }

}
