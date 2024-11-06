package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import springfox.documentation.schema.ModelKey;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Mock
    UserMapper mapper;

    @InjectMocks
    SubscriptionService subscriptionService;


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
    void followUserThrowsDataValidationExceptionTest() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);

        String exceptionMessage = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(Mockito.anyLong(), Mockito.anyLong()))
                .getMessage();

        assertEquals("The subscription already exists", exceptionMessage);


        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void unfollowUserTest() throws DataValidationException {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);

        subscriptionService.unfollowUser(Mockito.anyLong(), Mockito.anyLong());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void unfollowUserThrowsDataValidationExceptionTest() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);

        String exceptionMessage = assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(Mockito.anyLong(), Mockito.anyLong())).getMessage();

        assertEquals("The subscription doesn't exist already", exceptionMessage);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());

    }


    @Test
    void getFollowersTest() {
        // Test users
        User alexander = new User();
        User alexey = new User();
        User petr = new User();

        alexander.setUsername("Alexander");
        alexey.setUsername("alexey");
        petr.setUsername("Petr");

        Stream<User> userStream = Stream.of(
                alexander,
                alexey,
                petr
        );

        Long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("ale");

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(userStream);

        UserDto alexanderDto = new UserDto();
        UserDto alexeyDto = new UserDto();
        UserDto petrDto = new UserDto();

        alexanderDto.setUsername(alexander.getUsername());
        alexeyDto.setUsername(alexey.getUsername());
        petrDto.setUsername(petr.getUsername());


        List<UserDto> expectedUserDtoList = List.of(
                alexanderDto,
                alexeyDto
        );

        Mockito.when(mapper.toDto(alexander)).thenReturn(alexanderDto);
        Mockito.when(mapper.toDto(alexey)).thenReturn(alexeyDto);
//        кидает ошибку и пишет что unnecessary stubbing, но откуда я знаю что он не пройдет? Скорее это "фича" мокито, тут ничего не сделаешь
//        Mockito.when(mapper.toDto(petr)).thenReturn(petrDto);

        // must return Alexander and Alexey ignoring case
        List<UserDto> actualUserDtoList = subscriptionService.getFollowers(followeeId, userFilterDto);

        assertEquals(expectedUserDtoList, actualUserDtoList);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowersThrowsNoSuchElementException() {
        Long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(false);

        Exception exception = assertThrows(NoSuchElementException.class,
                () -> subscriptionService.getFollowers(followeeId, userFilterDto));

        assertEquals("Cannot find followee by id " + followeeId, exception.getMessage());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findByFolloweeId(followeeId);
    }
}