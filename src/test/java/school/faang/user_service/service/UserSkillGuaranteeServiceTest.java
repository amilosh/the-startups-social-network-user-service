package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.service.user_skill_guarantee.UserSkillGuaranteeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserSkillGuaranteeServiceTest {
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    UserSkillGuaranteeService userSkillGuaranteeService;

    private final long userId = 1L;
    private final long skillId = 101L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateGuarantee_GuaranteeDoesNotExist_CreatesNewGuarantee() {
        when(userSkillGuaranteeRepository.findMaxGuarantorId()).thenReturn(10L);
        when(userSkillGuaranteeRepository.create(userId, skillId, 11L)).thenReturn(11L);

        Long result = userSkillGuaranteeService.createGuarantee(userId, skillId);

        assertNotNull(result);
        assertEquals(Long.valueOf(11L), result);

        verify(userSkillGuaranteeRepository).findMaxGuarantorId();
        verify(userSkillGuaranteeRepository).create(userId, skillId, 11L);
    }

    @Test
    public void testCreateGuarantee_MaxGuarantorIdIsNull_CreatesNewGuaranteeWithIdOne() {
        when(userSkillGuaranteeRepository.findMaxGuarantorId()).thenReturn(null);
        when(userSkillGuaranteeRepository.create(userId, skillId, 1L)).thenReturn(1L);

        Long result = userSkillGuaranteeService.createGuarantee(userId, skillId);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result);

        verify(userSkillGuaranteeRepository).findMaxGuarantorId();
        verify(userSkillGuaranteeRepository).create(userId, skillId, 1L);
    }
}