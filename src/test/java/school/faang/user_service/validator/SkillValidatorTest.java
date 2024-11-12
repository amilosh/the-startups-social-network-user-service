package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;

    @Test
    void testValidateSkillExists() {
        when(skillRepository.existsById(1L)).thenReturn(true);

        boolean result = skillValidator.validateSkillExists(1L);

        verify(skillRepository, times(1)).existsById(1L);
        assertTrue(result);
    }
}