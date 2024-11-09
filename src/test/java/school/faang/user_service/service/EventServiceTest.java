package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    SkillMapper skillMapper;

    @InjectMocks
    private EventService eventService;

    EventDto eventDto;
    Event event;
    User user;
    SkillDto skillDto1;
    SkillDto skillDto2;
    Skill skill1;
    Skill skill2;
    List<Skill> skills;
    List<SkillDto> skillsDtos;
    List<Event> ownedEvents;
    List<EventDto> ownedEventsDto;

    @BeforeEach
    public void setUp() {
        skillDto1 = SkillDto.builder().id(1L).title("Java").build();
        skillDto2 = SkillDto.builder().id(2L).title("Spring").build();

        skillsDtos = new ArrayList<>();
        skillsDtos.add(skillDto1);
        skillsDtos.add(skillDto2);

        skill1 = Skill.builder().id(1L).title("Java").build();
        skill2 = Skill.builder().id(2L).title("Spring").build();

        skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);


        user = User.builder()
                .id(100L)
                .skills(skills)
                .build();


        event = Event.builder()
                .id(22L)
                .owner(user)
                .title("Java Conference 2024")
                .relatedSkills(skills)
                .build();


        eventDto = EventDto.builder()
                .id(22L)
                .ownerId(user.getId())
                .title("Java Conference 2024")
                .relatedSkills(List.of(
                        new SkillDto(1L, "Java"),
                        new SkillDto(2L, "Spring")))
                .build();

    }

    @Test
    void testCreateEventSuccess() {
        when(userService.getUserById(100L)).thenReturn(user);
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(skillMapper.toDtoList(user.getSkills())).thenReturn(Arrays.asList(skillDto1, skillDto2));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        Mockito.verify(eventRepository, times(1))
                .save(event);
    }

    @Test
    void testCreateEventUserDoNotHaveRequiredSkills() {

        SkillDto sqlDto = SkillDto.builder().id(2L).title("SQL").build();
        User user2 = User.builder()
                .id(100L)
                .skills(
                        Arrays.asList(
                                Skill.builder().id(1L).title("Java").build(),
                                Skill.builder().id(2L).title("SQL").build()))
                .build();


        when(userService.getUserById(100L)).thenReturn(user2);
        when(skillMapper.toDtoList(user2.getSkills())).thenReturn(Arrays.asList(skillDto1, sqlDto));

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        verify(eventRepository, never()).save(Mockito.any(Event.class));
    }

    @Test
    void testGetEventSuccess() {
        when(eventRepository.findById(event.getId())).thenReturn(ofNullable(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(event.getId());

        assertNotNull(result);
        assertEquals(eventDto, result);
    }

    @Test
    void testGetEventWithNonExistentEvent() {
        when(eventRepository.findById(11L)).thenReturn(empty());

        assertThrows(DataValidationException.class, () -> eventService.getEventById(11L));
    }

    @Test
    void testDeleteEvent_Success() {
        ownedEvents = new ArrayList<>();
        ownedEvents.add(event);
        user.setOwnedEvents(ownedEvents);
        List<User> attendees = new ArrayList<>();
        attendees.add(User.builder().id(2L).participatedEvents(ownedEvents).build());
        attendees.add(User.builder().id(3L).participatedEvents(ownedEvents).build());
        event.setAttendees(attendees);

        when(eventRepository.findById(22L)).thenReturn(of(event));

        eventService.deleteEvent(22L);

        verify(eventRepository, times(1)).deleteById(22L);
        verify(userRepository, times(1)).save(user);
        for (User attendee : attendees) {
            verify(userRepository, times(1)).save(attendee);
        }
    }

    @Test
    void testDeleteEventEventNotFound() {
        when(eventRepository.findById(22L)).thenReturn(empty());

        assertThrows(DataValidationException.class,
                () -> eventService.deleteEvent(22L));
    }

    @Test
    void testDeleteEventNoParticipantsOrOwner() {
        ownedEvents = new ArrayList<>();
        user.setOwnedEvents(ownedEvents);
        event.setAttendees(new ArrayList<>()); // Пустой список участников

        when(eventRepository.findById(22L)).thenReturn(of(event));

        eventService.deleteEvent(22L);

        verify(eventRepository, times(1)).deleteById(22L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetOwnedEventsWithUserId() {
        ownedEventsDto = new ArrayList<>();
        ownedEventsDto.add(eventDto);

        ownedEvents = new ArrayList<>();
        ownedEvents.add(event);
        user.setOwnedEvents(ownedEvents);

        when(userService.getUserById(100L)).thenReturn(user);
        when(eventMapper.toDtoList(ownedEvents)).thenReturn(ownedEventsDto);

        List<EventDto> result = eventService.getOwnedEvents(100L);

        assertEquals(ownedEventsDto, result);
    }

    @Test
    void testGetOwnedWithoutUserId() {
        when(userService.getUserById(10000L)).
                thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class,
                () -> eventService.getOwnedEvents(10000L));
    }

    @Test
    void testGetOwnedEventsWhenNoOwnedEvents() {
        User user = mock(User.class);
        when(userService.getUserById(10000L)).thenReturn(user);
        when(user.getOwnedEvents()).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getOwnedEvents(10000L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}