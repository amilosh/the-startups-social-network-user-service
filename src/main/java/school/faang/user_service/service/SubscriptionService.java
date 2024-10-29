package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user_filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Follower can't follow itself");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You already follow this user");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User with id: {} follow user with id: {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Follower can't unfollow itself");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You don't follow this user");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User with id: {} unfollow user with id: {}", followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        if (filter == null) {
            log.info("Getting followers for user with id: {}", followeeId);
            return subscriptionRepository.findByFolloweeId(followeeId)
                    .map(userMapper::ToDto)
                    .toList();
        }
        return filterUsers(followeeId, filter);
    }

    public List<UserDto> filterUsers(long followeeId, UserFilterDto filter) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filter)) {
                followers = userFilter.apply(followers, filter);
            }
        }
        log.info("Getting filtered followers for user with id {}", followeeId);
        return followers.map(userMapper::ToDto).toList();
    }

    public int getFollowersCount(long followeeId) {
        log.info("Getting followers count for user with id {}", followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        if (filter == null) {
            log.info("Getting user's followings with userId: {}", followeeId);
            return subscriptionRepository.findByFolloweeId(followeeId)
                    .map(userMapper::ToDto)
                    .toList();
        }
        return filterUsers(followeeId, filter);
    }

    public int getFollowingCount(long followeeId) {
        log.info("Getting user's followings count with userId: {}", followeeId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }
}