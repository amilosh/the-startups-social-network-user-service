package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;

    @Test
    public void existsByIdWithValidSkillIdSuccessTest() {
        Long skillId = 1L;
        when(skillRepository.existsById(skillId)).thenReturn(true);

        assertTrue(skillValidator.existsById(skillId));
    }

    @Test
    public void existsByIdWithNotValidSkillIdSuccessTest() {
        Long skillId = 1L;
        when(skillRepository.existsById(skillId)).thenReturn(false);

        assertFalse(skillValidator.existsById(skillId));
    }
}
