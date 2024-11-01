package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.goal.responce.ValidationResponse;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GoalValidationTest {
    @InjectMocks
    private GoalValidation goalValidation;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateGoalRequest_UserIdMissing() {
        GoalDTO goal = new GoalDTO();
        ValidationResponse response = goalValidation.validateGoalRequest(0L, goal);

        assertFalse(response.isValid());
        assertTrue(response.getErrors().contains("User ID is missing"));
    }

    @Test
    void testValidateGoalRequest_UserDoesNotExist() {
        GoalDTO goal = new GoalDTO();
        when(userRepository.existsById(1L)).thenReturn(false);

        ValidationResponse response = goalValidation.validateGoalRequest(1L, goal);

        assertFalse(response.isValid());
        assertTrue(response.getErrors().contains("User does not exist"));
    }

    @Test
    void testValidateGoalRequest_GoalIsMissing() {
        when(userRepository.existsById(1L)).thenReturn(true);

        ValidationResponse response = goalValidation.validateGoalRequest(1L, null);

        assertFalse(response.isValid());
        assertTrue(response.getErrors().contains("Goal is missing"));
    }

    @Test
    void testValidateGoalRequest_TitleIsMissing() {
        GoalDTO goal = new GoalDTO();
        goal.setTitle(null);
        when(userRepository.existsById(1L)).thenReturn(true);

        ValidationResponse response = goalValidation.validateGoalRequest(1L, goal);

        assertFalse(response.isValid());
        assertTrue(response.getErrors().contains("Goal title is missing"));
    }

    @Test
    void testValidateGoalRequest_TitleIsTooLong() {
        GoalDTO goal = new GoalDTO();
        goal.setTitle("A".repeat(65));
        when(userRepository.existsById(1L)).thenReturn(true);

        ValidationResponse response = goalValidation.validateGoalRequest(1L, goal);

        assertFalse(response.isValid());
        assertTrue(response.getErrors().contains("Goal title is too long"));
    }

    @Test
    void testValidateGoalRequest_Success() {
        GoalDTO goal = new GoalDTO();
        goal.setTitle("Valid Title");
        goal.setDescription("Valid Description");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillIds(Arrays.asList(1L, 2L));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(1);
        when(skillService.checkIfSkillExistsById(any(Long.class))).thenReturn(true);

        ValidationResponse response = goalValidation.validateGoalRequest(1L, goal);

        assertTrue(response.isValid());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    void testValidateGoalRequest_SkillDoesNotExist() {
        GoalDTO goal = new GoalDTO();
        goal.setTitle("Valid Title");
        goal.setSkillIds(Arrays.asList(1L, 999L)); // 999L не существует
        when(userRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(1);
        when(skillService.checkIfSkillExistsById(999L)).thenReturn(false);

        ValidationResponse response = goalValidation.validateGoalRequest(1L, goal);

        assertFalse(response.isValid());
        assertTrue(response.getErrors().contains("Skill with ID: 999 does not exist"));
    }
}