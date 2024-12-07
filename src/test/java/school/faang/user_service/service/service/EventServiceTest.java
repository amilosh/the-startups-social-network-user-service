package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventDescriptionFilter;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.skill.SkillRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventOwnerValidator;
import school.faang.user_service.validator.event.EventStartDateValidator;
import school.faang.user_service.validator.event.EventTitleValidator;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    private final EventOwnerValidator eventOwnerValidator = new EventOwnerValidator();
    private final EventStartDateValidator eventStartDateValidator = new EventStartDateValidator();
    private final EventTitleValidator eventTitleValidator = new EventTitleValidator();
    private final EventDescriptionFilter eventDescriptionFilter = new EventDescriptionFilter();
    private final EventTitleFilter eventTitleFilter = new EventTitleFilter();
    private final EventOwnerFilter eventOwnerFilter = new EventOwnerFilter();

    private final List<EventValidator> eventValidators = Arrays.asList(eventOwnerValidator, eventStartDateValidator, eventTitleValidator);
    private final List<EventFilter> eventFilters = Arrays.asList(eventDescriptionFilter, eventTitleFilter, eventOwnerFilter);

    private EventService eventService;

    private EventDto eventDto;
    private Event event;
    private Skill skill;
    private User user;
    private EventFilterDto filter;


    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);

        event = new Event();
        event.setId(1L);
        user = new User();
        user.setId(33L);
        skill = new Skill();

        eventService = new EventService(
                eventRepository,
                skillRepository,
                userRepository,
                eventMapper,
                eventValidators,
                eventFilters
        );
    }

    @Test
    void testCreateSaveEvent() {
        prepareDtoWithTitleAndOwnerId();
        eventDto.setStartDate(LocalDateTime.of(2024, 11, 3, 17, 0));

        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(event);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);
        when(skillRepository.findSkillsByGoalId(anyLong())).thenReturn(Collections.singletonList(skill));
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);

        verify(skillRepository, times(1)).findSkillsByGoalId(eventDto.getId());
        verify(userRepository, times(1)).getReferenceById(eventDto.getOwnerId());
        verify(eventRepository, times(1)).save(event);
        verify(eventMapper, times(1)).toDto(event);

        assertEquals(eventDto.getId(), result.getId());
        assertEquals(eventDto.getTitle(), result.getTitle());
        assertEquals(eventDto.getStartDate(), result.getStartDate());
    }

    @Test
    void testGetEventByIdIfIfDoesNotExistInDb() {
        prepareDtoWithTitleAndOwnerId();
        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(1L));
    }

    @Test
    void testGetEventByIdIfExistsInDb() {
        prepareDtoWithTitleAndOwnerId();
        event.setRelatedSkills(new ArrayList<>());
        Long eventId = 1L;
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        EventDto eventDto = eventService.getEvent(eventId);
        verify(eventRepository, times(1)).findById(eventId);
        assertNotNull(eventDto);
    }

    @Test
    void testGetEventsByFilterIfFilterISEmpty() {
        prepareFiltersAndEvent();
        filter = new EventFilterDto(null, null, null);
        List<EventDto> eventsDto = eventService.getEventsByFilter(filter);
        assertTrue(eventsDto.isEmpty());
    }

    @Test
    void testGetEventsByFilterIfDoesNotExistInDb() {
        prepareFiltersAndEvent();
        when(eventRepository.findAll()).thenReturn(new ArrayList<>());
        List<EventDto> eventsDto = eventService.getEventsByFilter(filter);
        assertTrue(eventsDto.isEmpty());
    }

    @Test
    void testGetEventsByFilterIfExistsInDb() {
        prepareDtoWithTitleAndOwnerId();
        prepareFiltersAndEvent();
        List<Event> events = prepareEvents();

        when(eventRepository.findAll()).thenReturn(events);
        List<EventDto> eventsDto = eventService.getEventsByFilter(filter);
        verify(eventRepository, times(1)).findAll();
        System.out.println(filter);
        assertNotNull(eventsDto);
        assertEquals(1, eventsDto.size());
        assertEquals(eventsDto.get(0).getTitle(), filter.getTitlePattern());
        assertTrue(eventsDto.get(0).getDescription().contains(filter.getDescriptionPattern()));
        assertEquals(eventsDto.get(0).getOwnerId(), filter.getOwnerIdPattern());
    }

    @Test
    void testOwnedEventsWithDbEmptyResponseUserId() {
        List<EventDto> eventsDto = eventService.getOwnedEvents(1L);
        verify(eventRepository, times(1)).findAllByUserId(anyLong());
        assertTrue(eventsDto.isEmpty());
    }

    @Test
    void testOwnedEventsWithDbResponseUserId() {
        prepareDtoWithTitleAndOwnerId();
        prepareFiltersAndEvent();
        List<Event> events = prepareEvents();
        when(eventRepository.findAllByUserId(anyLong())).thenReturn(events);
        List<EventDto> eventsDto = eventService.getOwnedEvents(1L);
        verify(eventRepository, times(1)).findAllByUserId(anyLong());
        assertFalse(eventsDto.isEmpty());
        assertEquals(3, eventsDto.size());
    }

    @Test
    void testGetParticipatedEventsWithDbEmptyResponse() {
        List<EventDto> eventsDto = eventService.getParticipatedEvents(1L);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(anyLong());
        assertTrue(eventsDto.isEmpty());
    }

    @Test
    void testGetParticipatedEventsWithDbResponseUserId() {
        prepareDtoWithTitleAndOwnerId();
        prepareFiltersAndEvent();
        List<Event> events = prepareEvents();
        when(eventRepository.findParticipatedEventsByUserId(anyLong())).thenReturn(events);
        List<EventDto> eventsDto = eventService.getParticipatedEvents(1L);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(anyLong());
        assertFalse(eventsDto.isEmpty());
        assertEquals(3, eventsDto.size());
    }

    @Test
    void testRemovePastEvent() {
        eventService.deletePastEvents();
        verify(eventRepository).deleteAllPastEvents(any(LocalDateTime.class));
    }

    private void prepareFiltersAndEvent() {
        filter = new EventFilterDto("Title", "desc", 33L);
        event.setDescription("desc");
        event.setRelatedSkills(new ArrayList<>());
    }

    private void prepareDtoWithTitleAndOwnerId() {
        eventDto.setTitle("Title");
        eventDto.setOwnerId(33L);
    }

    private List<Event> prepareEvents() {
        event.setDescription("desc");
        event.setRelatedSkills(new ArrayList<>());
        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setRelatedSkills(new ArrayList<>());
        secondEvent.setTitle("t");
        secondEvent.setDescription("Description");
        secondEvent.setOwner(user);
        Event thirdEvent = new Event();
        thirdEvent.setId(3L);
        thirdEvent.setTitle("Title");
        thirdEvent.setDescription("desc");
        thirdEvent.setOwner(user);
        thirdEvent.setRelatedSkills(new ArrayList<>());

        return Arrays.asList(event, secondEvent, thirdEvent);
    }
}
