package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    private final static int SKILL_ID = 1;
    private final static int USER_Id = 1;

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    void testCreateWithNullTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    void testCreateWithEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");

       assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    void testCrateWithCorrectTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");

        skillController.create(skillDto);

        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    void testGetUserSkills() {
        skillService.getUserSkills(USER_Id);

        verify(skillService, times(1)).getUserSkills(USER_Id);
    }

    @Test
    void testGetOfferedSkill() {
        skillService.getOfferedSkills(USER_Id);

        verify(skillService, times(1)).getOfferedSkills(USER_Id);
    }

    @Test
    void acquireSkillFromOffers() {
        skillService.acquireSkillFromOffers(SKILL_ID, USER_Id);

        verify(skillService, times(1)).acquireSkillFromOffers(SKILL_ID, USER_Id);
    }
}
