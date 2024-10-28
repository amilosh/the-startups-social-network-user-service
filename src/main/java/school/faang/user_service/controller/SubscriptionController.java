package school.faang.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId,@RequestParam long followeeId) {
        log.info("Attempt to follow user. FollowerId={}, followeeId={}", followerId, followeeId);
        if (followerId == followeeId) {
            log.info("Attempt self-subscription. FollowerId={}", followerId);
            throw new IllegalArgumentException("You can't subscribe to yourself");
        }
        subscriptionService.followUser(followerId, followeeId);
        log.info("Successfully followed user. FollowerId={}, FolloweeId={}", followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId){
        log.info("Attempt to unfollow:: followerId={}, followeeId={}", followerId, followeeId);
       if(followerId == followeeId){
           log.warn("Attempted self-unsubscription. FollowerId: {}", followerId);
           throw new IllegalArgumentException("You can't unsubscribe from yourself");
       }
       subscriptionService.unfollowUser(followerId, followeeId);
        log.info("Successfully unfollowed user. FollowerId: {}, FolloweeId: {}", followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@RequestParam long followeeId, @RequestParam UserFilterDto filter) {
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, filter);
        log.info("Fetched {} followers for FolloweeId={}", followers.size(), followeeId);
        return followers;
    }

    @GetMapping("/followees")
    public List<UserDto> getFollowing(@RequestParam long followerId, @RequestParam UserFilterDto filter) {
        log.info("Request to get followees. FollowerId={}, Filter={}", followerId, filter);
        List<UserDto> followees = subscriptionService.getFollowing(followerId, filter);
        log.info("Fetched {} followees for FollowerId={}", followees.size(), followerId);
        return followees;
    }

    @GetMapping("/followeeCount")
    public long getFollowerCount(@RequestParam long followeeId) {
        log.info("Request to get follower count for FolloweeId={}", followeeId);
        long count = subscriptionService.getFolloweeCount(followeeId);
        log.info("Fetched follower count: {} for FolloweeId={}", count, followeeId);
        return count;    }

    @GetMapping("/followingCount")
    public long getFollowingCount(@RequestParam long followerId) {
        log.info("Request to get following count for FollowerId={}", followerId);
        long count = subscriptionService.getFollowingCount(followerId);
        log.info("Fetched following count: {} for FollowerId={}", count, followerId);
        return count;
    }
}
