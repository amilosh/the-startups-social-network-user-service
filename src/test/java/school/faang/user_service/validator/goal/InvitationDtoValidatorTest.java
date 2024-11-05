package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvitationDtoValidatorTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalInvitationMapper goalInvitationMapper;
    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private InvitationDtoValidator invitationDtoValidator;
    private GoalInvitationDto goalInvitationDto;
    private GoalInvitation goalInvitation;
    private User invitedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        goalInvitationDto = new GoalInvitationDto(/* инициализация необходимых полей */);
        invitedUser = new User(); // инициализация User
        goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);

    }

    @Test
    void testCreateInvitation() {
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);

        assertNotNull(result);
        assertEquals(result, goalInvitationDto);
        verify(invitationDtoValidator).validate(goalInvitationDto);
        verify(goalInvitationRepository).save(goalInvitation);

    }
    @Test
    void testAcceptGoalInvitation() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
        when(userRepository.save(invitedUser)).thenReturn(invitedUser);

        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
        verify(goalInvitationRepository).save(goalInvitation);
        verify(userRepository).save(invitedUser);
    }

    @Test
    void testRejectGoalInvitation() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    void testRejectGoalInvitation_InvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvitationEntityNotFoundException.class, () -> goalInvitationService.rejectGoalInvitation(1L));
    }
}
