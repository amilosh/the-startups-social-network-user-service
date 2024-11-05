package school.faang.user_service.subscribe.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.controllers.subscribe.SubscriptionController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.services.subscribe.SubscriptionService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void followUser_ShouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/subscriptions/follow")
                .param("followerId", "1")
                .param("followeeId", "2"))
            .andExpect(status().isCreated());

        verify(subscriptionService).followUser(1L, 2L);
    }

    @Test
    void unfollowUser_ShouldReturnOkStatus() throws Exception {
        mockMvc.perform(delete("/subscriptions/unfollow")
                .param("followerId", "1")
                .param("followeeId", "2"))
            .andExpect(status().isOk());

        verify(subscriptionService).unfollowUser(1L, 2L);
    }

    @Test
    void getFollowers_ShouldReturnListOfUsers() throws Exception {
        UserFilterDTO filter = new UserFilterDTO();
        UserDTO user = new UserDTO();
        when(subscriptionService.getFollowers(1L, filter))
            .thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/subscriptions/followers")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(subscriptionService).getFollowers(1L, filter);
    }

    @Test
    void getFollowersCount_ShouldReturnCount() throws Exception {
        when(subscriptionService.countFollowers(1L)).thenReturn(10L);

        mockMvc.perform(get("/subscriptions/followers/count")
                .param("userId", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string("10"));

        verify(subscriptionService).countFollowers(1L);
    }

    @Test
    void getFollowing_ShouldReturnListOfUsers() throws Exception {
        UserFilterDTO filter = new UserFilterDTO();
        UserDTO user = new UserDTO();
        when(subscriptionService.getFollowing(1L, filter))
            .thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/subscriptions/following")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(subscriptionService).getFollowing(1L, filter);
    }
}
