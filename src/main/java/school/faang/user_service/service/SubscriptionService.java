package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.SubscriptionFilter;
import school.faang.user_service.filter.UserFilterFactory;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User is already following this user");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User is not following this user");
        } else {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }

    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        if (users == null) {
            throw new DataValidationException("User does not exist");
        }
        return filterUsers(users, filter);
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(users, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        List<SubscriptionFilter> filters = UserFilterFactory.createFilters(filterDto);
        List<SubscriptionFilter> applicableFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();

        List<User> filteredUsers = users
                .filter(user -> applicableFilters.stream().allMatch(filter -> filter.apply(user)))
                .toList();

        return userMapper.toDto(filteredUsers);
    }
}
