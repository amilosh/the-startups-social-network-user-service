package school.faang.user_service.validator.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceValidatorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceValidator eventServiceValidator;

    private User userJohn;
    private User userJane;
    private Event eventBaking;
    private Skill bakingSkill;
    private Skill decoratingSkill;
    private EventDto eventDto;

    @BeforeEach
    public void init() {
        bakingSkill = new Skill();
        bakingSkill.setId(1L);
        bakingSkill.setTitle("Baking Skill");
        decoratingSkill = new Skill();
        decoratingSkill.setId(2L);
        decoratingSkill.setTitle("Decorating Skill");

        userJohn = new User();
        userJohn.setId(1L);
        userJohn.setUsername("John");
        userJohn.setSkills(new ArrayList<>(Set.of(bakingSkill, decoratingSkill)));

        userJane = new User();
        userJane.setId(2L);
        userJane.setUsername("Jane");
        userJane.setSkills(new ArrayList<>(Set.of(bakingSkill)));

        eventBaking = new Event();
        eventBaking.setId(1L);
        eventBaking.setTitle("Baking Event");
        eventBaking.setRelatedSkills(new ArrayList<>(Set.of(bakingSkill, decoratingSkill)));

        eventDto = new EventDto();
    }

    @Test
    public void testValidateEventIdWithAnExistingEventId() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventBaking));
        Event result = eventServiceValidator.validateEventId(1L);
        Assertions.assertEquals(eventBaking, result);
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    public void testValidateEventIdWithNoExistingEventId() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventServiceValidator.validateEventId(1L));
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    public void testValidateUserIdWithAnExistingUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userJohn));
        User result = eventServiceValidator.validateUserId(1L);
        Assertions.assertEquals(userJohn, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testValidateUserIdWithNoExistingUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventServiceValidator.validateUserId(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testValidateOwnerSkillsWithRequiredSkills() {
        SkillDto bakingSkillDto = new SkillDto(bakingSkill.getId(), bakingSkill.getTitle());
        SkillDto decoratingSkillDto = new SkillDto(decoratingSkill.getId(), decoratingSkill.getTitle());
        eventDto.setRelatedSkills(List.of(bakingSkillDto, decoratingSkillDto));
        assertDoesNotThrow(() -> eventServiceValidator.validateOwnerSkills(userJohn, eventDto));
    }

    @Test
    public void testValidateOwnerSkillsWithNoRequiredSkills() {
        SkillDto bakingSkillDto = new SkillDto(bakingSkill.getId(), bakingSkill.getTitle());
        SkillDto decoratingSkillDto = new SkillDto(decoratingSkill.getId(), decoratingSkill.getTitle());
        eventDto.setRelatedSkills(List.of(bakingSkillDto, decoratingSkillDto));
        assertThrows(DataValidationException.class, () -> eventServiceValidator.validateOwnerSkills(userJane, eventDto));
    }
}