package school.faang.user_service.controller.skill;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.skill.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;

    @Test
    public void testCreateWithBlankTitle() {
        SkillDto skillDto = new SkillDto(2L, " ");
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateDtoEqualNull() {
        SkillDto skillDto = null;
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateWithBlankEqualNull() {
        SkillDto skillDto = new SkillDto(2L, null);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }
}
