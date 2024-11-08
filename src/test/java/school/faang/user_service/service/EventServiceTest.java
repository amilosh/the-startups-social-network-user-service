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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    List<Skill> skillsDtos;
    List<Event> ownedEvents;

    @BeforeEach
    public void setUp() {
        skillDto1 = SkillDto.builder().id(1L).title("Java").build();
        skillDto2 = SkillDto.builder().id(2L).title("Spring").build();

        skill1 = Skill.builder().id(1L).title("Java").build();
        skill2 = Skill.builder().id(2L).title("Spring").build();
//
        skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);


        event = Event.builder()
                .id(22L)
                .owner(user)
                .title("Java Conference 2024")
                .relatedSkills(skills)
                .build();

        user = User.builder()
                .id(100L)
                .skills(skills)
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
    void testCreateEvent_Success() {
        //Arrange
        when(userService.getUserById(100L)).thenReturn(user);
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(skillMapper.toDtoList(user.getSkills())).thenReturn(Arrays.asList(skillDto1, skillDto2));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        //Act
        EventDto result = eventService.create(eventDto);

        //Assert
        assertNotNull(result);
        Mockito.verify(eventRepository, times(1))
                .save(event);
    }

    @Test
    void testCreateEvent_UserDoNotHaveRequiredSkills() {

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
    void testGetEvent_Success() {
        long idEventDto = 22L;
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(event.getId());

        assertNotNull(result);
        assertEquals(eventDto, result);
    }

    @Test
    void testGetEvent_WithNonExistentEvent() {
        when(eventRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.getEventById(11L));
    }

    @Test
    void testDeleteEvent_Success() {
        ownedEvents = new ArrayList<>();
        ownedEvents.add(event);
        user.setOwnedEvents(ownedEvents);

        when(eventRepository.findById(22L)).thenReturn(Optional.of(event));

        eventService.deleteEvent(22L);

        verify(eventRepository, times(1)).deleteById(22L);
    }

    @Test
    void testUserSkillsForEvent_UserSkillsDoNotContainAllRequiredSkills() {
        EventDto eventDtoOther = EventDto.builder()
                .id(8L)
                .relatedSkills(List.of(
                        new SkillDto(1L, "Java"),
                        new SkillDto(2L, "Spring Boot")))
                .build();


        when(skillMapper.toDtoList(user.getSkills())).thenReturn(Arrays.asList(skillDto1, skillDto2));

        boolean result = eventService.isUserHaveSkillsForEvent(user, eventDtoOther);

        assertFalse(result);
    }

    @Test
    void testUserSkillsForEvent_UserSkillsContainAllRequiredSkills() {
        when(skillMapper.toDtoList(user.getSkills())).thenReturn(Arrays.asList(skillDto1, skillDto2));

        boolean result = eventService.isUserHaveSkillsForEvent(user, eventDto);

        assertTrue(result);
    }

    @Test
    void testUserSkillsForEvent_UserSkillsAreEmpty() {
        User userWithNoSkills = User.builder()
                .id(100L)
                .skills(List.of())  // У пользователя нет навыков
                .build();

        EventDto eventDtoWithSkills = EventDto.builder()
                .id(7L)
                .relatedSkills(List.of(
                        new SkillDto(1L, "Java"),
                        new SkillDto(2L, "Spring")))
                .build();

        when(skillMapper.toDtoList(anyList())).thenReturn(List.of());

        boolean result = eventService.isUserHaveSkillsForEvent(userWithNoSkills, eventDtoWithSkills);

        assertFalse(result, "User with no skills should not be able to create an event with required skills");
    }
}