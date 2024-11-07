package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testAcceptGoalInvitationPositive() {
        long invitationId = 1L;
        GoalInvitationDto expectedDto = GoalInvitationDto.builder().id(invitationId).build();
        when(goalInvitationService.acceptGoalInvitation(invitationId)).thenReturn(expectedDto);

        GoalInvitationDto result = goalInvitationController.acceptGoalInvitation(invitationId);

        verify(goalInvitationValidator).validateId(invitationId);
        verify(goalInvitationService).acceptGoalInvitation(invitationId);
        assertEquals(expectedDto, result);
    }

    @Test
    public void testAcceptGoalInvitationNegative() {
        long invalidId = -1L;
        when(goalInvitationService.acceptGoalInvitation(invalidId)).thenThrow(new DataValidationException("Invalid ID"));

        assertThrows(DataValidationException.class, () -> goalInvitationController.acceptGoalInvitation(invalidId));
        verify(goalInvitationService, times(1)).acceptGoalInvitation(invalidId);
    }

    @Test
    public void testRejectGoalInvitationPositive() {
        long invitationId = 1L;
        GoalInvitationDto expectedDto = GoalInvitationDto.builder()
                .id(invitationId)
                .build();
        when(goalInvitationService.rejectGoalInvitation(invitationId)).thenReturn(expectedDto);

        GoalInvitationDto result = goalInvitationController.rejectGoalInvitation(invitationId);

        verify(goalInvitationValidator).validateId(invitationId);
        verify(goalInvitationService).rejectGoalInvitation(invitationId);
        assertEquals(expectedDto, result);
    }

    @Test
    public void testRejectGoalInvitationNegative() {
        long invalidId = -1L;
        when(goalInvitationService.rejectGoalInvitation(invalidId)).thenThrow(new DataValidationException("Invalid ID"));

        assertThrows(DataValidationException.class, () -> goalInvitationController.rejectGoalInvitation(invalidId));
        verify(goalInvitationService, times(1)).rejectGoalInvitation(invalidId);
    }

    @Test
    public void testGetInvitations() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder().build();
        List<GoalInvitationDto> goalInvitationDtoList = new ArrayList<>(List.of(GoalInvitationDto.builder().build()));
        when(goalInvitationService.getInvitations(goalInvitationFilterDto))
                .thenReturn(goalInvitationDtoList);

        List<GoalInvitationDto> result = goalInvitationController.getInvitations(goalInvitationFilterDto);
        verify(goalInvitationService, times(1)).getInvitations(goalInvitationFilterDto);
        assertEquals(goalInvitationDtoList, result);
    }
}
