package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public ResponseEntity<Void> followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<UserDto>> getFollowers(long followeeId, UserFilterDto filter) {
        List<UserDto> users = subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Long> getFollowersCount(long followerId) {
        long followersCount = subscriptionService.getFollowersCount(followerId);
        return ResponseEntity.ok(followersCount);
    }

    public ResponseEntity<List<UserDto>> getFollowing(long followerId, UserFilterDto filter) {
        List<UserDto> users = subscriptionService.getFollowing(followerId, filter);
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Long> getFollowingCount(long followeeId) {
        long followeeCount = subscriptionService.getFollowingCount(followeeId);
        return ResponseEntity.ok(followeeCount);
    }

}
