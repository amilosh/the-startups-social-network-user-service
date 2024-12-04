package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.datatest.DataSubscription;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessages;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Test class for SubscriptionController")
@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;
    private final static long TEST_ID_USER1 = 1L;
    private final static long TEST_ID_USER2 = 2L;

    private final String mainUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.FOLLOWING_SERVICE_URL;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

    @Test
    void followUserSuccess() throws Exception {
        doNothing().when(subscriptionService).followUser(TEST_ID_USER1, TEST_ID_USER2);

        mockMvc.perform(MockMvcRequestBuilders.post(mainUrl +
                        "/followerId/" + TEST_ID_USER1 + "/followeeId/" + TEST_ID_USER2))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void unfollowUserSuccess() throws Exception {
        doNothing().when(subscriptionService).unfollowUser(TEST_ID_USER1, TEST_ID_USER2);
        mockMvc.perform(MockMvcRequestBuilders.delete(mainUrl +
                        "/followerId/" + TEST_ID_USER1 + "/followeeId/" + TEST_ID_USER2))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void followUserFollowingSelfFailTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(TEST_ID_USER1, TEST_ID_USER1),
                "Expected followUser to throw, however it does not happened");
        assertEquals(ErrorMessages.M_FOLLOW_YOURSELF, exception.getMessage(), "SCT001 - the message is different");
        verify(subscriptionService, never()).followUser(TEST_ID_USER1, TEST_ID_USER1);
    }

    @Test
    void unfollowUserFollowingSelfFailTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(TEST_ID_USER1, TEST_ID_USER1),
                "Expected unfollowUser to throw, however it does not happened");
        assertEquals(ErrorMessages.M_UNFOLLOW_YOURSELF, exception.getMessage(), "SCT002 - the message is different");
        verify(subscriptionService, never()).unfollowUser(TEST_ID_USER1, TEST_ID_USER1);
    }

    @Test
    void getFollowersSuccess() throws Exception {
        List<UserDto> dtoList = DataSubscription.getUserDtoList(10);
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoInitValues(2, 2);
        int followeeId = 1;

        when(subscriptionService.getFollowers(followeeId, userFilterDto)).thenReturn(dtoList);
        mockMvc.perform(MockMvcRequestBuilders.
                        post(mainUrl + UrlUtils.FOLLOWING_FILTER + followeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userFilterDto)))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(dtoList.size())))
                .andExpect(status().isOk());
        verify(subscriptionService, times(1)).getFollowers(followeeId, userFilterDto);
    }

    @Test
    void getFollowingCountSuccess() throws Exception {
        int followerId = 1;
        int followerCount = 1;

        when(subscriptionService.getFollowingCount(followerId)).thenReturn(followerCount);
        mockMvc.perform(MockMvcRequestBuilders.get(mainUrl
                        + UrlUtils.FOLLOWING_COUNT
                        + followerId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}