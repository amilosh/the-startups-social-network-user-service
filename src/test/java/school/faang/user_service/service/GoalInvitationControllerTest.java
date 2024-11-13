package school.faang.user_service.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.GoalInvitationResponseDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;


@WebMvcTest(GoalInvitationController.class)
public class GoalInvitationControllerTest {
    @Mock
    private GoalInvitationService service;
    @InjectMocks
    private GoalInvitationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCreateInvitationWhenInviterAndInviteeTheSamePerson() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(2L);
        goalInvitationDto.setInvitedUserId(2L);
        when(service.createInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);

        mockMvc.perform(post("/GoalInvitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviterId\": 2, \"invitedUserId\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inviterId").value(2L))
                .andExpect(jsonPath("$.invitedUserId").value(2L));
    }
    @Test
    public void testCreateWhenUserNotExist() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        when(service.createInvitation(null)).thenReturn(goalInvitationDto);
    }

    @Test
    public void testCreateSaveUser() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        when(service.createInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);
    }
    @Test
    public void testAcceptGoalInvitation() throws Exception {
        long id = 1L;
        GoalInvitationResponseDto responseDto = new GoalInvitationResponseDto();
        when(service.acceptGoalInvitation(id)).thenReturn(responseDto);
    }

    @Test
    public void testAcceptNotFoundGoalInvitation() throws Exception {
        Long id = null;
        when(service.rejectGoalInvitation(id)).thenReturn(false);
    }

    @Test
    public void testAcceptGoalInvitationWhenMoreThanThreeActiveGoals() throws Exception {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(2);
        List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();
        receivedGoalInvitations.add(new GoalInvitation());
        receivedGoalInvitations.add(new GoalInvitation());
        receivedGoalInvitations.add(new GoalInvitation());
        goalInvitation.getInvited().setReceivedGoalInvitations(receivedGoalInvitations);
        when(service.rejectGoalInvitation(2)).thenReturn(false);
    }

    @Test
    public void testAcceptAlreadyHaveSuchGoalInvitation() throws Exception {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(2);
        goalInvitation.setInvited(new User());
        goalInvitation.getInvited().getReceivedGoalInvitations().add(goalInvitation);
        when(service.rejectGoalInvitation(2)).thenReturn(false);
    }

    @Test
    public void testAcceptInvitation() throws Exception {
        Long id = 2L;
        when(service.rejectGoalInvitation(id)).thenReturn(true);
    }

    @Test
    public void testRejectGoalInvitationNotFound() throws Exception {
        Long id = null;
        when(service.rejectGoalInvitation(id)).thenReturn(false);
    }

    @Test
    public void testRejectGoalInvitation() throws Exception {
        long id = 1L;
        when(service.rejectGoalInvitation(id)).thenReturn(true);
    }

    @Test
    public void testGetInvitations() throws Exception {
        GoalInvitationFilterDto filterDto = new GoalInvitationFilterDto();
        List<GoalInvitation> goalInvitations = Collections.singletonList(new GoalInvitation());
        when(service.getInvitationsByFilter(any(GoalInvitationFilterDto.class))).thenReturn(goalInvitations);
    }
}
