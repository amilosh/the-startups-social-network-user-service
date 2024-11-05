package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.util.ServiceParameters;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResponseExceptionHandlerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ResponseExceptionHandler responseExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(responseExceptionHandler).build();
    }

    @Test
    void handleElementNotFindExceptionAddFollowingUsersSelf() throws Exception {
        long testIdUser1 = 1L;
        mockMvc.perform(MockMvcRequestBuilders.post(ServiceParameters.FOLLOWING_SERVICE_URL +
                        ServiceParameters.FOLLOWING_ADD + "followerId=" + testIdUser1 + "&followeeId=" + testIdUser1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}