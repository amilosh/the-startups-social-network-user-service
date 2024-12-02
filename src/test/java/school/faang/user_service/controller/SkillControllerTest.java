package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testCreateNullTitle() {
        SkillDto skillDto = new SkillDto();

        Throwable exception = assertThrows(
                DataValidationException.class,
                () -> skillController.create(skillDto));

        assertEquals("the skill has not a title", exception.getMessage());
    }

    @Test
    public void testCreateEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");

        Throwable exception = assertThrows(
                DataValidationException.class,
                () -> skillController.create(skillDto));

        assertEquals("the skill has not a title", exception.getMessage());
    }

    @Test
    public void testCreateBlanckTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(" ");

        Throwable exception = assertThrows(
                DataValidationException.class,
                () -> skillController.create(skillDto));

        assertEquals("the skill has not a title", exception.getMessage());
    }

    @Test
    public void testCreateRealTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("blabla");

        skillController.create(skillDto);

        Mockito.verify(skillService, Mockito.times(1))
                .create(skillDto);
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1;
        skillController.getUserSkills(userId);

        Mockito.verify(skillService, Mockito.times(1))
                .getUserSkills(userId);
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1;
        skillController.getOfferedSkills(userId);

        Mockito.verify(skillService, Mockito.times(1))
                .getOfferedSkills(userId);
    }

    @Test
    public void testAcquireSkillFromOffers() {
        long skillId = 1;
        long userId = 1;
        skillController.acquireSkillFromOffers(skillId, userId);

        Mockito.verify(skillService, Mockito.times(1))
                .acquireSkillFromOffers(skillId, userId);
    }

}
