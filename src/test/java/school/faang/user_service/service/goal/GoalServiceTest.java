package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.dto.response.GoalsResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidation;
import school.faang.user_service.validation.goal.responce.ValidationResponse;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private List<GoalFilter> goalFilters;

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

        when(goalValidation.validateGoalRequest(userId, goalDTO, true)).thenReturn(validationResponse);
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

        when(goalValidation.validateGoalRequest(userId, goalDTO, true)).thenReturn(validationResponse);

        GoalResponse response = goalService.createGoal(userId, goalDTO);

        assertThat(response.getMessage()).isEqualTo("Validation failed");
        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getErrors()).isEqualTo(validationResponse.getErrors());
        verify(goalRepository, never()).save(any());
    }

    @Test
    void updateGoal_SuccessfulUpdate_ShouldReturnSuccessResponse() {
        Long userId = 1L;
        GoalDTO goalDto = new GoalDTO();
        goalDto.setId(1L);
        goalDto.setTitle("Updated Title");
        goalDto.setDescription("Updated Description");
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setSkillIds(List.of(1L));

        Goal goalEntity = new Goal();
        goalEntity.setId(1L);
        goalEntity.setTitle("Initial Title");

        var validationResponse = new ValidationResponse();
        validationResponse.setValid(true);

        when(goalValidation.validateGoalRequest(userId, goalDto, false)).thenReturn(validationResponse);
        when(goalRepository.existsById(goalDto.getId())).thenReturn(true);
        when(goalRepository.findGoalById(goalDto.getId())).thenReturn(goalEntity);
        when(userService.getUserById(userId)).thenReturn(new User());
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalResponse response = goalService.updateGoal(userId, goalDto);

        assertEquals("Goal updated successfully", response.getMessage());
        assertEquals(201, response.getCode());
        assertNotNull(response.getData());
        verify(goalRepository).updateGoal(anyLong(), anyString(), anyString(), anyInt(), any(), any(), any());
    }

    @Test
    void updateGoal_GoalDoesNotExist_ShouldReturnValidationErrorResponse() {
        Long userId = 1L;
        GoalDTO goalDto = new GoalDTO();
        goalDto.setId(1L);
        goalDto.setTitle("Updated Title");
        goalDto.setDescription("Updated Description");
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setSkillIds(List.of(1L));

        var validationResponse = new ValidationResponse();
        validationResponse.setValid(true);

        when(goalValidation.validateGoalRequest(userId, goalDto, false)).thenReturn(validationResponse);
        when(goalRepository.existsById(goalDto.getId())).thenReturn(false);

        GoalResponse response = goalService.updateGoal(userId, goalDto);

        assertEquals("Validation failed", response.getMessage());
        assertEquals(400, response.getCode());
        assertTrue(response.getErrors().contains("Goal does not exist"));
        verify(goalRepository, never()).updateGoal(anyLong(), anyString(), anyString(), anyInt(), any(), any(), any());
    }

    @Test
    void updateGoal_ValidationFails_ShouldReturnValidationErrorResponse() {
        Long userId = 1L;
        GoalDTO goalDto = new GoalDTO();
        goalDto.setId(1L);
        goalDto.setTitle("Updated Title");
        goalDto.setDescription("Updated Description");
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setSkillIds(List.of(1L));

        var validationResponse = new ValidationResponse();
        validationResponse.setValid(false);
        validationResponse.setErrors(List.of("Title is required"));

        when(goalValidation.validateGoalRequest(userId, goalDto, false)).thenReturn(validationResponse);

        GoalResponse response = goalService.updateGoal(userId, goalDto);

        assertEquals("Validation failed", response.getMessage());
        assertEquals(400, response.getCode());
        assertTrue(response.getErrors().contains("Title is required"));
        verify(goalRepository, never()).existsById(anyLong());
        verify(goalRepository, never()).updateGoal(anyLong(), anyString(), anyString(), anyInt(), any(), any(), any());
    }

    @Test
    void updateGoal_WithMentorAndParent_ShouldSetMentorAndParent() {
        long userId = 1L;
        long mentorId = 2L;
        long parentGoalId = 3L;

        GoalDTO goalDto = new GoalDTO();
        goalDto.setId(1L);
        goalDto.setTitle("Title");
        goalDto.setDescription("Description");
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setSkillIds(List.of(1L));
        goalDto.setMentorId(mentorId);
        goalDto.setParentGoalId(parentGoalId);

        Goal goalEntity = new Goal();
        goalEntity.setId(1L);

        var validationResponse = new ValidationResponse();
        validationResponse.setValid(true);

        when(goalValidation.validateGoalRequest(userId, goalDto, false)).thenReturn(validationResponse);
        when(goalRepository.existsById(goalDto.getId())).thenReturn(true);
        when(goalRepository.findGoalById(goalDto.getId())).thenReturn(goalEntity);
        when(userService.getUserById(userId)).thenReturn(new User());
        when(userService.getUserById(mentorId)).thenReturn(new User());
        when(goalRepository.findGoalById(parentGoalId)).thenReturn(new Goal());
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalResponse response = goalService.updateGoal(userId, goalDto);

        assertEquals("Goal updated successfully", response.getMessage());
        assertEquals(201, response.getCode());
        verify(goalRepository).updateGoal(anyLong(), anyString(), anyString(), anyInt(), any(), any(), any());
        assertNotNull(goalEntity.getMentor());
        assertNotNull(goalEntity.getParent());
    }

    @Test
    void deleteGoal_GoalExists_ShouldReturnSuccessResponse() {
        long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findGoalById(goalId)).thenReturn(goal);

        GoalResponse response = goalService.deleteGoal(goalId);

        assertEquals("Goal deleted successfully", response.getMessage());
        assertEquals(204, response.getCode());
        verify(goalRepository).delete(goal);
    }

    @Test
    void deleteGoal_GoalDoesNotExist_ShouldReturnValidationErrorResponse() {
        long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);

        GoalResponse response = goalService.deleteGoal(goalId);

        assertEquals("Validation failed", response.getMessage());
        assertEquals(400, response.getCode());
        assertTrue(response.getErrors().contains("Goal does not exist"));
        verify(goalRepository, never()).delete(any());
    }


    @Test
    public void testGetGoalsByUser_UserNotFound() {
        long userId = 1L;
        when(userService.checkIfUserExistsById(userId)).thenReturn(false);

        GoalsResponse response = goalService.getGoalsByUser(userId, new GoalFilterDto());

        assertEquals("Validation failed", response.getMessage());
        assertEquals(400, response.getCode());
        assertTrue(response.getErrors().contains("Goal does not exist"));
        verify(goalRepository, never()).findAll();
    }

    @Test
    public void testGetGoalsByUser_UserFoundWithGoals() {
        long userId = 1L;
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("test");

        GoalFilter filters = new GoalTitleFilter();

        when(userService.checkIfUserExistsById(userId)).thenReturn(true);

        goalFilters.add(filters);

        Goal goal1 = new Goal();
        goal1.setTitle("test");
        Goal goal2 = new Goal();
        goal2.setTitle("test");
        List<Goal> goalList = List.of(goal1, goal2);

        when(goalRepository.findAll()).thenReturn(goalList);
        when(goalMapper.toDto(goal1)).thenReturn(new GoalDTO());
        when(goalMapper.toDto(goal2)).thenReturn(new GoalDTO());


        GoalsResponse response = goalService.getGoalsByUser(userId, filterDto);

        assertEquals("Goal fetched successfully", response.getMessage());
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        verify(goalRepository, times(1)).findAll();
    }
}