package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private GoalInvitationValidator goalInvitationValidator;

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto dto;
    private GoalInvitation goalInvitation;
    private User inviter;
    private User invited;
    private Goal goal;

    @Test
    public void testCreateInvitation() {
        preparationData();
        when(goalInvitationMapper.toEntity(any())).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any())).thenReturn(dto);
        when(userService.findUserById(dto.getInviterId())).thenReturn(inviter);
        when(userService.findUserById(dto.getInvitedUserId())).thenReturn(invited);
        when(goalService.findGoalById(dto.getGoalId())).thenReturn(goal);
        when(goalInvitationRepository.save(any())).thenReturn(any());

        GoalInvitationDto result = goalInvitationService.createInvitation(dto);

        System.out.println(result);
        assertEquals(dto, result);
        verify(goalInvitationMapper, times(1)).toEntity(any());
        verify(userService, times(1)).findUserById(dto.getInviterId());
        verify(userService, times(1)).findUserById(dto.getInvitedUserId());
        verify(goalService, times(1)).findGoalById(dto.getGoalId());
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalInvitationMapper, times(1)).toDto(any());
    }

    @Test
    public void testAcceptInvitation() {

    }


    public void preparationData() {
        dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
        goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.PENDING);
        inviter = User.builder()
                .id(1L)
                .build();
        invited = User.builder()
                .id(2L)
                .build();
        goal = Goal.builder()
                .id(1L)
                .build();
    }
}
