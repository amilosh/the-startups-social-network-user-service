package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationDtoValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @Mock
    private GoalInvitationDtoValidator goalInvitationDtoValidator;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    private GoalInvitationDto dto;

    @BeforeEach
    public void setUp() {
        dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
    }

    @Test
    public void testCreateInvitation() {
        doNothing().when(goalInvitationDtoValidator).validate(dto);

        when(goalInvitationService.createInvitation(dto)).thenReturn(dto);

        GoalInvitationDto result = goalInvitationController.createInvitation(dto);

        verify(goalInvitationDtoValidator, times(1)).validate(dto);
        verify(goalInvitationService, times(1)).createInvitation(dto);

        assertEquals(dto, result);
    }
}
