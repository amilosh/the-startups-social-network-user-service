package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillControllerTest {
    private static final long SKILL_ID = 1L;
    private static final long USER_ID = 1L;

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    void testCreateWithEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");

       assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }
}
