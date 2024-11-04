package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Objects;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(Long followerId, Long followeeId) throws DataValidationException {
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("You cannot follow yourself");
        }
        subscriptionService.followUser(followerId, followeeId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
