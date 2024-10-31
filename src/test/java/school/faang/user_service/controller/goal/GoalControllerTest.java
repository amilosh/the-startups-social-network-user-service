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
import school.faang.user_service.dto.request.CreateGoalRequest;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.service.goal.GoalService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoalControllerTest {
    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
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
}