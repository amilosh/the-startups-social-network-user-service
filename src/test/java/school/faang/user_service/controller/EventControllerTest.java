package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        EventDto expectedEventDto = EventDto.builder().build();

        when(eventService.create(eventDto)).thenReturn(expectedEventDto);

        EventDto result = eventController.create(eventDto);

        assertEquals(expectedEventDto, result);
        assertNotNull(result);

        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void testGetEvent() {
        EventDto eventDto = EventDto.builder().build();

        when(eventService.getEvent(anyLong())).thenReturn(eventDto);

        EventDto result = eventController.getEvent(1L);

        assertEquals(eventDto, result);
        assertNotNull(result);

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
        EventDto eventDto = EventDto.builder().build();
        when(eventService.getOwnedEvents(1L)).thenReturn(List.of(eventDto));

        List<EventDto> result = eventController.getOwnedEvents(1L);

        verify(eventService, times(1)).getOwnedEvents(1L);
        assertEquals(List.of(eventDto), result);
    }

    @Test
    public void testParticipatedEvents() {
        EventDto eventDto = EventDto.builder().build();
        when(eventService.getParticipatedEvents(1L)).thenReturn(List.of(eventDto));

        List<EventDto> result = eventController.getParticipatedEvents(1L);

        verify(eventService, times(1)).getParticipatedEvents(1L);
        assertEquals(List.of(eventDto), result);
    }
}
