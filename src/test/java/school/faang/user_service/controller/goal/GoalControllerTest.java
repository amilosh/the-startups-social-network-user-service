package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    @Mock
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
        lenient().when(userContext.getUserId()).thenReturn(1L);
    }

    @Test
    void createGoal_ShouldReturnCreatedGoal() throws Exception {
        GoalRequestDto goalRequestDto = new GoalRequestDto();
        goalRequestDto.setParentId(3L);
        goalRequestDto.setTitle("New Goal");
        goalRequestDto.setDescription("Goal Description");
        goalRequestDto.setStatus(GoalStatus.ACTIVE);
        goalRequestDto.setSkillIds(Arrays.asList(101L, 102L));

        GoalResponseDto goalResponseDto = new GoalResponseDto();
        goalResponseDto.setId(1L);
        goalResponseDto.setParentId(3L);
        goalResponseDto.setTitle("New Goal");
        goalResponseDto.setDescription("Goal Description");
        goalResponseDto.setStatus(GoalStatus.ACTIVE);
        goalResponseDto.setSkillIds(Arrays.asList(101L, 102L));

        when(goalService.createGoal(eq(1L), any(GoalRequestDto.class)))
                .thenReturn(goalResponseDto);

        mockMvc.perform(post("/api/v1/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.parentId").value(3L))
                .andExpect(jsonPath("$.title").value("New Goal"))
                .andExpect(jsonPath("$.description").value("Goal Description"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.skillIds[0]").value(101L))
                .andExpect(jsonPath("$.skillIds[1]").value(102L));
    }

    @Test
    void updateGoal_ShouldReturnUpdatedGoal() throws Exception {
        Long goalId = 1L;
        GoalRequestDto goalRequestDto = new GoalRequestDto();
        goalRequestDto.setParentId(3L);
        goalRequestDto.setTitle("Updated Goal");
        goalRequestDto.setDescription("Updated Description");
        goalRequestDto.setStatus(GoalStatus.COMPLETED);
        goalRequestDto.setSkillIds(Collections.singletonList(103L));

        GoalResponseDto goalResponseDto = new GoalResponseDto();
        goalResponseDto.setId(goalId);
        goalResponseDto.setParentId(3L);
        goalResponseDto.setTitle("Updated Goal");
        goalResponseDto.setDescription("Updated Description");
        goalResponseDto.setStatus(GoalStatus.COMPLETED);
        goalResponseDto.setSkillIds(Collections.singletonList(103L));

        when(goalService.updateGoal(eq(goalId), any(GoalRequestDto.class)))
                .thenReturn(goalResponseDto);

        mockMvc.perform(put("/api/v1/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goalId))
                .andExpect(jsonPath("$.parentId").value(3L))
                .andExpect(jsonPath("$.title").value("Updated Goal"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.skillIds[0]").value(103L));
    }

    @Test
    void deleteGoal_ShouldReturnNoContent() throws Exception {
        Long goalId = 1L;

        mockMvc.perform(delete("/api/v1/goals/{goalId}", goalId))
                .andExpect(status().isNoContent());
    }

    @Test
    void findSubtasksByGoalId_ShouldReturnSubtasks() throws Exception {
        Long goalId = 1L;

        GoalResponseDto subtask = new GoalResponseDto();
        subtask.setId(2L);
        subtask.setParentId(goalId);
        subtask.setTitle("Subtask");
        subtask.setDescription("Subtask Description");
        subtask.setStatus(GoalStatus.ACTIVE);
        subtask.setSkillIds(Arrays.asList(201L, 202L));

        List<GoalResponseDto> subtasks = Collections.singletonList(subtask);

        when(goalService.findSubtasksByGoalId(eq(goalId), any()))
                .thenReturn(subtasks);

        mockMvc.perform(get("/api/v1/goals/{goalId}/subtasks", goalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].parentId").value(goalId))
                .andExpect(jsonPath("$[0].title").value("Subtask"))
                .andExpect(jsonPath("$[0].description").value("Subtask Description"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].skillIds[0]").value(201L))
                .andExpect(jsonPath("$[0].skillIds[1]").value(202L));
    }

    @Test
    void getGoalsByUser_ShouldReturnGoals() throws Exception {
        Long userId = 1L;

        GoalResponseDto goal = new GoalResponseDto();
        goal.setId(1L);
        goal.setParentId(3L);
        goal.setTitle("User Goal");
        goal.setDescription("User Goal Description");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillIds(Collections.singletonList(301L));

        List<GoalResponseDto> goals = Collections.singletonList(goal);

        when(goalService.getGoalsByUser(eq(userId), any()))
                .thenReturn(goals);

        mockMvc.perform(get("/api/v1/goals/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].parentId").value(3L))
                .andExpect(jsonPath("$[0].title").value("User Goal"))
                .andExpect(jsonPath("$[0].description").value("User Goal Description"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].skillIds[0]").value(301L));
    }
}