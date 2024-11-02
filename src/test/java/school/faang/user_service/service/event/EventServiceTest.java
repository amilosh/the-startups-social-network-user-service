package school.faang.user_service.service.event;

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
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserService userService;

    @Mock
    private EventFilter eventFilter;


    @Mock
    private EventValidation eventValidation;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    public void setUp() {
        Skill skill = new Skill();
        skill.setId(1L);

        eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .ownerId(1L)
                .build();

        User user = new User();
        user.setId(1L);
        user.setSkills(List.of(skill));

        event = new Event();
        event.setId(1L);
        event.setRelatedSkills(List.of(skill));
    }

    @Test
    public void testCreateEventSuccess() {
        when(eventMapper.dtoToEvent(eventDto)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);
        var result = eventService.create(eventDto);
        assertEquals(result, eventDto);
        verify(eventMapper, times(1)).eventToDto(any(Event.class));
        verify(eventMapper, times(1)).dtoToEvent(any(EventDto.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void getEventTest() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventMapper.eventToDto(any(Event.class))).thenReturn(eventDto);
        EventDto result = eventService.getEvent(1L);
        assertEquals(result, eventDto);
        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventMapper, times(1)).eventToDto(any(Event.class));
    }

    @Test
    void testGetEventsByFilter() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Event 1");
        event1.setDescription("Description 1");


        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Event 2");
        event2.setDescription("Description 2");

        Event event3 = new Event();
        event3.setId(3L);
        event3.setTitle("Event 3");
        event3.setDescription("Description 3");


        List<Event> events = List.of(event1, event2, event3);
        when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filter = EventFilterDto.builder()
                .title("Event 2").
                build();

        when(eventFilter.filterEvents(events, filter)).thenReturn(List.of(event2));

        EventDto expectedEventDto = EventDto.builder()
                .id(2L)
                .title("Event2")
                .description("Description 2")
                .build();

        when(eventMapper.eventToDto(event2)).thenReturn(expectedEventDto);

        List<EventDto> result = eventService.getEventsByFilter(filter);

        int expectedSize = 1;
        int resultSize = result.size();
        EventDto resultIndex = result.get(0);

        assertEquals(expectedSize, resultSize);
        assertEquals(expectedEventDto, resultIndex);
    }

    @Test
    public void testGetEventsByFilterNoEventsFound() {
        EventFilterDto filter = EventFilterDto.builder().build();
        filter.setTitle("Non-existent event");

        when(eventRepository.findAll()).thenReturn(List.of());
        when(eventFilter.filterEvents(List.of(), filter)).thenReturn(List.of());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        int expectedSize = 0;
        int resultSize = result.size();

        assertEquals(expectedSize, resultSize);
    }

    @Test
    public void testDeletingEvent() {
        Event event = new Event();
        event.setId(1L);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdatingEvent() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("Updated Event")
                .build();

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setTitle("Old Event");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventMapper.dtoToEvent(eventDto)).thenReturn(existingEvent);
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);
        when(eventMapper.eventToDto(existingEvent)).thenReturn(eventDto);

        EventDto updatedEventDto = eventService.updateEvent(eventDto);

        verify(eventValidation).validateEventDto(eventDto, userService);
        verify(eventMapper).dtoToEvent(eventDto);
        verify(eventRepository).save(existingEvent);
        assertEquals("Updated Event", updatedEventDto.getTitle());
    }

    @Test
    public void getOwnedEventsTest() {
        User user = new User();
        user.setId(1L);

        eventRepository.findAllByUserId(user.getId());
        verify(eventRepository, times(1)).findAllByUserId(user.getId());
    }

    @Test
    public void getParticipatedEventsTest() {
        User user = new User();
        user.setId(1L);

        eventRepository.findParticipatedEventsByUserId(user.getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(user.getId());
    }
}
