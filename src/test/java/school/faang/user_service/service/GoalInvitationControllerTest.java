package school.faang.user_service.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.GoalInvitationResponseDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.slf4j.MDC.get;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.http.RequestEntity.put;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GoalInvitationController.class)
public class GoalInvitationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private GoalInvitationService service;
    @InjectMocks
    private GoalInvitationController controller;
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    public void testCreateInvitation() throws Exception {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        when(service.createInvitation(any(GoalInvitationDto.class))).thenReturn(invitationDto);

        mockMvc.perform(post("/GoalInvitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invitationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldName").value("expectedValue"));
    }
    @Test
    public void testAcceptGoalInvitation() throws Exception {
        long id = 1L;
        GoalInvitationResponseDto responseDto = new GoalInvitationResponseDto();
        when(service.acceptGoalInvitation(id)).thenReturn(responseDto);

        mockMvc.perform(put("/GoalInvitation/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldName").value("expectedValue"));
    }

    @Test
    public void testRejectGoalInvitation() throws Exception {
        long id = 1L;
        when(service.rejectGoalInvitation(id)).thenReturn(true);

        mockMvc.perform(delete("/GoalInvitation/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"Invitation declined.\"}"));
    }

    @Test
    public void testRejectGoalInvitationNotFound() throws Exception {
        long id = 1L;
        when(service.rejectGoalInvitation(id)).thenReturn(false);

        mockMvc.perform(delete("/GoalInvitation/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    public void testGetInvitations() throws Exception {
        GoalInvitationFilterDto filterDto = new GoalInvitationFilterDto();
        List<GoalInvitation> goalInvitations = Collections.singletonList(new GoalInvitation());
        when(service.getInvitationsByFilter(any(GoalInvitationFilterDto.class))).thenReturn(goalInvitations);

        mockMvc.perform(get("/GoalInvitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
