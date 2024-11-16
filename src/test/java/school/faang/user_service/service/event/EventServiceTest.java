package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventIdFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.EventValidation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventValidation eventValidation;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private UserService userService;
    @Mock
    private List<EventFilter> eventFilters;
    @Mock
    private EventIdFilter eventIdFilter;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    public void setUp() {
        eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .ownerId(1L)
                .relatedSkills(new ArrayList<>())
                .build();

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        Skill skill = new Skill();
        skill.setId(1L);
    }

    @Test
    public void testCreateEvent() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .ownerId(1L)
                .build();

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        when(eventMapper.dtoToEvent(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);

        EventDto createdEvent = eventService.create(eventDto);

        assertNotNull(createdEvent);
        assertEquals(eventDto.getId(), createdEvent.getId());
        verify(eventValidation, times(1)).validateEvent(eventDto);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testFindEventById() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);

        EventDto foundEvent = eventService.getEventDto(event.getId());

        assertNotNull(foundEvent);
        assertEquals(event.getId(), foundEvent.getId());
    }

    @Test
    public void testGetEventThrowingEntityNotFoundException() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventDto(event.getId()));
    }

    @Test
    public void testUpdateEvent() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        User user = new User();
        user.setSkills(new ArrayList<>());

        when(userService.findUser(eventDto.getOwnerId())).thenReturn(user);
        when(eventMapper.dtoToEventWithId(eventDto, event.getId())).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);

        EventDto updatedEvent = eventService.updateEvent(eventDto);

        assertNotNull(updatedEvent);
        assertEquals(event.getId(), updatedEvent.getId());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testDeleteEventById() {
        long eventId = 1L;
        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testGetOwnedEvents() {
        long userId = 1L;
        Event event = new Event();
        event.setId(1L);

        when(eventRepository.findAllByUserId(userId)).thenReturn(List.of(event));
        when(eventMapper.toDtoList(List.of(event))).thenReturn(List.of(eventDto));

        List<EventDto> ownedEvents = eventService.getOwnedEvents(userId);

        assertNotNull(ownedEvents);
        assertEquals(1, ownedEvents.size());
    }

    @Test
    public void testGetParticipatedEvents() {
        List<Event> events = Collections.singletonList(event);

        when(eventRepository.findParticipatedEventsByUserId(eventDto.getOwnerId())).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(Collections.singletonList(eventDto));

        List<EventDto> participatedEvents = eventService.getParticipatedEvents(eventDto.getOwnerId());

        assertNotNull(participatedEvents);
        assertEquals(1, participatedEvents.size());
    }

    @Test
    void checkEventExistenceReturnFalse() {
        long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        boolean result = eventService.checkEventExistence(eventId);

        assertFalse(result);
    }

    @Test
    void checkEventExistenceReturnTrue() {
        long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        boolean result = eventService.checkEventExistence(eventId);

        assertTrue(result);
    }

    @Test
    public void testCheckEventExistence() {
        long id = 1L;
        when(eventRepository.existsById(id)).thenReturn(true);
        eventService.checkEventExistence(id);
        verify(eventRepository, times(1)).existsById(id);
    }

    @Test
    void testGetEventsByFilterShouldReturnFilteredList() {
        EventFilterDto filterDto = EventFilterDto.builder().id(1L).build();
        eventFilters = List.of(eventIdFilter);
        eventService = new EventService(eventValidation, eventRepository, eventMapper, userService, eventFilters);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventIdFilter.isApplicable(filterDto)).thenReturn(true);
        when(eventIdFilter.apply(any(), eq(filterDto))).thenReturn(Stream.of(event));
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getEventsByFilter(filterDto);

        verify(eventIdFilter, times(1)).isApplicable(any(EventFilterDto.class));
        verify(eventIdFilter, times(1)).apply(any(), any(EventFilterDto.class));
        verify(eventMapper, times(1)).eventToDto(any(Event.class));
        verify(eventRepository, times(1)).findAll();
        assertTrue(result.contains(eventDto));
    }
}
