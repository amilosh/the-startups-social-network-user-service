package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventDtoValidator validator;
    @Mock
    private EventTitleFilter titleFilter;

    private final Long testId = 1L;
    private final EventDto inputDto = EventDto.builder().id(testId).build();
    private final EventDto outputDto = EventDto.builder().id(testId + 1).build();
    private final List<EventDto> eventDtos = List.of(outputDto);
    private final Event event = Event.builder().build();
    private final List<Event> events = List.of(event);
    private final Event savedEvent = Event.builder().id(testId).build();
    private final User user = User.builder().build();

    @Test
    public void testCreate() {
        when(eventMapper.toEntity(inputDto)).thenReturn(event);
        when(userService.findById(inputDto.getOwnerId())).thenReturn(user);
        when(eventRepository.save(event)).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(outputDto);

        EventDto result = eventService.create(inputDto);
        Assertions.assertSame(result, outputDto);
    }

    @Test
    public void testInvalidEventIdGet() {
        when(eventRepository.findById(testId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventService.getEvent(testId)
        );
    }

    @Test
    public void testValidEventIdGet() {
        when(eventRepository.findById(testId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(outputDto);

        EventDto result = eventService.getEvent(testId);
        Assertions.assertSame(result, outputDto);
    }

    @Test
    public void testGetEventsByFilters() {
        List<EventFilter> eventFilters = List.of(titleFilter);
        EventService service = new EventService(eventRepository, userService, eventMapper, validator, eventFilters);
        EventFilterDto filters = Mockito.mock(EventFilterDto.class);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.toListDto(events)).thenReturn(eventDtos);

        List<EventDto> eventsByFilters = service.getEventsByFilters(filters);
        Assertions.assertSame(eventsByFilters, eventDtos);
    }

    @Test
    public void testInvalidEventIdDelete() {
        when(eventRepository.existsById(testId)).thenReturn(false);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventService.deleteEvent(testId)
        );
    }

    @Test
    public void testValidEventIdDelete() {
        when(eventRepository.existsById(testId)).thenReturn(true);
        eventService.deleteEvent(testId);
        verify(eventRepository, times(1)).deleteById(testId);
    }

    @Test
    public void testInvalidEventDtoUpdate() {
        when(eventRepository.findById(inputDto.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(inputDto)
        );
    }

    @Test
    public void testValidEventDtoUpdate() {
        when(eventRepository.findById(inputDto.getId())).thenReturn(Optional.of(event));
        when(eventMapper.toEntity(inputDto)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(outputDto);
        EventDto result = eventService.updateEvent(inputDto);

        Assertions.assertSame(result, outputDto);
    }

    @Test
    public void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(testId)).thenReturn(events);
        when(eventMapper.toListDto(events)).thenReturn(eventDtos);
        List<EventDto> result = eventService.getOwnedEvents(testId);

        Assertions.assertSame(result, eventDtos);
    }

    @Test
    public void testParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(testId)).thenReturn(events);
        when(eventMapper.toListDto(events)).thenReturn(eventDtos);
        List<EventDto> result = eventService.getParticipatedEvents(testId);

        Assertions.assertSame(result, eventDtos);
    }
}