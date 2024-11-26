package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.EventValidation;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
    private UserValidator userValidator;
    @Mock
    private EventCleanerService eventCleanerService;

    private EventDto eventDto;
    private Event event;
    private final long userId = 1L;

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

        when(userService.findUserById(eventDto.getOwnerId())).thenReturn(user);
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
    public void testGetEvents_InvalidUserId() {
        doThrow(new EntityNotFoundException("User with id #" + userId + " not found"))
                .when(userValidator).validateUserById(userId);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> eventService.getEvents(userId));

        assertEquals("User with id #" + userId + " not found", exception.getMessage());
        verify(userValidator).validateUserById(userId);
        verify(eventRepository, never()).findAllByUserId(userId);
    }

    @Test
    public void testGetEvents_ValidUserId() {
        List<Event> expectedEvents = Arrays.asList(event, new Event());
        when(eventRepository.findAllByUserId(userId)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.getEvents(userId);

        assertEquals(expectedEvents, actualEvents);
        verify(userValidator).validateUserById(userId);
        verify(eventRepository).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Test deleteCompletedAndCanceledEvent positive case")
    void testDeleteCompletedAndCanceledEvent_ShouldSplitIntoBatchesAndCallDeleteAsync() {
        List<Event> events = List.of(
                Event.builder().id(1L).build(),
                Event.builder().id(2L).build(),
                Event.builder().id(3L).build(),
                Event.builder().id(4L).build(),
                Event.builder().id(5L).build()
        );
        int batchSize = 2;
        ReflectionTestUtils.setField(eventService, "batchSize", batchSize);
        when(eventRepository.findAllCompletedAndCanceledEvents()).thenReturn(events);
        doNothing().when(eventCleanerService).deleteSelectedListEventsAsync(anyList());

        eventService.deleteCompletedAndCanceledEvent();

        verify(eventRepository).findAllCompletedAndCanceledEvents();
        verify(eventCleanerService, times(3)).deleteSelectedListEventsAsync(anyList());
    }

    @Test
    @DisplayName("Test deleteCompletedAndCanceledEvent negative case")
    void testDeleteCompletedAndCanceledEventEmptyList() {
        when(eventRepository.findAllCompletedAndCanceledEvents()).thenReturn(Collections.emptyList());
        int batchSize = 2;
        ReflectionTestUtils.setField(eventService, "batchSize", batchSize);

        eventService.deleteCompletedAndCanceledEvent();

        verify(eventRepository).findAllCompletedAndCanceledEvents();
        verify(eventRepository, never()).deleteById(anyLong());
    }
}
