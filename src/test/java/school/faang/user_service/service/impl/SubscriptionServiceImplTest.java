package school.faang.user_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.datatest.DataSubscription;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorMessages;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserFilter userFilter;
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private final static long TEST_ID_USER1 = 1L;
    private final static long TEST_ID_USER2 = 2L;
    private final static long FOLLOWEE_ID = 1L;
    private final static int PAGE_SIZE = 2;
    private final static int PAGE_NUMBER = 2;
    private final static int NUMBER_USER_SUCCESS_FILTER = 7;
    private final static int NUMBER_USER_NOT_SUCCESS_FILTER = 5;
    private final static int FOLLOWER_COUNT = 1;

    @Test
    void followUserSuccessTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(TEST_ID_USER1, TEST_ID_USER2)).thenReturn(false);
        subscriptionService.followUser(TEST_ID_USER1, TEST_ID_USER2);
        verify(subscriptionRepository, times(1)).followUser(TEST_ID_USER1, TEST_ID_USER2);
    }

    @Test
    void followUserExistExceptionFailTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(TEST_ID_USER1, TEST_ID_USER2)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(TEST_ID_USER1, TEST_ID_USER2));
        assertEquals(exception.getMessage(), ErrorMessages.M_FOLLOW_EXIST);
        verify(subscriptionRepository, never()).followUser(TEST_ID_USER1, TEST_ID_USER2);
    }

    @Test
    void unfollowUserSuccessTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(TEST_ID_USER1, TEST_ID_USER2)).thenReturn(true);
        subscriptionService.unfollowUser(TEST_ID_USER1, TEST_ID_USER2);
        verify(subscriptionRepository, times(1)).unfollowUser(TEST_ID_USER1, TEST_ID_USER2);
    }

    @Test
    void unfollowDoesNotExistsExceptionFailTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(TEST_ID_USER1, TEST_ID_USER2)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(TEST_ID_USER1, TEST_ID_USER2));
        assertEquals(exception.getMessage(), ErrorMessages.M_FOLLOW_DOES_NOT_EXIST);
        verify(subscriptionRepository, never()).unfollowUser(TEST_ID_USER1, TEST_ID_USER2);
    }

    @Test
    void getFollowersSetAllFilterValueSuccessTest() {
        generalGetFollowersFilterCalculation(
                PAGE_NUMBER,
                PAGE_SIZE,
                PAGE_SIZE,
                "EE001 - Incorrect number rows in a page.",
                false);
    }

    @Test
    void getFollowersSetNullPageSizeSuccessTest() {
        generalGetFollowersFilterCalculation(
                PAGE_NUMBER,
                null,
                NUMBER_USER_SUCCESS_FILTER + NUMBER_USER_NOT_SUCCESS_FILTER,
                "EE002 - Incorrect number rows.",
                false);
    }

    @Test
    void getFollowersSetNullPageSuccessTest() {
        generalGetFollowersFilterCalculation(
                null,
                PAGE_SIZE,
                NUMBER_USER_SUCCESS_FILTER + NUMBER_USER_NOT_SUCCESS_FILTER,
                "EE003 - Incorrect number rows.",
                false);
    }

    @Test
    void getFollowersSetNullPageAndPageSizeSuccessTest() {
        generalGetFollowersFilterCalculation(
                null,
                null,
                NUMBER_USER_SUCCESS_FILTER + NUMBER_USER_NOT_SUCCESS_FILTER,
                "EE004 - Incorrect number rows.",
                false);
    }

    @Test
    void getFollowersLogicFilterTest() {
        generalGetFollowersFilterCalculation(
                null,
                null,
                NUMBER_USER_SUCCESS_FILTER,
                "EE005 -A logic of general filter calculation is wrong.",
                true);
    }

    @Test
    void getFollowingCountSuccessTest() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(FOLLOWEE_ID)).thenReturn(FOLLOWER_COUNT);

        assertEquals(FOLLOWER_COUNT, subscriptionService.getFollowingCount(FOLLOWEE_ID), "EFC001 - Wrong counting");
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(FOLLOWEE_ID);
    }

    private void generalGetFollowersFilterCalculation(
            Integer pageNumber,
            Integer pageSize,
            Integer resultNumberRows,
            String errorMessage,
            boolean isLogicFilterPresent) {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoInitValues(pageNumber, pageSize);

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(DataSubscription.getUserList(NUMBER_USER_SUCCESS_FILTER, NUMBER_USER_NOT_SUCCESS_FILTER).stream());
        if (isLogicFilterPresent) {
            subscriptionService = new SubscriptionServiceImpl(subscriptionRepository,
                    userMapper,
                    DataSubscription.getListUserFilters());
        } else {
            when(userFilter.apply(any(User.class), any(UserFilterDto.class))).thenReturn(true);
            subscriptionService = new SubscriptionServiceImpl(subscriptionRepository,
                    userMapper,
                    List.of(userFilter));
        }

        log.info(userFilterDto.toString());

        List<UserDto> resultUserDTos = subscriptionService.getFollowers(FOLLOWEE_ID, userFilterDto);

        log.info("" + resultUserDTos.size());

        verify(subscriptionRepository, times(1)).findByFolloweeId(FOLLOWEE_ID);
        assertEquals(resultNumberRows, resultUserDTos.size(), errorMessage);
    }
}