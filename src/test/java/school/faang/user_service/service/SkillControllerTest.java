package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.SkillDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController = new SkillController(skillService);

    public static SkillDto anySkillDto(String title) {
        SkillDto skill = new SkillDto();
        skill.setTitle(title);
        return skill;
    }

    @Test
    public void testValidateSkillEmpty() {
        SkillDto skillDto = anySkillDto("");

        boolean result = skillController.validateSkill(skillDto);

        assertFalse(result);
    }

    @Test
    public void testValidateSkillNull() {
        SkillDto skillDto = anySkillDto(null);

        boolean result = skillController.validateSkill(skillDto);

        assertFalse(result);
    }

    @Test
    public void testValidateSkillPositive() {
        SkillDto skillDto = anySkillDto("title");

        boolean result = skillController.validateSkill(skillDto);

        assertTrue(result);
    }
}
