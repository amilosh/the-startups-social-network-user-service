package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final SubscriptionValidator subscriptionValidator;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateUserIsTryingToCallHimself(followerId, followeeId);
        subscriptionValidator.validateUserAlreadyHasThisSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);

        log.info("User {} subscribed to user {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUserIsTryingToCallHimself(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);

        log.info("User {} unfollowed user {}", followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> users = subscriptionRepository.findByFolloweeId(followeeId)
                .toList();

        return filterUsers(users, filter);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        List<User> users = subscriptionRepository.findByFollowerId(followerId)
                .toList();

        return filterUsers(users, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(List<User> users, UserFilterDto filter) {
        Stream<User> userStream = users.stream();

        return userFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .flatMap(f -> f.apply(userStream, filter))
                .map(userMapper::toDto)
                .toList();
    }
}
