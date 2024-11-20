package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventValidationTest {
    private final EventValidation eventValidation = new EventValidation();
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .title("Event Title")
                .startDate(LocalDateTime.now(ZoneId.of("UTC")))
                .ownerId(1L)
                .build();
    }

    @Test
    public void testEventEmptyTitle() {
        eventDto.setTitle("");
        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventNullTitle() {
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventNullStartDate() {
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventNullOwnerId() {
        eventDto.setOwnerId(null);
        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventSuccess() {
        assertDoesNotThrow(() -> eventValidation.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventDtoSuccess() {
        SkillDto skill1 = SkillDto.builder().id(1L).title("Skill1").build();
        SkillDto skill2 = SkillDto.builder().id(2L).title("Skill2").build();

        EventDto event = EventDto.builder().build();
        event.setRelatedSkills(Arrays.asList(skill1, skill2));

        List<Long> userSkills = Arrays.asList(1L, 2L, 3L);

        eventValidation.validateRelatedSkills(event, userSkills);
    }

    @Test
    public void testValidateEventDtoFailureMissingSkill() {
        SkillDto skill1 = SkillDto.builder().id(1L).title("Skill1").build();
        SkillDto skill2 = SkillDto.builder().id(2L).title("Skill2").build();

        EventDto event = EventDto.builder().build();
        event.setRelatedSkills(Arrays.asList(skill1, skill2));

        List<Long> userSkills = List.of(1L);

        assertThrows(DataValidationException.class, () ->
                eventValidation.validateRelatedSkills(event, userSkills), "User does not have required skills");
    }

    @Test
    public void testValidateEventDtoFailureNoSkills() {
        EventDto event = EventDto.builder().build();
        event.setRelatedSkills(List.of());
        List<Long> userSkills = Arrays.asList(1L, 2L);

        eventValidation.validateRelatedSkills(event, userSkills);
    }
}


