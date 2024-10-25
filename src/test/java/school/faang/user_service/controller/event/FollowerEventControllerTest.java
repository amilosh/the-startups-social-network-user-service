package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.model.event.FollowerEvent;
import school.faang.user_service.publisher.FollowerEventPublisher;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FollowerEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowerEventPublisher followerEventPublisher;

    @Test
    void followUser_ShouldReturnSuccessMessage_WhenEventIsPublished() throws Exception {
        Long followerId = 1L;
        Long followedUserId = 2L;

        mockMvc.perform(post("/subscriptions/user/{followerId}/follow/{followedUserId}", followerId, followedUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully followed the user."));

        ArgumentCaptor<FollowerEvent> eventCaptor = ArgumentCaptor.forClass(FollowerEvent.class);
        verify(followerEventPublisher).publish(eventCaptor.capture());

        FollowerEvent publishedEvent = eventCaptor.getValue();
        assert publishedEvent.getFollowerId().equals(followerId);
        assert publishedEvent.getFollowedUserId().equals(followedUserId);
    }

    @Test
    void followUser_ShouldReturnErrorMessage_WhenPublishingFails() throws Exception {
        Long followerId = 1L;
        Long followedUserId = 2L;

        doThrow(new RuntimeException("Publishing error")).when(followerEventPublisher).publish(any(FollowerEvent.class));

        mockMvc.perform(post("/subscriptions/user/{followerId}/follow/{followedUserId}", followerId, followedUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to follow the user: Publishing error"));
    }

    @Test
    void followProject_ShouldReturnSuccessMessage_WhenEventIsPublished() throws Exception {
        Long followerId = 1L;
        Long followedProjectId = 3L;

        mockMvc.perform(post("/subscriptions/user/{followerId}/follow-project/{followedProjectId}", followerId, followedProjectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully followed the project."));

        ArgumentCaptor<FollowerEvent> eventCaptor = ArgumentCaptor.forClass(FollowerEvent.class);
        verify(followerEventPublisher).publish(eventCaptor.capture());

        FollowerEvent publishedEvent = eventCaptor.getValue();
        assert publishedEvent.getFollowerId().equals(followerId);
        assert publishedEvent.getFollowedProjectId().equals(followedProjectId);
    }

    @Test
    void followProject_ShouldReturnErrorMessage_WhenPublishingFails() throws Exception {
        Long followerId = 1L;
        Long followedProjectId = 3L;

        doThrow(new RuntimeException("Publishing error")).when(followerEventPublisher).publish(any(FollowerEvent.class));

        mockMvc.perform(post("/subscriptions/user/{followerId}/follow-project/{followedProjectId}", followerId, followedProjectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to follow the project: Publishing error"));
    }
}