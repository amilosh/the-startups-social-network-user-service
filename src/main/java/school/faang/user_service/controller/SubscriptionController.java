package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(@RequestParam Long followerId, @RequestParam Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@RequestParam Long followerId, @RequestParam Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getFollowers(
        @RequestParam Long userId,
        @RequestBody(required = false) UserFilterDTO filter) {
        return subscriptionService.getFollowers(userId, filter);
    }
}
