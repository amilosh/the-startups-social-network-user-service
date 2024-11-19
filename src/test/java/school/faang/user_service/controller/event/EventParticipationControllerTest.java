package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    private MockMvc mockMvc;
    private long eventId;
    private long userId;
    private List<UserDto> participants;
    private int participantsAmount;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventParticipationController).build();

        eventId = 1L;
        userId = 2L;
        participants = List.of(
                UserDto.builder()
                        .id(1L)
                        .username("John")
                        .email("john@example.com")
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .username("Jane")
                        .email("jane@example.com")
                        .build()
        );

        participantsAmount = participants.size();
    }

    @Test
    void testFindAllParticipantsByEventId() throws Exception {
        when(eventParticipationService.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        mockMvc.perform(get("/event-participation/participants-list/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(participants.get(0).getId()))
                .andExpect(jsonPath("$[0].username").value(participants.get(0).getUsername()))
                .andExpect(jsonPath("$[0].email").value(participants.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(participants.get(1).getId()))
                .andExpect(jsonPath("$[1].username").value(participants.get(1).getUsername()))
                .andExpect(jsonPath("$[1].email").value(participants.get(1).getEmail()));

        verify(eventParticipationService, times(1)).findAllParticipantsByEventId(eventId);
    }

    @Test
    void testFindParticipantsAmountByEventId() throws Exception {
        when(eventParticipationService.findParticipantsAmountByEventId(eventId)).thenReturn(participantsAmount);

        mockMvc.perform(get("/event-participation/participants-number/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(participantsAmount));

        verify(eventParticipationService, times(1)).findParticipantsAmountByEventId(eventId);
    }

    @Test
    void testRegisterParticipant() throws Exception {
        mockMvc.perform(put("/event-participation/register/{eventId}/{userId}", eventId, userId))
                .andExpect(status().isOk());

        verify(eventParticipationService, times(1)).registerParticipant(eventId, userId);
    }

    @Test
    void testUnregisterParticipant() throws Exception {
        mockMvc.perform(put("/event-participation/unregister/{eventId}/{userId}", eventId, userId))
                .andExpect(status().isOk());

        verify(eventParticipationService, times(1)).unregisterParticipant(eventId, userId);
    }
}