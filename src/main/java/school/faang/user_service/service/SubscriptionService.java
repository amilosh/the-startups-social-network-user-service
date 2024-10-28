package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void followUser(long followerId, long followeeId) {
        log.info("Attempting to follow: followerId={}, followeeId={}", followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("Subscription already exists: followerId={}, followeeId={}", followerId, followeeId);
            throw new IllegalArgumentException("Subscription already exists");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Successfully added: followerId={}, followeeId={}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        log.info("Attempting to unfollow: followerId={}, followeeId={}", followerId, followeeId);
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("Subscription does not exist. FollowerId: {}, FolloweeId: {}", followerId, followeeId);
            throw new IllegalArgumentException("Subscription does not exist");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("Unsubcription succesful: followerId={}, followeeId={}", followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        log.info("Getting followers for followeeId={}, filter={}", followeeId, filter);
        List<UserDto> followers = (List<UserDto>) subscriptionRepository.findByFolloweeId(followeeId);
        log.info("Found {} followers", followers.size());
        return filterUsers(followers, filter);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        log.info("Fetching following list for followeeId={}, filter={}", followeeId, filter);
        List<UserDto> following = (List<UserDto>) subscriptionRepository.findByFolloweeId(followeeId);
        log.info("Retrieved {} users followed by followeeId={}", following.size(), followeeId);
        return filterUsers(following, filter);
    }


    public long getFolloweeCount(long followerId) {
        log.info("Counting followees for followerId={}", followerId);
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.info("FollowerId={} follows count={}", followerId, count);
        return count;
    }

    public long getFollowingCount(long followeeId) {
        log.info("Counting followers for followeeId={}", followeeId);
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.info("FollowerId={} has followers", followeeId, count);
        return count;
    }

    private List<UserDto> filterUsers(List<UserDto> users, UserFilterDto filter) {
        log.info("Applying filters: {}", filter);
        List<UserDto> filteredUsers = users.stream()
            .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
            .collect(Collectors.toList());
        log.info("Filtered users count after applying filters: {}", filteredUsers.size());
        return filteredUsers;
    }
}
