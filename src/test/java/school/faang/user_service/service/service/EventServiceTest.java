package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventServiceImplementation;
import school.faang.user_service.validator.event.EventOwnerValidator;
import school.faang.user_service.validator.event.EventStartDateValidator;
import school.faang.user_service.validator.event.EventTitleValidator;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private UserRepository userRepository;
    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    private final EventOwnerValidator eventOwnerValidator = new EventOwnerValidator();
    private final EventStartDateValidator eventStartDateValidator = new EventStartDateValidator();
    private final EventTitleValidator eventTitleValidator = new EventTitleValidator();
    @InjectMocks
    private EventServiceImplementation eventService;

    private final List<EventValidator> eventValidators = Arrays.asList(eventOwnerValidator, eventStartDateValidator, eventTitleValidator);
    ;
    private EventDto eventDto;
    private Event event;
    private Skill skill;
    private User user;


    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);

        event = new Event();
        user = new User();
        skill = new Skill();

        eventService.setEventValidators(eventValidators);
    }

    @Test
    public void testCreateWithBlankTitle() {
        eventDto.setTitle(" ");
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void testCreateWithNullTitle() {
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testCreateWithNullOwner() {
        eventDto.setTitle("Title");
        eventDto.setOwnerId(null);
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testCreateWithNullStartDate() {
        prepareDtoWithTitleAndOwnerId();
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testCreateSaveEvent() {
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
        EventDto eventDto = eventService.getEvent(1L);
        assertNull(eventDto);
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

    private void prepareDtoWithTitleAndOwnerId() {
        eventDto.setTitle("Title");
        eventDto.setOwnerId(1L);
    }


}
