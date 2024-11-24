package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    private EventDto testEventDto;
    private UserDto testUserDto;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testEventDto = new EventDto();
        testEventDto.setId(10L);

        testUserDto = new UserDto();
        testUserDto.setId(1L);

        testUser = new User();
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Проверка registerParticipant")
    public void testRegisterParticipant() {
        eventParticipationController.registerParticipant(testEventDto, testUserDto);

        verify(eventParticipationService, times(1)).registerParticipant(testEventDto.getId(), testUserDto.getId());
    }

    @Test
    @DisplayName("Проверка unregisterParticipant")
    public void testUnregisterParticipant() {
        eventParticipationController.unregisterParticipant(testEventDto, testUserDto);

        verify(eventParticipationService, times(1)).unregisterParticipant(testEventDto.getId(), testUserDto.getId());
    }

    @Test
    @DisplayName("Проверка getParticipant возвращает список участников")
    public void testGetParticipant() {
        List<User> participants = List.of(testUser);
        when(eventParticipationService.getParticipant(testEventDto.getId())).thenReturn(participants);

        List<User> result = eventParticipationController.getParticipant(testEventDto);

        assertEquals(participants, result);
        verify(eventParticipationService, times(1)).getParticipant(testEventDto.getId());
    }

    @Test
    @DisplayName("Проверка getParticipantsCount возвращает количество участников")
    public void testGetParticipantsCount() {
        int expectedCount = 5;
        when(eventParticipationService.getParticipantsCount(testEventDto.getId())).thenReturn(expectedCount);

        int result = eventParticipationController.getParticipantsCount(testEventDto);

        assertEquals(expectedCount, result);
        verify(eventParticipationService, times(1)).getParticipantsCount(testEventDto.getId());
    }
}

