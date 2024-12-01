package school.faang.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> followUser(@Valid @PathVariable long followerId, @Valid @PathVariable long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@Valid @PathVariable long followerId, @Valid @PathVariable long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{followeeId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@Valid @PathVariable long followeeId, @Valid UserFilterDto filter) {
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("{followerId}/followers/count")
    public ResponseEntity<Long> getFollowersCount(@Valid @PathVariable long followerId) {
        long count = subscriptionService.getFollowersCount(followerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{followeeId}/following")
    public ResponseEntity<List<UserDto>> getFollowing(@Valid @PathVariable long followeeId, @Valid UserFilterDto filter) {
        List<UserDto> following = subscriptionService.getFollowing(followeeId, filter);
        return ResponseEntity.ok(following);
    }

    @GetMapping("{followerId}/following/count")
    public ResponseEntity<Long> getFollowingCount(@Valid @PathVariable long followerId) {
        long count = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(count);
    }
}