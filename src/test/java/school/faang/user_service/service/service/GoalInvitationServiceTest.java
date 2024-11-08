package school.faang.user_service.service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    private GoalInvitationService invitationService;

    @Mock
    private GoalInvitationRepository invitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalMapperImpl mapper;
    private GoalInvitationDto goalInvitationDto;
    private GoalInvitation invitation;

    @BeforeEach
    void setUp() {
        goalInvitationDto = new GoalInvitationDto();
        invitation = new GoalInvitation();
    }

    @Test
    void testCreateInvitation() {
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setGoalId(3L);
        goalInvitationDto.setStatus(RequestStatus.PENDING);

        User inviter = new User();
        inviter.setId(1L);

        User invitedUser = new User();
        invitedUser.setId(2L);

        Goal goal = new Goal();
        goal.setId(3L);

        invitation.setId(1L);
        invitation.setInviter(inviter);
        invitation.setInvited(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(RequestStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(goalRepository.findById(3L)).thenReturn(Optional.of(goal));
        when(invitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        GoalInvitationDto result = invitationService.creatInvitation(goalInvitationDto);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(goalRepository, times(1)).findById(3L);
        verify(invitationRepository, times(1)).save(any(GoalInvitation.class));
        
        assertEquals(goalInvitationDto, result);
    }

    @Test
    void testAcceptGoalInvitation() {
        when(invitationRepository.getReferenceById(invitation.getId())).thenReturn(invitation);

        invitationService.acceptGoalInvitation(invitation.getId());

        verify(invitationRepository, times(1)).getReferenceById(invitation.getId());
        verify(invitationRepository, times(1)).save(invitation);
    }

    @Test
    void testRejectGoalInvitation() {
        invitation.setId(1L);
        when(invitationRepository.getReferenceById(invitation.getId())).thenReturn(invitation);

        invitationService.rejectGoalInvitation(invitation.getId());

        verify(invitationRepository, times(1)).save(invitation);
    }

    @Test
    void testGetInvitation() {

    }
}
