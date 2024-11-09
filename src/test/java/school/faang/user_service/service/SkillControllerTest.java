package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    public static SkillDto anySkillDto(String title) {
        SkillDto skill = new SkillDto();
        skill.setTitle(title);
        return skill;
    }

    @Test
    public void testCreateSkillPositive() {

        SkillDto skillDto = anySkillDto("title");
        when(skillService.create(skillDto)).thenReturn(skillDto);

        skillController.create(skillDto);
        verify(skillService, times(1)).create(skillDto);
    }
}
