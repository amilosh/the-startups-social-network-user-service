package school.faang.user_service.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class EventControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("200 status test and content test")
    void endpointCreateTest() throws Exception {

        Skill skill = skillRepository.save(
                Skill.builder()
                        .title("test title")
                        .users(List.of(User.builder()
                                .id(1L)
                                .build()))
                        .build()
        );

        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .title("test event")
                .description("test event description")
                .location("test location")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .maxAttendees(5)
                .relatedSkills(List.of(skill.getId()))
                .type(EventType.MEETING)
                .status(EventStatus.IN_PROGRESS)
                .build();

        String requestJson = objectMapper.writeValueAsString(eventDto);

        MvcResult result = mockMvc.perform(post("/v1/events")
                        .header("x-user-id", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        EventDto responseEvent = objectMapper.readValue(responseJson, EventDto.class);

        List<Event> events = eventRepository.findAll();
        assertFalse(events.isEmpty(), "The list of events should not be empty");

        assertEquals(eventDto.getOwnerId(), responseEvent.getOwnerId());
        assertEquals(eventDto.getTitle(), responseEvent.getTitle());
        assertEquals(eventDto.getDescription(), responseEvent.getDescription());
        assertEquals(eventDto.getLocation(), responseEvent.getLocation());
        assertEquals(eventDto.getMaxAttendees(), responseEvent.getMaxAttendees());
        assertEquals(eventDto.getType(), responseEvent.getType());
        assertEquals(eventDto.getStatus(), responseEvent.getStatus());
    }
}

