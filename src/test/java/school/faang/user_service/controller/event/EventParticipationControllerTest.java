package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import school.faang.user_service.dto.ParticipantReqParam;
import school.faang.user_service.utilities.UrlUtils;

import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class EventParticipationControllerTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3");
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        postgres.withDatabaseName("test_db")
                .withUsername("user")
                .withPassword("password");
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void registerParticipantSuccessTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(2L))))
                .andExpect(status().isOk());
    }

    @Test
    void registerParticipantForNonExistentEventFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/100" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(2L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Event with id: 100 does not exist"));
                });
    }

    @Test
    void registerParticipantForNonExistentUserFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(100L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("User with id: 100 does not exist"));
                });
    }

    @Test
    void registerParticipantWithNullParticipantIdFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(null))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("The given id must not be null"));
                });
    }

    @Test
    void registerParticipantWithNegativeParticipantIdFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(-2L))))
                .andExpect(status().isBadRequest())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must be greater than or equal to 1"));
                });
    }

    @Test
    void unregisterParticipantSuccessTest() throws Exception {
        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/5" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(7L))))
                .andExpect(status().isOk());
    }

    @Test
    void unregisterParticipantForNonExistentEventFailTest() throws Exception {
        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/100" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(2L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Event with id: 100 does not exist"));
                });
    }

    @Test
    void unregisterParticipantForNonExistentUserFailTest() throws Exception {
        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(100L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("User with id: 100 does not exist"));
                });
    }

    @Test
    void unregisterParticipantWithNullParticipantIdFailTest() throws Exception {
        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(null))))
                .andExpect(status().isBadRequest())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("The given id must not be null"));
                });
    }

    @Test
    void unregisterParticipantWithNegativeParticipantIdFailTest() throws Exception {
        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/1" + UrlUtils.PARTICIPANTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParticipantReqParam(-1L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must be greater than or equal to 1"));
                });
    }

    @Test
    void getParticipantForEventWithOneParticipantSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/3" + UrlUtils.PARTICIPANTS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].username", is("JohnDoe")))
                .andExpect(jsonPath("$.[0].email", is("johndoe@example.com")));
    }

    @Test
    void getParticipantsCountForEventWithThreeParticipantSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + "/2" + UrlUtils.PARTICIPANTS + UrlUtils.AMOUNT))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String content = mvcResult.getResponse().getContentAsString();
                    assertEquals("3", content);
                });
    }
}
