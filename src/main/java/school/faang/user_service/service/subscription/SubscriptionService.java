package school.faang.user_service.service.subscription;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.util.List;

@Data
@Service
@Slf4j
public class SubscriptionService {
    private final SubscriptionValidator subscriptionValidator;
    private final SubscriptionRepository subscriptionRepository;
    private final UserFilter userFilter;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateUserIsTryingToCallHimself(
                followerId,
                followeeId,
                String.format("User %s trying to subscribe to himself", followerId)
        );
        subscriptionValidator.validateUserAlreadyHasThisSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);

        log.info("User {} subscribed to user {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUserIsTryingToCallHimself(
                followerId,
                followeeId,
                String.format("User %s trying to unfollow himself", followerId)
        );
        subscriptionRepository.unfollowUser(followerId, followeeId);

        log.info("User {} unfollowed the user {}", followerId, followeeId);
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
        subscriptionValidator.validateUserFilterIsApplicable(filter);

        return users.stream()
                .filter(user -> userFilter.apply(user, filter))
                .map(userMapper::toDto)
                .toList();
    }
}
