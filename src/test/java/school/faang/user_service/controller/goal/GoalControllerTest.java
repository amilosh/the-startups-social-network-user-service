package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.request.CreateGoalRequest;
import school.faang.user_service.dto.request.GetGoalsByFilterRequest;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.dto.response.GoalsResponse;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoalControllerTest {
    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateGoal_Success() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setUserId(1L);
        request.setGoal(new GoalDTO());

        GoalResponse goalResponse = new GoalResponse(
                "Goal created successfully",
                201
        );

        when(goalService.createGoal(1L, request.getGoal())).thenReturn(goalResponse);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Goal created successfully"))
                .andExpect(jsonPath("$.code").value(201));
    }

    @Test
    void testCreateGoal_ValidationFailed() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setUserId(1L);
        request.setGoal(new GoalDTO());

        GoalResponse goalResponse = new GoalResponse(
                "Validation failed",
                400
        );

        when(goalService.createGoal(1L, request.getGoal())).thenReturn(goalResponse);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void updateGoal_WhenUpdateIsSuccessful_Returns201() throws Exception {
        var goal = new GoalDTO();
        goal.setId(1L);
        goal.setTitle("New Title");
        goal.setDescription("New Description");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillIds(List.of(1L));

        CreateGoalRequest request = new CreateGoalRequest();
        request.setUserId(1L);
        request.setGoal(goal);

        GoalResponse goalResponse = new GoalResponse("Goal updated successfully", 201);
        goalResponse.setCode(201);
        goalResponse.setData(goal);

        when(goalService.updateGoal(any(Long.class), any(goal.getClass()))).thenReturn(goalResponse);

        mockMvc.perform(put("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(201));
    }

    @Test
    void updateGoal_WhenValidationFails_Returns400() throws Exception {
        var goal = new GoalDTO();

        CreateGoalRequest request = new CreateGoalRequest();
        request.setUserId(1L);
        request.setGoal(goal);

        GoalResponse goalResponse = new GoalResponse(
                "Validation error",
                400
        );
        goalResponse.setErrors(List.of("Validation error"));

        when(goalService.updateGoal(any(Long.class), any(goal.getClass()))).thenReturn(goalResponse);

        mockMvc.perform(put("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors[0]").value("Validation error"));
    }

    @Test
    void deleteGoal_WhenDeletionIsSuccessful_Returns204() throws Exception {
        long goalId = 1L;
        GoalResponse goalResponse = new GoalResponse(
                "Goal deleted successfully",
                204
        );

        when(goalService.deleteGoal(anyLong())).thenReturn(goalResponse);

        mockMvc.perform(delete("/api/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(
                        "{\"message\":\"Goal deleted successfully\",\"code\":204,\"data\":null,\"errors\":[]}"
                ));
    }

    @Test
    void deleteGoal_WhenGoalDoesNotExist_Returns400() throws Exception {
        long goalId = 1L;
        GoalResponse goalResponse = new GoalResponse(
                "Validation error",
                400
        );
        goalResponse.setErrors(List.of("Goal does not exist"));

        when(goalService.deleteGoal(anyLong())).thenReturn(goalResponse);

        mockMvc.perform(delete("/api/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors[0]").value("Goal does not exist"));
    }

    @Test
    void getGoalsByUser_WhenSuccessful_Returns200() throws Exception {
        long userId = 1L;
        GetGoalsByFilterRequest filters = new GetGoalsByFilterRequest();

        var goalFilterDto = new GoalFilterDto();
        goalFilterDto.setTitle("Sample Title");

        filters.setFilters(goalFilterDto);

        GoalsResponse goalsResponse = new GoalsResponse(
                "Goals retrieved successfully",
                200
        );
        goalsResponse.setData(List.of());

        when(goalService.getGoalsByUser(anyLong(), any())).thenReturn(goalsResponse);

        mockMvc.perform(post("/api/goals/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filters)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getGoalsByUser_WhenFiltersAreInvalid_Returns400() throws Exception {
        long userId = 1L;
        GetGoalsByFilterRequest filters = new GetGoalsByFilterRequest();
        filters.setFilters(new GoalFilterDto());

        GoalsResponse goalsResponse = new GoalsResponse(
                "Validation failed",
                400
        );
        goalsResponse.setErrors(List.of("Invalid filters"));

        when(goalService.getGoalsByUser(anyLong(), any())).thenReturn(goalsResponse);

        mockMvc.perform(post("/api/goals/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filters)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors[0]").value("Invalid filters"));
    }
}