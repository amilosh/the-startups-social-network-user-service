package school.faang.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.FollowDto;
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

    @PostMapping("/followers")
    public ResponseEntity<Void> followUser(@Valid @RequestBody FollowDto followDto) {
        subscriptionService.followUser(followDto.getFollowerId(), followDto.getFolloweeId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/followers")
    public ResponseEntity<Void> unfollowUser(@Valid @RequestBody FollowDto followDto) {
        subscriptionService.unfollowUser(followDto.getFollowerId(), followDto.getFolloweeId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/followers/{followeeId}")
    public ResponseEntity<List<UserDto>> getFollowers(@Valid @PathVariable long followeeId, @Valid @RequestBody UserFilterDto filter) {
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/followers-count/{followerId}")
    public ResponseEntity<Long> getFollowersCount(@Valid @PathVariable long followerId) {
        long count = subscriptionService.getFollowersCount(followerId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/following/{followeeId}")
    public ResponseEntity<List<UserDto>> getFollowing(@Valid @PathVariable long followeeId, @Valid @RequestBody UserFilterDto filter) {
        List<UserDto> following = subscriptionService.getFollowing(followeeId, filter);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/following-count/{followerId}")
    public ResponseEntity<Long> getFollowingCount(@Valid @PathVariable long followerId) {
        long count = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(count);
    }
}