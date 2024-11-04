package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

//    @Test
//    void followUserTest() {
//    }
//
//    @Test
//    void followUserThrowsDataValidationExceptionTest() {
//
//    }
}