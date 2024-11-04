package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @InjectMocks
    private GoalValidator goalValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillService skillService;

    private GoalDTO goal;

    @BeforeEach
    void setUp() {
        goal = new GoalDTO();
        goal.setId(1L);
        goal.setTitle("Test Goal");
        goal.setDescription("This is a test goal");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillIds(List.of(1L, 2L, 3L));
    }

    @Test
    public void testUserIdIsMissing() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(0L, goal, true)
        );

        assertEquals(exception.getMessage(), "User ID is missing");
    }

    @Test
    public void testUserDoesNotExist() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(999L, goal, true)
        );

        assertEquals(exception.getMessage(), "User does not exist");
    }

    @Test
    public void testGoalIsMissing() {
        when(userRepository.existsById(1L)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, null, true)
        );

        assertEquals(exception.getMessage(), "Goal is missing");
    }

    @Test
    public void testGoalTitleIsMissingWithNull() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setTitle(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Goal title is missing");
    }

    @Test
    public void testGoalTitleIsMissingWithEmpty() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setTitle("");

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Goal title is missing");
    }

    @Test
    public void testGoalTitleIsTooLong() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setTitle("a".repeat(100));

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Goal title is too long");
    }

    @Test
    public void testGoalDescriptionIsMissingWithNull() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setDescription(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Goal description is missing");
    }

    @Test
    public void testGoalDescriptionIsTooLong() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setDescription("a".repeat(200));

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Goal description is too long");
    }

    @Test
    public void testGoalStatusIsMissingWithNull() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setStatus(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Goal status is missing");
    }

    @Test
    public void testGoalIdDoesNotExistWithNull() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, false)
        );

        assertEquals(exception.getMessage(), "Goal ID does not exist");
    }

    @Test
    public void testGoalDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        goal.setId(999L);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, false)
        );

        assertEquals(exception.getMessage(), "Goal does not exist");
    }

    @Test
    public void testUserAlreadyHasGoal() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.existsById(goal.getId())).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(10);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "User already has the maximum number of active goals");
    }

    @Test
    public void testSkillIdsAreMissing() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.existsById(goal.getId())).thenReturn(true);

        goal.setSkillIds(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "Skill IDs are missing");
    }

    @Test
    public void testOneOfSkillIdDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.existsById(goal.getId())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                goalValidator.validateGoalRequest(1L, goal, true)
        );

        assertEquals(exception.getMessage(), "One of the skill IDs does not exist");
    }
}
