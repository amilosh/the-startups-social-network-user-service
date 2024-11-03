package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(String.format("Subscribing to yourself. FollowerId = followeeId = %s", followerId));
        }

        boolean existFollowing = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (existFollowing) {
            throw new DataValidationException(String.format("Subscribing exist. FollowerId = %s, followeeId = %s ", followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(String.format("Unsubscribing to yourself. FollowerId = followeeId = %s", followerId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);

        return users.filter(user -> UserFilter.applyAllFilters(userFilters, user, filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);

        return users.filter(user -> UserFilter.applyAllFilters(userFilters, user, filterDto))
                .map(userMapper::toDto)
                .toList();
    }

}
