package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    SkillRepository skillRepository;

    @InjectMocks
    SkillService skillService;

    @Test
    void testValidateSkillExists() {
        when(skillRepository.existsById(1L)).thenReturn(true);

        boolean result = skillService.validateSkillExists(1L);

        verify(skillRepository, times(1)).existsById(1L);
        assertTrue(result);
    }
}