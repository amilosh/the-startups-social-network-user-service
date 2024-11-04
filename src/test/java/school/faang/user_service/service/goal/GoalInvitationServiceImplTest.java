package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.InvitationCheckException;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.exception.goal.UserNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;


import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceImplTest {
    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    private User inviter;
    private User invitedUser;
    private Goal goal;
    private GoalInvitationDto invitationDto;
    private GoalInvitation goalInvitation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        inviter = new User(); // инициализация объекта User
        inviter.setId(1L);

        invitedUser = new User(); // инициализация объекта User
        invitedUser.setId(2L);

        goal = new Goal(); // инициализация объекта Goal
        goal.setId(3L);

        invitationDto = new GoalInvitationDto(1L, 2L, 3L); // инициаторы DTO
        goalInvitation = new GoalInvitation(); // инициализация объекта GoalInvitation
    }

    @Test
    public void createInvitation_ShouldCreateInvitation_WhenValidData() {
        // Given
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invitedUser.getId())).thenReturn(Optional.of(invitedUser));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(goalInvitation);
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(invitationDto);

        // When
        GoalInvitationDto result = goalInvitationService.createInvitation(invitationDto);

        // Then
        assertNotNull(result);
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    public void createInvitation_ShouldThrowException_WhenInviterAndInvitedSame() {
        invitationDto = new GoalInvitationDto(1L, 1L, 3L); // inviterId и invitedUserId совпадают

        assertThrows(InvitationCheckException.class, () -> goalInvitationService.createInvitation(invitationDto));
    }

    @Test
    public void acceptGoalInvitation_ShouldAcceptInvitation_WhenValidId() {
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.save(goal)).thenReturn(goal);

        // When
        goalInvitationService.acceptGoalInvitation(1L);

        // Then
        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    public void acceptGoalInvitation_ShouldThrowException_WhenInvitationNotFound() {
        when(goalInvitationRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(InvitationEntityNotFoundException.class, () -> goalInvitationService.acceptGoalInvitation(100L));
    }

    @Test
    public void rejectGoalInvitation_ShouldRejectInvitation_WhenValidId() {
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        // When
        goalInvitationService.rejectGoalInvitation(1L);

        // Then
        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    public void rejectGoalInvitation_ShouldThrowException_WhenInvitationNotFound() {
        when(goalInvitationRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(InvitationEntityNotFoundException.class, () -> goalInvitationService.rejectGoalInvitation(100L));
    }
}

