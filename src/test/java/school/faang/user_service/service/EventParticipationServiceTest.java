package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService service;

    @Mock
    private EventParticipationRepository repository;

    @Mock
    private UserMapper mapper;

    private final long eventId = 1L;
    private final long userId = 100L;

    @Test
    public void registerParticipantWithRegisteredUserTest() {
        prepareData();
        assertThrows(IllegalStateException.class, () -> service.registerParticipant(eventId, userId));
    }

    @Test
    public void registerParticipantTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());
        service.registerParticipant(eventId, userId);
        verify(repository, times(1)).register(eventId, userId);
    }

    @Test
    void unregisterParticipantIsParticipantRegisteredTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(new ArrayList<>());
        assertThrows(IllegalStateException.class, () -> service.unregisterParticipant(eventId, userId));
    }

    @Test
    void unregisterParticipantTest() {
        prepareData();
        service.unregisterParticipant(eventId, userId);
        verify(repository, times(1)).unregister(eventId, userId);
    }

    @Test
    void emptyEventTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> service.getParticipant(eventId));
    }

    @Test
    void getParticipantsTest() {
        User mockUser = mock(User.class);
        UserDto mockUserDto = mock(UserDto.class);
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(mockUser));
        when(mapper.toDto(mockUser)).thenReturn(mockUserDto);
        assertEquals(List.of(mockUserDto), service.getParticipant(eventId));
    }

    @Test
    void getParticipantsCountTest() {
        when(repository.countParticipants(eventId)).thenReturn(1);
        assertEquals(1, service.getParticipantsCount(eventId));
        verify(repository, times(1)).countParticipants(eventId);
    }

    private void prepareData() {
        User user = new User();
        user.setId(userId);
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
    }
}