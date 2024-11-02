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
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto dto;
    private GoalInvitation goalInvitation;

    @BeforeEach
    public void setUp() {
        dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
        goalInvitation = goalInvitationMapper.toEntity(dto);
    }

    @Test
    public void testCreateInvitation() {
        Mockito.when(userService.findUserById(dto.getInviterId())).thenReturn(goalInvitation.getInviter());
        Mockito.when(userService.findUserById(dto.getInvitedUserId())).thenReturn(goalInvitation.getInvited());
        Mockito.when(goalService.findGoalById(dto.getGoalId())).thenReturn(goalInvitation.getGoal());
        Mockito.when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.createInvitation(dto);

        verify(userService, times(1)).findUserById(dto.getInviterId());
        verify(userService, times(1)).findUserById(dto.getInvitedUserId());
        verify(goalService, times(1)).findGoalById(dto.getGoalId());
        verify(goalInvitationRepository, times(1)).save(goalInvitation);

        assertEquals(dto, result);
    }
}
