package school.faang.user_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.request.CreateGoalRequest;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateGoalSuccess() throws Exception {
        Long userId = 1L;
        GoalDto goalDTO = new GoalDto();
        goalDTO.setTitle("New Goal");
        CreateGoalRequest request = new CreateGoalRequest(userId, goalDTO);

        when(goalService.createGoal(userId, goalDTO)).thenReturn(goalDTO);

        mockMvc.perform(post("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("New Goal"));

        verify(goalService, times(1)).createGoal(userId, goalDTO);
    }

    @Test
    void testUpdateGoalSuccess() throws Exception {
        Long userId = 1L;
        GoalDto goalDTO = new GoalDto();
        goalDTO.setId(1L);
        goalDTO.setTitle("Updated Goal Title");
        CreateGoalRequest request = new CreateGoalRequest(userId, goalDTO);

        when(goalService.updateGoal(userId, goalDTO)).thenReturn(goalDTO);

        mockMvc.perform(put("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("Updated Goal Title"));

        verify(goalService, times(1)).updateGoal(userId, goalDTO);
    }

    @Test
    void testDeleteGoalSuccess() throws Exception {
        Long goalId = 1L;

        doNothing().when(goalService).deleteGoal(goalId);

        mockMvc.perform(delete("/goals/{goalId}", goalId))
                .andExpect(status().isNoContent());

        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    void testGetGoalsByUserSuccess() throws Exception {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        GoalDto goal1 = new GoalDto();
        goal1.setTitle("Goal 1");
        GoalDto goal2 = new GoalDto();
        goal2.setTitle("Goal 2");

        when(goalService.getGoalsByUser(userId, filters)).thenReturn(List.of(goal1, goal2));

        mockMvc.perform(post("/goals/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Goal 1"))
                .andExpect(jsonPath("$[1].title").value("Goal 2"));

        verify(goalService, times(1)).getGoalsByUser(userId, filters);
    }
}