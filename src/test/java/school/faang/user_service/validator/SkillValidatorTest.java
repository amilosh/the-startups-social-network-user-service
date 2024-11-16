package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.SkillDuplicateException;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;

    private Skill skill;
    private long id;
    private String title;

    @BeforeEach
    void setUp() {
        id = 1L;
        title = "title";
        skill = Skill.builder().id(id).title(title).build();
    }

    @Test
    void testValidateSkillExists() {
        when(skillRepository.existsById(id)).thenReturn(true);

        boolean result = skillValidator.validateSkillExists(id);

        verify(skillRepository, times(1)).existsById(id);
        assertTrue(result);
    }

    @Test
    void testValidateDuplicateShouldThrowException() {
        when(skillRepository.existsByTitle(title)).thenReturn(true);

        assertThrows(SkillDuplicateException.class, () ->
                skillValidator.validateDuplicate(skill));
    }

    @Test
    void testValidateDuplicateSuccess() {
        when(skillRepository.existsByTitle(title)).thenReturn(false);

        assertDoesNotThrow(() -> skillValidator.validateDuplicate(skill));
    }
}