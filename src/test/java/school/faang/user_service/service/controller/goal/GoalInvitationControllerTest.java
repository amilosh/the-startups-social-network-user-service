package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.GoalInvitationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    @InjectMocks
    private GoalInvitationController invitationController;

    @Mock
    private GoalInvitationService invitationService;

    @Test
    void testCreateInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setGoalId(3L);
        goalInvitationDto.setStatus(RequestStatus.PENDING);
        when(invitationService.creatInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);

        GoalInvitationDto result = invitationController.createInvitation(goalInvitationDto);

        verify(invitationService, times(1)).creatInvitation(goalInvitationDto);
        assertEquals(goalInvitationDto, result);
    }

    @Test
    void testAcceptGoalInvitation() {
        long id = 1L;

        invitationController.acceptGoalInvitation(id);

        verify(invitationService, times(1)).acceptGoalInvitation(id);
    }

    @Test
    void testRejectGoalInvitation() {
        long id = 1L;

        invitationController.rejectGoalInvitation(id);

        verify(invitationService, times(1)).rejectGoalInvitation(id);
    }
}
