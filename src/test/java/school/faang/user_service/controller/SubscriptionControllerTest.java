package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorMessage;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.util.ServiceParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Test class for SubscriptionController")
class SubscriptionControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;
    private final long testIdUser1 = 1L;
    private final long testIdUser2 = 2L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

    @Test
    void followUserExpectedOK() throws Exception {
        doNothing().when(subscriptionService).followUser(testIdUser1, testIdUser2);
        mockMvc.perform(MockMvcRequestBuilders.post(ServiceParameters.FOLLOWING_SERVICE_URL +
                        ServiceParameters.FOLLOWING_ADD +
                        "followerId=" + testIdUser1 + "&followeeId=" + testIdUser2))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void unfollowUserPositive() throws Exception {
        doNothing().when(subscriptionService).unfollowUser(testIdUser1, testIdUser2);
        mockMvc.perform(MockMvcRequestBuilders.post(ServiceParameters.FOLLOWING_SERVICE_URL +
                        ServiceParameters.FOLLOWING_DELETE +
                        "followerId=" + testIdUser1 + "&followeeId=" + testIdUser2))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void followUserShouldThrowDataValidationExceptionWhenFollowingSelf() {
        doNothing().when(subscriptionService).followUser(testIdUser1, testIdUser1);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(testIdUser1, testIdUser1),
                "Expected followUser to throw, however it does not happened");
        assertEquals(ErrorMessage.M_FOLLOW_YOURSELF, exception.getMessage(), "SCT001 - the message is different");
        verify(subscriptionService, never()).followUser(testIdUser1, testIdUser1);
    }

    @Test
    void unfollowUserShouldThrowDataValidationExceptionWhenFollowingSelf() {
        doNothing().when(subscriptionService).unfollowUser(testIdUser1, testIdUser1);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(testIdUser1, testIdUser1),
                "Expected unfollowUser to throw, however it does not happened");
        assertEquals(ErrorMessage.M_UNFOLLOW_YOURSELF, exception.getMessage(), "SCT002 - the message is different");
        verify(subscriptionService, never()).unfollowUser(testIdUser1, testIdUser1);
    }
}