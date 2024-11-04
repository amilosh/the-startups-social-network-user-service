package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.subscription.SubscriptionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @MockBean
    SubscriptionService subscriptionService;

    @Autowired
    private MockMvc mockMvc;

    // This bean cannot be found in app context, it makes an error, so I mock it
    // must be fixed
    @MockBean
    UserContext userContext;

    @Test
    void followUserTest() throws Exception {
        Mockito.doNothing().when(subscriptionService).followUser(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(post("/subscriptions/follow")
                        .param("followerId", "1")
                        .param("followeeId", "2"))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void followUserThrowsDataValidationExceptionTest() throws Exception {
        mockMvc.perform(post("/subscriptions/follow")
                    .param("followerId", "1")
                    .param("followeeId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You cannot follow yourself"));

        Mockito.verify(subscriptionService, Mockito.times(0))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }
}