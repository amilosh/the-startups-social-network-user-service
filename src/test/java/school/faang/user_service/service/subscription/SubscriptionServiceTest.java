package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final long followerId = 4L;
    private final long followeeId = 3L;


    @Test
    public void testfollowUserSuccess() {
        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionValidator, times(1))
                .validateUserIsTryingToCallHimself(followerId, followeeId, "User 4 trying to subscribe to himself");
        verify(subscriptionValidator, times(1))
                .validateUserAlreadyHasThisSubscription(followerId, followeeId);
        verify(subscriptionRepository, times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserSuccess() {
        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionValidator, times(1))
                .validateUserIsTryingToCallHimself(followerId, followeeId, "User 4 trying to unfollow himself");
        verify(subscriptionRepository, times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        subscriptionService.getFollowers(followeeId, new UserFilterDto("Ivan", null, "kenobiObiVan123@gmail.com"));

        verify(subscriptionRepository, times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }
}
