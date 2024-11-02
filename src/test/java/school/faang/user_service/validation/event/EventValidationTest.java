package school.faang.user_service.validation.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventValidationTest {
    @InjectMocks
    EventValidation eventValidation;

    @Mock
    private EventDto eventDto;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .title("Event Title")
                .build();
    }

    @Test
    public void testEventEmptyOrBlankTitle() {
        eventDto.setTitle("  ");
        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventNullTitle() {
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventDtoUserHasRequiredSkills() {
        Skill skill = new Skill();
        skill.setId(1L);

        User user = new User();
        user.setSkills(List.of(skill));

        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto()));

        when(userService.findOwnerById(eventDto)).thenReturn(user);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                eventValidation.validateEventDto(eventDto, userService));
        assertEquals("User does not have required skills", exception.getMessage());

    }

    @Test
    public void testValidateEventDtoUserDoesNotHaveRequiredSkills() {
        Skill skill = new Skill();
        skill.setId(1L);

        User user = new User();
        user.setSkills(List.of());

        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto()));

        when(userService.findOwnerById(eventDto)).thenReturn(user);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                eventValidation.validateEventDto(eventDto, userService));
        assertEquals("User does not have required skills", exception.getMessage());
    }
}
