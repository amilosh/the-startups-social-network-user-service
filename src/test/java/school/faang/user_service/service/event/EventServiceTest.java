package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventService eventService;

    private Event event1;
    private Event event2;
    private Event updatedEvent;
    private User user;
    private User userWithoutSkills;
    private User notOwner;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setTitle("SkillTitle1");

        Skill skill2 = new Skill();
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
        event2.setOwner(userWithoutSkills);
        event2.setRelatedSkills(skills);

        updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setTitle("Updated Title1");
        updatedEvent.setOwner(user);
        updatedEvent.setRelatedSkills(skills);
    }

    @Test
    public void createEventTest() {
        when(userRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        Event result = eventService.create(event1);

        verify(userRepository).findByIdWithSkills(1L);
        verify(eventRepository).save(event1);
        assertEquals(event1.getTitle(), result.getTitle());
        assertEquals(event1.getOwner(), result.getOwner());
        assertEquals(event1.getRelatedSkills(), result.getRelatedSkills());
    }

    @Test
    public void createEventShouldThrowExceptionIfUserNotFoundTest() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> eventService.create(event1)
        );
    }

    @Test
    public void createEventShouldThrowExceptionIfUserHasNoSkillTest() {
        when(userRepository.findByIdWithSkills(2L)).thenReturn(Optional.of(userWithoutSkills));
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.create(event2)
        );
    }

    @Test
    public void getEventTest() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event1));

        Event result = eventService.getEvent(1L);

        verify(eventRepository).findById(1L);
        assertEquals(event1.getId(), result.getId());
        assertEquals(event1.getTitle(), result.getTitle());
        assertEquals(event1.getOwner(), result.getOwner());
        assertEquals(event1.getRelatedSkills(), result.getRelatedSkills());
    }

    @Test
    public void getEventShouldThrowExceptionIfEventNotFoundTest() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> eventService.getEvent(1L)
        );
    }

    @Test
    public void getEventsByFilterTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filter = new EventFilterDto();
        filter.setTitle("Title1");
        filter.setOwnerId(1L);
        filter.setLocation("Location1");

        List<Event> result = eventService.getEventsByFilter(filter);

        verify(eventRepository).findAll();
        assertEquals(1, result.size());
        assertTrue(result.contains(event1));
    }

    @Test
    public void getEventsByFilterNoFilterTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filter = new EventFilterDto();
        List<Event> result = eventService.getEventsByFilter(filter);

        verify(eventRepository).findAll();
        assertEquals(2, result.size());
    }

    @Test
    public void getEventsByFilterNoMatchFilterTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filter = new EventFilterDto();
        filter.setTitle("AnotherTitle");

        List<Event> result = eventService.getEventsByFilter(filter);

        verify(eventRepository).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    public void getOwnedEventsTest() {
        List<Event> events = List.of(event1, event2);
        when(eventRepository.findAllByUserId(1L)).thenReturn(events);

        List<Event> result = eventService.getOwnedEvents(1L);

        verify(eventRepository).findAllByUserId(1L);
        assertEquals(2, result.size());
    }

    @Test
    public void getParticipatedEventsTest() {
        List<Event> events = List.of(event1);
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(events);

        List<Event> result = eventService.getParticipatedEvents(1L);

        verify(eventRepository).findParticipatedEventsByUserId(1L);
        assertEquals(1, result.size());
        assertTrue(result.contains(event1));
    }

    @Test
    public void deleteEventTest() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    public void deleteEventShouldThrowExceptionIfUserNotFoundTest() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> eventService.deleteEvent(1L)
        );
    }

    @Test
    public void updateEventTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(userRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        Event result = eventService.updateEvent(updatedEvent);

        assertEquals("Updated Title1", result.getTitle());
    }

    @Test
    public void updateEventShouldThrowExceptionIfEventNotFoundTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> eventService.updateEvent(updatedEvent)
        );
    }

    @Test
    public void updateEventShouldThrowExceptionIfUserHasNoSkillsTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(userRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(userWithoutSkills));

        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(updatedEvent)
        );
    }

    @Test
    public void updateEventShouldThrowExceptionIfUserNoOwnerTest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        updatedEvent.setOwner(notOwner);

        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(updatedEvent)
        );
    }
}
