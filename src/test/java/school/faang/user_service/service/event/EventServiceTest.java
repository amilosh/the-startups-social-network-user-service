package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.event_filters.EventDateRangeFilter;
import school.faang.user_service.service.event.event_filters.EventFilter;
import school.faang.user_service.service.event.event_filters.EventLocationFilter;
import school.faang.user_service.service.event.event_filters.EventMaxAttendeesFilter;
import school.faang.user_service.service.event.event_filters.EventOwnerNameFilter;
import school.faang.user_service.service.event.event_filters.EventSkillsFilter;
import school.faang.user_service.service.event.event_filters.EventTitleFilter;
import school.faang.user_service.validator.event.EventServiceValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
    private EventServiceValidator eventServiceValidator;
    @InjectMocks
    private EventService eventService;

    private EventMapperImpl eventMapper;
    private SkillMapperImpl skillMapper;

    private User userJohn;
    private Event eventBaking;
    private Event eventCarFixing;

    @BeforeEach
    void setUp() {
        List<EventFilter> eventFilters = List.of(new EventTitleFilter(), new EventDateRangeFilter(),
                new EventMaxAttendeesFilter(), new EventOwnerNameFilter(),
                new EventSkillsFilter(), new EventLocationFilter());
        ReflectionTestUtils.setField(eventService, "eventFilters", eventFilters);
        skillMapper = new SkillMapperImpl();
        eventMapper = new EventMapperImpl(skillMapper);
        eventService = new EventService
                (eventRepository, eventServiceValidator, eventMapper, eventFilters, skillRepository);

        ReflectionTestUtils.setField(eventService, "batchSize", 10);
    }

    @BeforeEach
    public void init() {
        Skill bakingSkill = new Skill();
        bakingSkill.setId(1L);
        bakingSkill.setTitle("Baking Skill");

        Skill decoratingSkill = new Skill();
        decoratingSkill.setId(2L);
        decoratingSkill.setTitle("Decorating Skill");

        userJohn = new User();
        userJohn.setId(1L);
        userJohn.setUsername("John");
        userJohn.setSkills(new ArrayList<>(Set.of(bakingSkill, decoratingSkill)));

        User userJane = new User();
        userJane.setId(2L);
        userJane.setUsername("Jane");
        userJane.setSkills(new ArrayList<>(Set.of(bakingSkill)));

        eventBaking = new Event();
        eventBaking.setId(1L);
        eventBaking.setTitle("Baking Event");
        eventBaking.setRelatedSkills(new ArrayList<>(Set.of(bakingSkill, decoratingSkill)));
        eventBaking.setOwner(userJohn);
        eventBaking.setAttendees(new ArrayList<>(Set.of(userJane)));
        eventBaking.setMaxAttendees(30);

        eventCarFixing = new Event();
        eventCarFixing.setTitle("Car Fixing");
        eventCarFixing.setMaxAttendees(100);
    }

    @Test
    public void testCreateSavingEvent() {
        EventDto eventBakingDto = eventMapper.toDto(eventBaking);

        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("Baking");
        eventBakingDto.setRelatedSkills(List.of(skillDto));

        when(skillRepository.findAllById(anyList())).thenReturn(List.of(skillMapper.toEntity(skillDto)));

        when(eventServiceValidator.validateUserId(eventBakingDto.getOwnerId())).thenReturn(userJohn);
        doNothing().when(eventServiceValidator).validateOwnerSkills(userJohn, eventBakingDto);

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(1L);
            return event;
        });

        when(skillRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Skill> skills = invocation.getArgument(0);
            skills.forEach(s -> s.setId(100L));
            return skills;
        });

        EventDto result = eventService.create(eventBakingDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getRelatedSkills());
        assertEquals(1, result.getRelatedSkills().size());
        assertEquals("Baking", result.getRelatedSkills().get(0).getTitle());

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();

        assertNotNull(capturedEvent.getRelatedSkills());
        assertEquals(1, capturedEvent.getRelatedSkills().size());
        assertEquals("Baking", capturedEvent.getRelatedSkills().get(0).getTitle());
        verify(skillRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testGetRetrievesEventFromRepository() {
        EventDto eventBakingDto = eventMapper.toDto(eventBaking);
        long eventId = 1L;
        when(eventServiceValidator.validateEventId(eventId)).thenReturn(eventBaking);
        EventDto result = eventService.get(eventId);
        verify(eventServiceValidator, times(1)).validateEventId(eventId);
        assertEquals(eventBakingDto, result);
    }

    @Test
    public void testGetByFilter_withTitleFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitlePattern("Baking");

        when(eventRepository.findAll()).thenReturn(List.of(eventBaking, eventCarFixing));

        List<EventDto> result = eventService.getByFilter(eventFilterDto);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getTitle(), "Baking Event");
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetByFilter_withMaxAttendeesFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setMaxAttendees(50);

        when(eventRepository.findAll()).thenReturn(List.of(eventBaking, eventCarFixing));

        eventBaking.setMaxAttendees(30);
        eventCarFixing.setMaxAttendees(100);

        List<EventDto> eventDtos = eventService.getByFilter(eventFilterDto);

        assertEquals(1, eventDtos.size());
        assertEquals(eventDtos.get(0).getTitle(), "Baking Event");
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteDeletesEventFromRepository() {
        long eventId = 1L;
        eventService.delete(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdateUpdatesEventInRepository() {
        EventDto updatingEventDto = new EventDto();
        updatingEventDto.setId(1L);
        updatingEventDto.setTitle("Updated Baking Event");
        updatingEventDto.setMaxAttendees(40);
        updatingEventDto.setOwnerId(1L);

        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Cooking");
        updatingEventDto.setRelatedSkills(List.of(skillDto));

        when(eventServiceValidator.validateEventId(updatingEventDto.getId())).thenReturn(eventBaking);
        when(eventServiceValidator.validateUserId(updatingEventDto.getOwnerId())).thenReturn(userJohn);

        Event event = eventMapper.toEntity(updatingEventDto);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        when(skillRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Skill> skills = invocation.getArgument(0);
            skills.forEach(s -> s.setId(200L));
            return skills;
        });

        EventDto result = eventService.update(updatingEventDto);

        assertNotNull(result);
        assertEquals(updatingEventDto.getId(), result.getId());
        assertNotNull(result.getRelatedSkills());
        assertEquals(1, result.getRelatedSkills().size());
        assertEquals("Cooking", result.getRelatedSkills().get(0).getTitle());

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(skillRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testGetOwnedEventsGetsEventsByOwner() {
        EventDto eventBakingDto = eventMapper.toDto(eventBaking);
        long ownerId = 1L;
        List<Event> ownedEvents = List.of(eventBaking);

        when(eventRepository.findAllByUserId(ownerId)).thenReturn(ownedEvents);

        List<EventDto> result = eventService.getOwnedEvents(ownerId);

        assertEquals(1, result.size());
        assertEquals(eventBakingDto, result.get(0));
    }

    @Test
    public void testGetParticipatedEventsGetsEventsByParticipant() {
        EventDto eventBakingDto = eventMapper.toDto(eventBaking);
        long userId = 2L;
        List<Event> participatedEvents = List.of(eventBaking);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(participatedEvents);

        List<EventDto> result = eventService.getParticipatedEvents(userId);

        assertEquals(1, result.size());
        assertEquals(eventBakingDto, result.get(0));
    }

    @Test
    public void testDeletePastEvents_whenEventsExist_shouldDeleteEvents() throws Exception {
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(List.of(eventBaking, eventCarFixing));
        doNothing().when(eventRepository).deleteAllById(any());

        CompletableFuture<Void> result = eventService.deletePastEvents();
        result.get();

        verify(eventRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
        verify(eventRepository, times(1)).deleteAllById(any());
        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
    }

    @Test
    public void testDeletePastEvents_whenNoEventsExist_shouldNotDeleteEvents() throws Exception {
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(List.of());

        CompletableFuture<Void> result = eventService.deletePastEvents();
        result.get();

        verify(eventRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
        verify(eventRepository, never()).deleteAllById(any());
        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
    }

    @Test
    public void testDeletePastEvents_whenExceptionOccurs_shouldLogError() {
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<Void> result = eventService.deletePastEvents();

        assertTrue(result.isCompletedExceptionally());
        verify(eventRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
    }
}