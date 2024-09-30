package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator validator;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        validator.isSubscriber(followerId, followeeId, subscriptionRepository);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validator.isNotSubscriber(followerId, followeeId, subscriptionRepository);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        validator.validateId(followeeId);
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));

        return userMapper.toDto(users.toList());
    }

    public int getFollowersCount(long followeeId) {
        validator.validateId(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        validator.validateId(followerId);
        List<User> users = subscriptionRepository.findByFollowerId(followerId).toList();
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users.stream(), filters));

        return userMapper.toDto(users);
    }

    public int getFollowingCount(long followerId) {
        validator.validateId(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}