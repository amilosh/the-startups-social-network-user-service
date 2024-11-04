package school.faang.user_service.validation.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventValidationTest {
    @InjectMocks
    private EventValidation eventValidation;

    @Mock
    private EventDto eventDto;

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
    public void testValidateEventDto_Success() {
        SkillDto skill1 = SkillDto.builder().id(1L).title("Skill1").build();
        SkillDto skill2 = SkillDto.builder().id(2L).title("Skill2").build();

        EventDto event = EventDto.builder().build();
        event.setRelatedSkills(Arrays.asList(skill1, skill2));

        List<Long> userSkills = Arrays.asList(1L, 2L, 3L);

        eventValidation.validateEventDto(event, userSkills);
    }

    @Test
    public void testValidateEventDto_Failure_MissingSkill() {
        SkillDto skill1 = SkillDto.builder().id(1L).title("Skill1").build();
        SkillDto skill2 = SkillDto.builder().id(2L).title("Skill2").build();

        EventDto event = EventDto.builder().build();
        event.setRelatedSkills(Arrays.asList(skill1, skill2));

        List<Long> userSkills = List.of(1L);

        assertThrows(DataValidationException.class, () -> {
            eventValidation.validateEventDto(event, userSkills);
        }, "User does not have required skills");
    }

    @Test
    public void testValidateEventDto_Failure_NoSkills() {
        EventDto event = EventDto.builder().build();
        event.setRelatedSkills(List.of());
        List<Long> userSkills = Arrays.asList(1L, 2L);

        eventValidation.validateEventDto(event, userSkills);
    }
}


