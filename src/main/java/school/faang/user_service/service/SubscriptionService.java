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
import school.faang.user_service.service.validation.SubscriptionValidation;
import school.faang.user_service.service.validation.UserValidation;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final SubscriptionValidation subscriptionValidation;
    private final UserValidation userValidation;

    public void followUser(long followerId, long followeeId) {
        userValidation.isUserExists(followerId, followeeId);
        subscriptionValidation.isFollowingExistsValidate(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User with id: {} follow user with id: {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        userValidation.isUserExists(followerId, followeeId);
        subscriptionValidation.isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User with id: {} unfollow user with id: {}", followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        userValidation.isUserExists(followeeId);

        if (isNull(filter)) {
            log.info("Getting followers for user with id: {}", followeeId);
            return userMapper.entityStreamToDtoList(subscriptionRepository.findByFolloweeId(followeeId));
        }
        return filterUsers(followeeId, filter);
    }

    private List<UserDto> filterUsers(long followeeId, UserFilterDto filter) {
        userValidation.isUserExists(followeeId);

        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filter)) {
                followers = userFilter.apply(followers, filter);
            }
        }
        log.info("Getting filtered followers/followings for user with id {}", followeeId);
        return userMapper.entityStreamToDtoList(followers);
    }

    public int getFollowersCount(long followeeId) {
        userValidation.isUserExists(followeeId);

        log.info("Getting followers count for user with id {}", followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        userValidation.isUserExists(followeeId);

        if (isNull(filter)) {
            log.info("Getting followings for user with id: {}", followeeId);
            return userMapper.entityStreamToDtoList(subscriptionRepository.findByFolloweeId(followeeId));
        }
        return filterUsers(followeeId, filter);
    }

    public int getFollowingCount(long followeeId) {
        userValidation.isUserExists(followeeId);

        log.info("Getting followings count for user with id: {}", followeeId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    private boolean isNull(UserFilterDto userFilterDto) {
        return userFilterDto == null;
    }
}