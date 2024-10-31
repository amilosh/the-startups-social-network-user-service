package school.faang.user_service.aaa.bbb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.EventValidation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventValidation eventValidation;

    private EventDto eventDto;
    private User user;
    private Event event;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(1L);

        eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .ownerId(1L)
                .build();

        user = new User();
        user.setId(1L);
        user.setSkills(List.of(skill));

        event = new Event();
        event.setId(1L);
        event.setRelatedSkills(List.of(skill));
    }

    @Test
    public void testCreateEventSuccess() {
        doNothing().when(eventValidation).validateEvent(eventDto);
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));
        when(eventMapper.dtoToEvent(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);

        EventDto result = eventService.create(eventDto);
        assertEquals(eventDto, result);

        verify(eventValidation).validateEvent(eventDto);
        verify(userRepository).findById(eventDto.getOwnerId());
        verify(eventRepository).findById(eventDto.getId());
        verify(eventMapper).dtoToEvent(eventDto);
        verify(eventRepository).save(event);
        verify(eventMapper).eventToDto(event);
    }

    @Test
    public void testCreateEventUserNotFound() {
        doNothing().when(eventValidation).validateEvent(eventDto);
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.create(eventDto));

        verify(eventValidation).validateEvent(eventDto);
        verify(userRepository).findById(eventDto.getOwnerId());
        verify(eventRepository, never()).findById(anyLong());
        verify(eventMapper, never()).dtoToEvent(any(EventDto.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void testCreateEventSkillsMismatch() {
        Skill differentSkill = new Skill();
        differentSkill.setId(2L);
        user.setSkills(List.of(differentSkill));

        doNothing().when(eventValidation).validateEvent(eventDto);
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));

        assertThrows(NoSuchElementException.class, () -> eventService.create(eventDto));

        verify(eventValidation).validateEvent(eventDto);
        verify(userRepository).findById(eventDto.getOwnerId());
        verify(eventRepository).findById(eventDto.getId());
        verify(eventMapper, never()).dtoToEvent(any(EventDto.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void testCreateEventValidationFails() {
        doThrow(new DataValidationException("Validation failed")).when(eventValidation).validateEvent(eventDto);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));

        verify(eventValidation).validateEvent(eventDto);
        verify(userRepository, never()).findById(anyLong());
        verify(eventRepository, never()).findById(anyLong());
        verify(eventMapper, never()).dtoToEvent(any(EventDto.class));
        verify(eventRepository, never()).save(any(Event.class));
    }
}
