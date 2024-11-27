package school.faang.user_service.controller.subscription;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController controller;

    @Mock
    private SubscriptionService service;

    private final long followerId = 1L;
    private final long followeeId = 10L;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testFollowUser() throws Exception {
        mockMvc.perform(post("/api/v1/subscriptions/{followerId}/follow/{followeeId}", followerId, followeeId))
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser() throws Exception {
        mockMvc.perform(delete("/api/v1/subscriptions/{followerId}/unfollow/{followeeId}", followerId, followeeId))
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() throws Exception {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("Dima");
        filter.setCityPattern("Moscow");
        List<UserDto> expectedFollowers = List.of(new UserDto(), new UserDto());
        when(service.getFollowers(followeeId, filter)).thenReturn(expectedFollowers);

        MvcResult result = mockMvc.perform(get("/api/v1/subscriptions/{followeeId}/followers", followeeId)
                        .param("namePattern", String.valueOf(filter.getNamePattern()))
                        .param("cityPattern", String.valueOf(filter.getCityPattern())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<UserDto> actualFollowers = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertThat(actualFollowers).isEqualTo(expectedFollowers);
        verify(service).getFollowers(followeeId, filter);
    }

    @Test
    public void testGetFollowing() throws Exception {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("Dima");
        filter.setCityPattern("Moscow");
        List<UserDto> expectedFollowers = List.of(new UserDto(), new UserDto());
        when(service.getFollowing(followerId, filter)).thenReturn(expectedFollowers);

        MvcResult result = mockMvc.perform(get("/api/v1/subscriptions/{followeeId}/following", followerId)
                        .param("namePattern", String.valueOf(filter.getNamePattern()))
                        .param("cityPattern", String.valueOf(filter.getCityPattern())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<UserDto> actualFollowers = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertThat(actualFollowers).isEqualTo(expectedFollowers);
        verify(service).getFollowing(followerId, filter);
    }

    @Test
    public void testGetFollowersCount() throws Exception {
        int followersCount = 10;
        when(service.getFollowersCount(followeeId)).thenReturn(followersCount);
        MvcResult result = getResult(String.format("/%d/followers/count", followeeId), followersCount);

        Assertions.assertEquals(String.valueOf(followersCount), result.getResponse().getContentAsString());
    }

    @Test
    public void testGetFollowingCount() throws Exception {
        int followersCount = 10;
        when(service.getFollowingCount(followerId)).thenReturn(followersCount);
        MvcResult result = getResult(String.format("/%d/following/count", followerId), followersCount);

        Assertions.assertEquals(String.valueOf(followersCount), result.getResponse().getContentAsString());
    }

    private MvcResult getResult(String path, int expectedUserCount) throws Exception {
        return mockMvc.perform(get("/api/v1/subscriptions" + path))
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(expectedUserCount)))
                .andReturn();
    }
}
