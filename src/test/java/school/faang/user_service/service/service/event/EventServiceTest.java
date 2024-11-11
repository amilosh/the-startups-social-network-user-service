package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataNotMatchException;
import school.faang.user_service.exception.EntityNotFoundExceptionWithID;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.filters.event.EventTitleFilter;
import school.faang.user_service.filters.event.EventUserIdFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapperImpl eventMapper;

    @Mock
    private SkillRepository skillRepository;

    private List<EventFilter> eventFilters;

    @BeforeEach
    void setUp() {
        this.eventFilters = Arrays.asList(
                new EventTitleFilter(),
                new EventUserIdFilter()
        );
        eventService.setEventFilters(eventFilters);
    }

    @Test
    public void testCreateEventWithDontHaveNeededSkills() {
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(new SkillDto(1L, "programming")))
                .build();
        when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(
                        List.of(Skill.builder()
                                .title("speak in english")
                                .build())
                );

        assertThrows(DataNotMatchException.class, () -> eventService.create(eventDto));
        verify(skillRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void testCreateEventWithCorrectRelatedSkills() {
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(
                        new SkillDto(1L, "programming"),
                        new SkillDto(2L, "Spring")))
                .build();
        when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(List.of(
                        Skill.builder().title("Programming").build(),
                        Skill.builder().title("spring").build(),
                        Skill.builder().title("docker").build()
                ));
        Event savedEvent = eventMapper.toEntity(eventDto);
        when(eventRepository.save(savedEvent))
                .thenReturn(savedEvent);

        EventDto returnedEventDto = eventService.create(eventDto).getBody();

        verify(eventMapper, times(2)).toEntity(eventDto);
        verify(eventRepository, times(1)).save(savedEvent);
        assertNotNull(returnedEventDto);
    }

    @Test
    public void testToGetEventByIdWithCorrectEventId() {
        Event event = Event.builder()
                .id(1L)
                .build();
        Optional<Event> optionalEvent = Optional.of(event);

        when(eventRepository.findById(event.getId())).thenReturn(optionalEvent);

        EventDto eventDto = eventService.getEvent(1L).getBody();

        verify(eventRepository, times(1)).findById(1L);
        verify(eventMapper, times(1)).toDto(event);
        assertNotNull(eventDto);
    }

    @Test
    public void testToGetEventByIdWithWrongEventId() {
        Event event = Event.builder()
                .id(1L)
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundExceptionWithID.class, () -> eventService.getEvent(1L));
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    public void testToDeleteEventByCorrectEventId() {
        Event event = Event.builder()
                .id(1L)
                .build();
        Optional<Event> optionalEvent = Optional.of(event);

        when(eventRepository.findById(event.getId())).thenReturn(optionalEvent);

        EventDto eventDto = eventService.deleteEvent(1L).getBody();

        verify(eventRepository, times(1)).findById(1L);
        verify(eventMapper, times(1)).toDto(event);
        assertNotNull(eventDto);
    }

    @Test
    public void testToDeleteEventByWrongEventId() {
        Event event = Event.builder()
                .id(2L)
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundExceptionWithID.class, () -> eventService.deleteEvent(2L));
        verify(eventRepository, times(1)).findById(2L);
        verify(eventRepository, times(0)).deleteById(2L);
    }

    @Test
    public void testToUpdateEventByCorrectEventIdAndRelatedSkills() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .relatedSkills(List.of(
                        new SkillDto(1L, "programming"),
                        new SkillDto(2L, "Spring")))
                .build();
        when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(List.of(
                        Skill.builder().title("Programming").build(),
                        Skill.builder().title("spring").build(),
                        Skill.builder().title("docker").build()
                ));
        when(eventRepository.existsById(eventDto.getId())).thenReturn(true);
        Event savedEvent = eventMapper.toEntity(eventDto);
        when(eventRepository.save(savedEvent))
                .thenReturn(savedEvent);

        EventDto returnedEventDto = eventService.updateEvent(eventDto).getBody();

        verify(eventMapper, times(2)).toEntity(eventDto);
        verify(eventRepository, times(1)).save(savedEvent);
        assertNotNull(returnedEventDto);
    }

    @Test
    public void testToUpdateEventByCorrectEventIdAndWrongRelatedSkills() {
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(new SkillDto(1L, "programming")))
                .build();
        when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(
                        List.of(Skill.builder()
                                .title("speak in english")
                                .build())
                );
        when(eventRepository.existsById(eventDto.getId())).thenReturn(true);

        assertThrows(DataNotMatchException.class, () -> eventService.updateEvent(eventDto));
        verify(skillRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void testToUpdateEventWhichNotSuchEvent() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        when(eventRepository.existsById(eventDto.getId())).thenReturn(false);

        assertThrows(EntityNotFoundExceptionWithID.class, () -> eventService.updateEvent(eventDto));
        verify(eventRepository, times(1)).existsById(eventDto.getId());
    }

    @Test
    public void testToGettingEventsByTitle() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .titlePattern("social media")
                .build();
        when(eventRepository.findAll()).
                thenReturn(List.of(
                        Event.builder().title("social media startup").build(),
                        Event.builder().title("sport programming").build()));

        List<EventDto> eventDtos = eventService.getEventsByFilter(eventFilterDto).getBody();

        assertEquals(1, eventDtos.size());
        assertEquals(eventDtos.get(0).getTitle(), "social media startup");
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void getOwnedEventsByUserId() {
        when(eventRepository.findAllByUserId(1L))
                .thenReturn(List.of(Event.builder().id(1L).build()));

        List<EventDto> response = eventService.getOwnedEvents(1L).getBody();
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1, response.get(0).getId());
        verify(eventRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void getParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(1L))
                .thenReturn(List.of(Event.builder().id(1).build()));

        List<EventDto> response = eventService.getParticipatedEvents(1L).getBody();
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1, response.get(0).getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(1L);
    }
}