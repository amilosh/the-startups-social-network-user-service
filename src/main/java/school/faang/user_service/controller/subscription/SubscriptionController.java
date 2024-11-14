package school.faang.user_service.controller.subscription;

import lombok.Data;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@Data
@Controller
public class SubscriptionController {
    private SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
