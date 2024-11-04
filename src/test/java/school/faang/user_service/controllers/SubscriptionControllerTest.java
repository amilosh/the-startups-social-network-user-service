package school.faang.user_service.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.services.SubscriptionService;

@AutoConfigureMockMvc
public class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

    @Test
    public void testFollowUser() throws Exception {
        mockMvc.perform(post("/subscriptions/follow")
                .param("followerId", "1")
                .param("followeeId", "2"))
            .andExpect(status().isCreated());

        verify(subscriptionService, times(1)).followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUser() throws Exception {
        mockMvc.perform(delete("/subscriptions/unfollow")
                .param("followerId", "1")
                .param("followeeId", "2"))
            .andExpect(status().isOk());

        verify(subscriptionService, times(1)).unfollowUser(1L, 2L);
    }
}
