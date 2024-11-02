package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Test
    public void testCreate() {
        EventDto eventDto = EventDto.builder().build();

        when(eventService.create(eventDto)).thenReturn(eventDto);
        eventController.create(eventDto);

        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void testGetEvent() {
        EventDto eventDto = EventDto.builder().build();

        when(eventService.getEvent(anyLong())).thenReturn(eventDto);
        eventController.getEvent(1L);
        verify(eventService, times(1)).getEvent(anyLong());
    }

    @Test
    public void testDeleteEventById() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        when(eventService.updateEvent(eventDto)).thenReturn(eventDto);
        eventService.updateEvent(eventDto);
        verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    public void testOwnedEvents() {
        long userId = 1L;
        eventService.getOwnedEvents(userId);
        verify(eventService, times(1)).getOwnedEvents(userId);
    }

    @Test
    public void testParticipatedEvents() {
        long userId = 1L;
        eventService.getParticipatedEvents(userId);
        verify(eventService, times(1)).getParticipatedEvents(userId);
    }
}
