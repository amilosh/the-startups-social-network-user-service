package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;
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

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(Long followerId, Long followeeId) throws DataValidationException {
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("You cannot unfollow yourself");
        }
        subscriptionService.unfollowUser(followerId, followeeId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getFollowers(Long followeeId, UserFilterDto filter) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subscriptionService.getFollowers(followeeId, filter));
    }

}
