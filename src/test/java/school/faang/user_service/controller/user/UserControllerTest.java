package school.faang.user_service.controller.user;

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
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class UserControllerTest {
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
    void getUsersByIdsSuccessTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1,2,3))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].username", is("JohnDoe")))
                .andExpect(jsonPath("$.[0].email", is("johndoe@example.com")))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].username", is("JaneSmith")))
                .andExpect(jsonPath("$.[1].email", is("janesmith@example.com")))
                .andExpect(jsonPath("$.[2].id", is(3)))
                .andExpect(jsonPath("$.[2].username", is("MichaelJohnson")))
                .andExpect(jsonPath("$.[2].email", is("michaeljohnson@example.com")));
    }

    @Test
    void getUsersByIdsWithNegativeIdFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1,-2,3))))
                .andExpect(status().isBadRequest())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Invalid user ID passed. User ID must not be less than 1"));
                });
    }

    @Test
    void getUserSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS + "/7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.username", is("JamesWilson")))
                .andExpect(jsonPath("$.email", is("jameswilson@example.com")));
    }

    @Test
    void getUserWithNegativeIdFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS + "/-7"))
                .andExpect(status().isBadRequest())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("getUser.id: must be greater than or equal to 1"));
                });
    }

    @Test
    void getUserWithForNonExistentUserFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS + "/55"))
                .andExpect(status().isNotFound())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("User not found by id: 55"));
                });
    }
}
