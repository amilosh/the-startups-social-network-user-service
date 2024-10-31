package school.faang.user_service.service.skill;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckIfSkillExistsById_SkillExists() {
        Long skillId = 1L;
        when(skillRepository.existsById(skillId)).thenReturn(true);

        boolean exists = skillService.checkIfSkillExistsById(skillId);

        assertThat(exists).isTrue();
        verify(skillRepository, times(1)).existsById(skillId);
    }

    @Test
    void testCheckIfSkillExistsById_SkillDoesNotExist() {
        Long skillId = 1L;
        when(skillRepository.existsById(skillId)).thenReturn(false);

        boolean exists = skillService.checkIfSkillExistsById(skillId);

        assertThat(exists).isFalse();
        verify(skillRepository, times(1)).existsById(skillId);
    }

    @Test
    void testGetSkillById_SkillExists() {
        Long skillId = 1L;
        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("Test Skill");
        when(skillRepository.getReferenceById(skillId)).thenReturn(skill);

        Skill retrievedSkill = skillService.getSkillById(skillId);

        assertThat(retrievedSkill).isNotNull();
        assertThat(retrievedSkill.getId()).isEqualTo(skillId);
        verify(skillRepository, times(1)).getReferenceById(skillId);
    }

    @Test
    void testGetSkillById_SkillDoesNotExist() {
        Long skillId = 1L;
        when(skillRepository.getReferenceById(skillId)).thenThrow(new EntityNotFoundException());

        assertThatThrownBy(() -> skillService.getSkillById(skillId))
                .isInstanceOf(EntityNotFoundException.class);
        verify(skillRepository, times(1)).getReferenceById(skillId);
    }
}