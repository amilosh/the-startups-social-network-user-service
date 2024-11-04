package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionService subscriptionService;


    @BeforeEach
    public void initUsers() {}

    @Test
    void followUserTest() throws DataValidationException {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);

        subscriptionService.followUser(Mockito.anyLong(), Mockito.anyLong());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void followUserThrowsDataValidationExceptionTest() throws DataValidationException {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(Mockito.anyLong(), Mockito.anyLong()));


        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

}