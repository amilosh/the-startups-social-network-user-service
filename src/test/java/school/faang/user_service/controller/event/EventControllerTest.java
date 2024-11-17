package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController controller;
    @Mock
    private EventService service;

    private final EventDto inputEventDto = new EventDto();
    private final EventDto outputEventDto = new EventDto();
    private final List<EventDto> listOutputEventDto = List.of(new EventDto());
    private final EventDto eventDto = new EventDto();
    private final Long testId = 1L;

    @Test
    public void testCreateMethod() {
        when(service.create(inputEventDto)).thenReturn(outputEventDto);
        EventDto response = controller.create(inputEventDto);
        verify(service, times(1)).create(inputEventDto);
        Assertions.assertSame(response, outputEventDto);
    }

    @Test
    public void testGetEventMethod() {
        when(service.getEvent(testId)).thenReturn(outputEventDto);
        EventDto response = controller.getEvent(testId);
        verify(service, times(1)).getEvent(testId);
        Assertions.assertSame(response, outputEventDto);
    }

    @Test
    public void testGetEventsByFilters() {
        EventFilterDto filters = new EventFilterDto();
        when(service.getEventsByFilters(filters)).thenReturn(listOutputEventDto);
        List<EventDto> response = controller.getEventsByFilters(filters);
        verify(service, times(1)).getEventsByFilters(filters);
        Assertions.assertSame(response, listOutputEventDto);
    }

    @Test
    public void testDeleteEvent() {
        controller.deleteEvent(testId);
        verify(service, times(1)).deleteEvent(testId);
    }

    @Test
    public void testUpdateEvent() {
        when(service.updateEvent(inputEventDto)).thenReturn(outputEventDto);
        EventDto response = controller.updateEvent(eventDto);
        verify(service, times(1)).updateEvent(eventDto);
        Assertions.assertSame(response, outputEventDto);
    }

    @Test
    public void testGetOwnedEvents() {
        when(service.getOwnedEvents(testId)).thenReturn(listOutputEventDto);
        List<EventDto> response = controller.getOwnedEvents(testId);
        verify(service, times(1)).getOwnedEvents(testId);
        Assertions.assertSame(response, listOutputEventDto);
    }

    @Test
    public void testGetParticipatedEvents() {
        when(service.getParticipatedEvents(testId)).thenReturn(listOutputEventDto);
        List<EventDto> response = controller.getParticipatedEvents(testId);
        verify(service, times(1)).getParticipatedEvents(testId);
        Assertions.assertSame(response, listOutputEventDto);
    }
}