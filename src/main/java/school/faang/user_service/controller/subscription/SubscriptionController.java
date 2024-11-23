package school.faang.user_service.controller.subscription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/follow/{followeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(
            @PathVariable @NotNull(message = "Follower ID should not be null") Long followerId,
            @PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(
            @PathVariable @NotNull(message = "Follower ID should not be null") Long followerId,
            @PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowers(
            @PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId,
            @Valid @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/{followerId}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowing(
            @PathVariable @NotNull(message = "Follower ID should not be null") Long followerId,
            @Valid @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/{followeeId}/followers/count")
    @ResponseStatus(HttpStatus.OK)
    public int getFollowersCount(@PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/following/count")
    @ResponseStatus(HttpStatus.OK)
    public int getFollowingCount(@PathVariable @NotNull(message = "Follower ID should not be null") Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
