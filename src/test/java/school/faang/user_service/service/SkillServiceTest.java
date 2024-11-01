package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper;

    @Test
    void testCreateWithExistingTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateSaveSkill() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
    }
}
