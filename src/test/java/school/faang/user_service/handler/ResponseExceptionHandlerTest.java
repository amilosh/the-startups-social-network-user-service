package school.faang.user_service.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.utilities.UrlServiceParameters;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResponseExceptionHandlerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ExceptionApiHandler responseExceptionHandler;
    private final static long TEST_ID_USER1 = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(responseExceptionHandler).build();
    }

    @Test
    void handleElementNotFindExceptionAddFollowingUsersSelfFailTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(UrlServiceParameters.FOLLOWING_SERVICE_URL +
                        UrlServiceParameters.FOLLOWING_ADD + "followerId=" + TEST_ID_USER1 + "&followeeId=" + TEST_ID_USER1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}