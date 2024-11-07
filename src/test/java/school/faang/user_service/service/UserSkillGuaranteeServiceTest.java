package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.service.user_skill_guarantee.UserSkillGuaranteeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserSkillGuaranteeServiceTest {
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    UserSkillGuaranteeService userSkillGuaranteeService;

    private final long userId = 1L;
    private final long skillId = 101L;

    @Test
    public void testCreateGuarantee_GuaranteeDoesNotExist_CreatesNewGuarantee() {
        // Гарантия отсутствует
        when(userSkillGuaranteeRepository.existsByUserIdAndSkillId(userId, skillId)).thenReturn(false);
        when(userSkillGuaranteeRepository.findMaxGuarantorId()).thenReturn(10L); // Максимальное значение гарантии — 10
        when(userSkillGuaranteeRepository.create(userId, skillId, 11L)).thenReturn(11L); // Ожидаем создание новой с ID = 11

        Long result = userSkillGuaranteeService.createGuarantee(userId, skillId);

        assertNotNull(result);
        assertEquals(Long.valueOf(11L), result);

        verify(userSkillGuaranteeRepository).existsByUserIdAndSkillId(userId, skillId);
        verify(userSkillGuaranteeRepository).findMaxGuarantorId();
        verify(userSkillGuaranteeRepository).create(userId, skillId, 11L);
    }

    @Test
    public void testCreateGuarantee_GuaranteeAlreadyExists_ReturnsNull() {
        // Гарантия уже существует
        when(userSkillGuaranteeRepository.existsByUserIdAndSkillId(userId, skillId)).thenReturn(true);

        Long result = userSkillGuaranteeService.createGuarantee(userId, skillId);

        assertNull(result);

        verify(userSkillGuaranteeRepository).existsByUserIdAndSkillId(userId, skillId);
        verify(userSkillGuaranteeRepository, never()).findMaxGuarantorId();
        verify(userSkillGuaranteeRepository, never()).create(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testCreateGuarantee_MaxGuarantorIdIsNull_CreatesNewGuaranteeWithIdOne() {
        // `maxGuarantorId` отсутствует, и новая гарантия создается с `newGuarantorId = 1`
        when(userSkillGuaranteeRepository.existsByUserIdAndSkillId(userId, skillId)).thenReturn(false);
        when(userSkillGuaranteeRepository.findMaxGuarantorId()).thenReturn(null);
        when(userSkillGuaranteeRepository.create(userId, skillId, 1L)).thenReturn(1L);

        Long result = userSkillGuaranteeService.createGuarantee(userId, skillId);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result);

        verify(userSkillGuaranteeRepository).existsByUserIdAndSkillId(userId, skillId);
        verify(userSkillGuaranteeRepository).findMaxGuarantorId();
        verify(userSkillGuaranteeRepository).create(userId, skillId, 1L);
    }
}
