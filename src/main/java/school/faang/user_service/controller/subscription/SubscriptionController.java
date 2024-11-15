package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@Controller
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        isFollowerFolloweeIdsEqual(followerId, followeeId, "Follower can't follow itself");

        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        isFollowerFolloweeIdsEqual(followerId, followeeId, "Follower can't unfollow itself");

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/count/{followeeId}")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @PostMapping("/followings/{followeeId}")
    public List<UserDto> getFollowing(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/followings/count/{followeeId}")
    public int getFollowingCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    private void isFollowerFolloweeIdsEqual(long followerId, long followeeId, String message) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }
}