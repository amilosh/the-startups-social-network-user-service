package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class EventParticipationServiceTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");
    @Autowired
    private EventParticipationService eventParticipationService;

    static {
        postgres.withDatabaseName("test_db")
                .withUsername("user")
                .withPassword("password");
        postgres.withInitScript("service/event/init.sql");
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void registerParticipantWithoutExceptionTest() {
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(1L,2L));
    }

    @Test
    void registerParticipantWithIllegalArgumentExceptionForEventTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(100L,1L));
        String expectedMessage = "Event with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerParticipantWithIllegalArgumentExceptionForUserTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(3L,100L));
        String expectedMessage = "User with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerParticipantWithIllegalArgumentExceptionForUserAlreadyRegisteredTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(3L,1L));
        String expectedMessage = "User with id: 1 already registered for the event: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void unregisterParticipantWithoutExceptionTest() {
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(5L,7L));
    }

    @Test
    void unregisterParticipantWithIllegalArgumentExceptionForEventTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(100L,1L));
        String expectedMessage = "Event with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void unregisterParticipantWithIllegalArgumentExceptionForUserTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(3L,100L));
        String expectedMessage = "User with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void unregisterParticipantWithIllegalArgumentExceptionForUserIsNotRegisteredTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(3L,2L));
        String expectedMessage = "User with id: 2 is not registered for the event: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getParticipantWithOneElementTest() {
        assertDoesNotThrow(() -> eventParticipationService.getParticipant(3L));
        List<UserDto> participant = eventParticipationService.getParticipant(3L);
        Assertions.assertEquals(1, participant.size());
        Assertions.assertEquals(1, participant.get(0).id());
        Assertions.assertEquals("johndoe@example.com", participant.get(0).email());
        Assertions.assertEquals("JohnDoe", participant.get(0).username());
    }

    @Test
    void getParticipantWithEmptyListTest() {
        assertDoesNotThrow(() -> eventParticipationService.getParticipant(4L));
        List<UserDto> participant = eventParticipationService.getParticipant(4L);
        Assertions.assertEquals(0, participant.size());
        Assertions.assertTrue(participant.isEmpty());
    }

    @Test
    void getParticipantsCountOnEmptyEventTest() {
        assertDoesNotThrow(() -> eventParticipationService.getParticipantsCount(1L));
        Assertions.assertEquals(0, eventParticipationService.getParticipantsCount(1L));
    }

    @Test
    void getParticipantsCountOnEventWithThreeParticipantTest() {
        assertDoesNotThrow(() -> eventParticipationService.getParticipantsCount(2L));
        Assertions.assertEquals(3, eventParticipationService.getParticipantsCount(2L));
    }
}
