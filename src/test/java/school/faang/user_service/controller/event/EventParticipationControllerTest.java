package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.utilities.UrlUtils;

import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventParticipationControllerTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");
    @Autowired
    private MockMvc mockMvc;

    static {
        postgres.withDatabaseName("test_db")
                .withUsername("user")
                .withPassword("password");
        postgres.withInitScript("event/init.sql");
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void registerParticipantStatusOkTest() throws Exception {
        mockMvc.perform(put(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.REGISTER)
                        .param("userId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void registerParticipantStatus400Test() throws Exception {
        mockMvc.perform(put(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/100" + UrlUtils.REGISTER)
                        .param("userId", "2"))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Event with id: 100 does not exist"));
                });
    }

    @Test
    void unregisterParticipantStatusOkTest() throws Exception {
        mockMvc.perform(put(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/5" + UrlUtils.UNREGISTER)
                        .param("userId", "7"))
                .andExpect(status().isOk());
    }

    @Test
    void unregisterParticipantStatus400Test() throws Exception {
        mockMvc.perform(put(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/100" + UrlUtils.UNREGISTER)
                        .param("userId", "2"))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Event with id: 100 does not exist"));
                });
    }

    @Test
    void getParticipantWithOneElementTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/3" + UrlUtils.PARTICIPANTS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].username", is("JohnDoe")))
                .andExpect(jsonPath("$.[0].email", is("johndoe@example.com")));
    }

    @Test
    void getParticipantsCountOnEventWithThreeParticipantTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/2" + UrlUtils.PARTICIPANTS + UrlUtils.AMOUNT))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String content = mvcResult.getResponse().getContentAsString();
                    assertEquals("3", content);
                });
    }
}
