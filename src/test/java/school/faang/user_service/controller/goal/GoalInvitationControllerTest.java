package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @Mock
    private GoalInvitationValidator goalInvitationValidator;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Test
    @DisplayName("Create Invitation controller test")
    public void testCreateInvitation() {
        GoalInvitationDto dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
        doNothing().when(goalInvitationValidator).validateDto(dto);
        when(goalInvitationService.createInvitation(dto)).thenReturn(dto);

        GoalInvitationDto result = goalInvitationController.createInvitation(dto);

        verify(goalInvitationValidator, times(1)).validateDto(dto);
        verify(goalInvitationService, times(1)).createInvitation(dto);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Accept Invitation controller test")
    public void testAcceptGoalInvitationPositive() {
        long validId = 1L;
        doNothing().when(goalInvitationValidator).validateId(validId);

        goalInvitationController.acceptGoalInvitation(validId);

        verify(goalInvitationValidator, times(1)).validateId(validId);
        verify(goalInvitationService, times(1)).acceptGoalInvitation(validId);
    }

    @Test
    @DisplayName("Accept Invitation controller test")
    public void testAcceptGoalInvitationNegative() {
        long validId = 1L;

        goalInvitationController.acceptGoalInvitation(validId);

        verify(goalInvitationService, times(1)).acceptGoalInvitation(validId);
    }

    @Test
    @DisplayName("Reject Invitation controller test")
    public void testRejectGoalInvitation() {
        long validId = 1L;
        doNothing().when(goalInvitationValidator).validateId(validId);

        goalInvitationController.rejectGoalInvitation(validId);

        verify(goalInvitationValidator, times(1)).validateId(validId);
        verify(goalInvitationService, times(1)).rejectGoalInvitation(validId);
    }
}
