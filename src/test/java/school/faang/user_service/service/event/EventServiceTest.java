package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    @InjectMocks
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @InjectMocks
    private EventService eventService;

    private Event event1;
    private Event event2;
    private Event updatedEvent;
    private User user;
    private User userWithoutSkills;
    private User notOwner;
    private EventFilterDto filter;
    private EventFilterDto filterFullResult;
    private EventFilterDto filterEmptyResult;
    private EventDto eventDto1;
    private EventDto eventDto2;
    private EventDto updatedEventDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("SkillTitle1");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("SkillTitle2");

        List<Skill> skills = List.of(skill1, skill2);
        user.setSkills(skills);

        Skill skill3 = new Skill();
        skill3.setTitle("SkillTitle3");

        userWithoutSkills = new User();
        userWithoutSkills.setId(2L);
        userWithoutSkills.setSkills(List.of(skill3));

        notOwner = new User();
        notOwner.setId(3L);
        notOwner.setSkills(skills);

        event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Title1");
        event1.setOwner(user);
        event1.setLocation("Location1");
        event1.setRelatedSkills(skills);

        event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Title2");
        event2.setLocation("Location2");
        event2.setOwner(userWithoutSkills);
        event2.setRelatedSkills(skills);

        updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setTitle("Updated Title1");
        updatedEvent.setOwner(user);
        updatedEvent.setRelatedSkills(skills);

        SkillDto skillDto1 = new SkillDto();
        skillDto1.setId(1L);
        skillDto1.setTitle("SkillTitle1");

        SkillDto skillDto2 = new SkillDto();
        skillDto2.setId(2L);
        skillDto2.setTitle("SkillTitle2");

        List<SkillDto> skillsDto = List.of(skillDto1, skillDto2);

        eventDto1 = new EventDto();
        eventDto1.setId(1L);
        eventDto1.setTitle("Title1");
        eventDto1.setLocation("Location1");
        eventDto1.setOwnerId(user.getId());
        eventDto1.setRelatedSkills(skillsDto);

        eventDto2 = new EventDto();
        eventDto2.setId(2L);
        eventDto2.setTitle("Title2");
        eventDto2.setLocation("Location2");
        eventDto2.setOwnerId(userWithoutSkills.getId());
        eventDto2.setRelatedSkills(skillsDto);

        updatedEventDto = new EventDto();
        updatedEventDto.setId(1L);
        updatedEventDto.setTitle("Updated Title1");
        updatedEventDto.setOwnerId(user.getId());
        updatedEventDto.setRelatedSkills(skillsDto);

        filter = new EventFilterDto();
        filter.setTitle("Title1");
        filter.setOwnerId(1L);
        filter.setLocation("Location1");

        filterFullResult = new EventFilterDto();

        filterEmptyResult = new EventFilterDto();
        filterEmptyResult.setTitle("AnotherTitle");
    }

    @Test
    public void createEventTest() {
        when(userRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        EventDto result = eventService.create(eventDto1);

        verify(userRepository).findByIdWithSkills(1L);
        assertEquals(eventDto1.getId(), result.getId());
        assertEquals(eventDto1.getTitle(), result.getTitle());
    }

    @Test
    public void createEventUserNotFoundTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> eventService.create(eventDto1)
        );
    }

    @Test
    public void createEventUserHasNoSkillTest() {
        when(userRepository.findByIdWithSkills(2L)).thenReturn(Optional.of(userWithoutSkills));

        assertThrows(
                DataValidationException.class,
                () -> eventService.create(eventDto2)
        );
    }

    @Test
    public void getEventTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));

        EventDto result = eventService.getEvent(1L);

        verify(eventRepository).findById(1L);
        assertEquals(event1.getId(), result.getId());
        assertEquals(event1.getTitle(), result.getTitle());
        assertEquals(event1.getOwner().getId(), result.getOwnerId());
    }

    @Test
    public void getEventNotFoundTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> eventService.getEvent(1L)
        );
    }

    @Test
    public void getEventsByFilterTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        List<EventDto> result = eventService.getEventsByFilter(filter);

        verify(eventRepository).findAll();
        assertEquals(1, result.size());
    }

    @Test
    public void getEventsNoFilterTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        List<EventDto> result = eventService.getEventsByFilter(filterFullResult);

        verify(eventRepository).findAll();
        assertEquals(2, result.size());
    }

    @Test
    public void getEventsNoMatchingFilterTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        List<EventDto> result = eventService.getEventsByFilter(filterEmptyResult);

        verify(eventRepository).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    public void getOwnedEventsTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAllByUserId(1L)).thenReturn(events);

        List<EventDto> result = eventService.getOwnedEvents(1L);

        verify(eventRepository).findAllByUserId(1L);
        assertEquals(2, result.size());
    }

    @Test
    public void getParticipatedEventsTest() {
        List<Event> events = List.of(event1);
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(events);

        List<EventDto> result = eventService.getParticipatedEvents(1L);

        verify(eventRepository).findParticipatedEventsByUserId(1L);
        assertEquals(1, result.size());
    }

    @Test
    public void deleteEventTest() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    public void deleteEventNotFoundTest() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> eventService.deleteEvent(1L)
        );
    }

    @Test
    public void updateEventTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(userRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        EventDto result = eventService.updateEvent(updatedEventDto);

        assertEquals(updatedEventDto.getTitle(), result.getTitle());
    }

    @Test
    public void updateEventNotFoundTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> eventService.updateEvent(updatedEventDto)
        );
    }

    @Test
    public void updateEventUserHasNoSkillTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(userRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(userWithoutSkills));

        assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(updatedEventDto)
        );
    }

    @Test
    public void updateEventUserNoOwnerTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        updatedEventDto.setOwnerId(notOwner.getId());

        assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(updatedEventDto)
        );
    }
}
