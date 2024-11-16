package school.faang.user_service.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final SubscriptionValidator subscriptionValidator;
    private final UserFilter userFilter;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateFollowUser(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUnfollowUser(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        subscriptionValidator.validateUserExists(followeeId);

        subscriptionValidator.validateFilter(filter);
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        List<UserDto> followersDto = userMapper.toDto(followers);

        return filterUsers(followersDto, filter);
    }

    public List<UserDto> filterUsers(List<UserDto> users, UserFilterDto filter) {
        Stream<UserDto> userStream = users.stream();
        if (userFilter.isApplicable(filter)) {
            return userFilter.apply(userStream, filter).collect(Collectors.toList());
        }
        
        return users;
    }

    public long getFollowersCount(long followeeId) {
        subscriptionValidator.validateUserExists(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, @Valid UserFilterDto filter) {
        subscriptionValidator.validateUserExists(followerId);
        subscriptionValidator.validateFilter(filter);

        List<User> following = subscriptionRepository.findByFolloweeId(followerId).toList();
        List<UserDto> followingDto = userMapper.toDto(following);

        return filterUsers(followingDto, filter);
    }

    public long getFollowingCount(long followerId) {
        subscriptionValidator.validateUserExists(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}