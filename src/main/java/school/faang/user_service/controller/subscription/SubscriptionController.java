package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionRequestDto followUser(Long followerId, Long followeeId) {
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("You cannot follow yourself");
        }
        return subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow")
    public SubscriptionRequestDto unfollowUser(Long followerId, Long followeeId) {
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("You cannot unfollow yourself");
        }
        return subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}")
    public List<SubscriptionUserDto> getFollowers(@PathVariable("followeeId") Long followeeId) {
        return subscriptionService.getFollowers(followeeId);
    }

    @PostMapping("/{followeeId}/filtered")
    public List<SubscriptionUserDto> getFilteredFollowers(@PathVariable("followeeId") Long followeeId, @RequestBody SubscriptionUserFilterDto filter) {
        return subscriptionService.getFilteredFollowers(followeeId, filter);
    }

    @GetMapping("/followers-count")
    public Integer getFollowersCount(Long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("/following")
    public List<SubscriptionUserDto> getFollowing(Long followeeId, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        return subscriptionService.getFollowing(followeeId, subscriptionUserFilterDto);
    }

    @GetMapping("following-count")
    public Integer getFollowingCount(Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
