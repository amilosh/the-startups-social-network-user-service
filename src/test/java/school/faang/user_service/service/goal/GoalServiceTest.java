package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidation;
import school.faang.user_service.validation.goal.responce.ValidationResponse;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserService userService;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalValidation goalValidation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGoal_Success() {
        Long userId = 1L;
        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setSkillIds(List.of(2L, 3L));

        Goal goalEntity = new Goal();
        var validationResponse = new ValidationResponse();
        validationResponse.setValid(true);
        validationResponse.setErrors(List.of());

        when(goalValidation.validateCreateGoalRequest(userId, goalDTO)).thenReturn(validationResponse);
        when(userService.getUserById(userId)).thenReturn(new User());
        when(goalMapper.toEntity(goalDTO)).thenReturn(goalEntity);
        when(skillService.getSkillById(2L)).thenReturn(new Skill());
        when(skillService.getSkillById(3L)).thenReturn(new Skill());

        GoalResponse response = goalService.createGoal(userId, goalDTO);

        assertThat(response.getMessage()).isEqualTo("Goal created successfully");
        assertThat(response.getCode()).isEqualTo(201);
        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    void testCreateGoal_ValidationFailed() {
        Long userId = 1L;
        GoalDTO goalDTO = new GoalDTO();

        var validationResponse = new ValidationResponse();
        validationResponse.setValid(false);
        validationResponse.setErrors(List.of("Invalid data"));

        when(goalValidation.validateCreateGoalRequest(userId, goalDTO)).thenReturn(validationResponse);

        GoalResponse response = goalService.createGoal(userId, goalDTO);

        assertThat(response.getMessage()).isEqualTo("Validation failed");
        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getErrors()).isEqualTo(validationResponse.getErrors());
        verify(goalRepository, never()).save(any());
    }
}