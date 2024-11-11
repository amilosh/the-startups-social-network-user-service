package school.faang.user_service.service;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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


@WebMvcTest(GoalInvitationController.class)
public class GoalInvitationControllerTest {
    @Mock
    private GoalInvitationService service;
    @InjectMocks
    private GoalInvitationController controller;

    @Test
    public void testCreateInvitationWhenInviterAndInviteeTheSamePerson() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(2L);
        goalInvitationDto.setInvitedUserId(2L);
        when(service.createInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);

    }
    @Test
    public void testCreateWhenUserNotExist() throws Exception {
        GoalInvitationDto goalInvitationDto = null;
        when(service.createInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);
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
