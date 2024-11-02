package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DuplicateObjectException;
import school.faang.user_service.exception.GoalInvitationStatusException;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.exception.ValueExceededException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GoalInvitationServiceTest {

    private static final String USERS_ARE_EQUAL_MESSAGE = "User inviter and invited user are equal.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found by ID: ";
    private static final String GOAL_NOT_FOUND_MESSAGE = "Goal not found by ID: ";
    private static final String GOAL_INVITATION_NOT_FOUND_MESSAGE = "Goal invitation not found by ID: ";
    private static final String GOAL_INVITATION_STATUS_MESSAGE = "The goal invitation status is already ";
    private static final String DUPLICATE_GOAL_MESSAGE = "User with ID: %d already has the goal with ID: %d";
    private static final String MAX_ACTIVE_GOALS_EXCEEDED_MESSAGE = "The maximum number: %d of active goals has been exceeded for user with ID: %d";

    @Value("${application.constants.max-active-goals-count}")
    private int maxActiveGoalsCount;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    public void testCreateInvitationEqualInviterIdAndInvitedId() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 1L, 1L);

        GoalInvitationValidationException goalInvitationValidationException = assertThrows(GoalInvitationValidationException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals(USERS_ARE_EQUAL_MESSAGE, goalInvitationValidationException.getMessage());
    }

    @Test
    public void testCreateInvitationInviterNotFound() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals(USER_NOT_FOUND_MESSAGE + goalInvitationDto.getInviterId(), entityNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testCreateInvitationInvitedNotFound() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);
        User inviter = createUser(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals(USER_NOT_FOUND_MESSAGE + goalInvitationDto.getInvitedUserId(), entityNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verify(userRepository, times(1)).findById(goalInvitationDto.getInvitedUserId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testCreateInvitationGoalNotFound() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invited));
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals(GOAL_NOT_FOUND_MESSAGE + goalInvitationDto.getGoalId(), entityNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verify(userRepository, times(1)).findById(goalInvitationDto.getInvitedUserId());
        verify(goalRepository, times(1)).findById(goalInvitationDto.getGoalId());
        verifyNoMoreInteractions(userRepository, goalRepository);
    }

    @Test
    public void testCreateInvitationSuccessful() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        Goal goal = createGoal(1L);
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invited));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);
        assertNotNull(result);
        assertEquals(goalInvitation.getInviter().getId(), result.getInviterId());
        assertEquals(goalInvitation.getInvited().getId(), result.getInvitedUserId());
        assertEquals(goalInvitation.getGoal().getId(), result.getGoalId());
        assertEquals(goalInvitation.getStatus(), result.getStatus());

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verify(userRepository, times(1)).findById(goalInvitationDto.getInvitedUserId());
        verify(goalRepository, times(1)).findById(goalInvitationDto.getGoalId());
        verify(goalInvitationRepository, times(1)).save(any(GoalInvitation.class));
        verifyNoMoreInteractions(userRepository, goalRepository, goalInvitationRepository);
    }

    @Test
    public void testAcceptGoalInvitationNotFound() {
        long goalInvitationId = 1L;

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitationId));
        assertEquals(GOAL_INVITATION_NOT_FOUND_MESSAGE + goalInvitationId, entityNotFoundException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitationId);
        verifyNoMoreInteractions(goalInvitationRepository);
    }

    @Test
    public void testAcceptGoalInvitationStatusAlreadyAccepted() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationStatusException goalInvitationStatusException = assertThrows(GoalInvitationStatusException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(GOAL_INVITATION_STATUS_MESSAGE + goalInvitation.getStatus(), goalInvitationStatusException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verifyNoMoreInteractions(goalInvitationRepository);
    }

    @Test
    public void testAcceptGoalInvitationStatusAlreadyRejected() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.REJECTED);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationStatusException goalInvitationStatusException = assertThrows(GoalInvitationStatusException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(GOAL_INVITATION_STATUS_MESSAGE + goalInvitation.getStatus(), goalInvitationStatusException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verifyNoMoreInteractions(goalInvitationRepository);
    }

    @Test
    public void testAcceptGoalInvitationGoalNotFound() {
        Goal goal = createGoal(1L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(GOAL_NOT_FOUND_MESSAGE + goal.getId(), entityNotFoundException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository);
    }

    @Test
    public void testAcceptGoalInvitationInviterNotFound() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(USER_NOT_FOUND_MESSAGE + inviter.getId(), entityNotFoundException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);
    }

    @Test
    public void testAcceptGoalInvitationInvitedNotFound() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(USER_NOT_FOUND_MESSAGE + invited.getId(), entityNotFoundException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);
    }

    @Test
    public void testAcceptGoalInvitationInvitedHasGoal() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        invited.getGoals().add(goal);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));

        DuplicateObjectException duplicateObjectException = assertThrows(DuplicateObjectException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(String.format(DUPLICATE_GOAL_MESSAGE, invited.getId(), goal.getId()), duplicateObjectException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);
    }

    @Test
    public void testAcceptGoalInvitationMaxActiveGoalsExceeded() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        invited.getGoals().addAll(List.of(
                createGoal(2L),
                createGoal(3L),
                createGoal(4L)
        ));
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));

        ValueExceededException valueExceededException = assertThrows(ValueExceededException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(String.format(MAX_ACTIVE_GOALS_EXCEEDED_MESSAGE, maxActiveGoalsCount, invited.getId()), valueExceededException.getMessage());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);
    }

    @Test
    public void testAcceptGoalInvitationSuccessful() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));
        when(userRepository.save(any(User.class))).thenReturn(invited);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
        assertNotNull(result);
        assertEquals(goalInvitation.getId(), result.getId());
        assertEquals(goalInvitation.getInviter().getId(), result.getInviterId());
        assertEquals(goalInvitation.getInvited().getId(), result.getInvitedUserId());
        assertEquals(goalInvitation.getGoal().getId(), result.getGoalId());
        assertEquals(goalInvitation.getStatus(), result.getStatus());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verify(userRepository, times(1)).save(invited);
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);
    }

    @Test
    public void testRejectGoalInvitationNotFound() {

    }

    @Test
    public void testRejectGoalInvitationStatusAlreadyAccepted() {

    }

    @Test
    public void testRejectGoalInvitationStatusAlreadyRejected() {

    }

    @Test
    public void testRejectGoalInvitationGoalNotFound() {

    }

    @Test
    public void testRejectGoalInvitationInviterNotFound() {

    }

    @Test
    public void testRejectGoalInvitationInvitedNotFound() {

    }

    @Test
    public void testRejectGoalInvitationSuccessful() {

    }

    @Test
    public void testGetInvitationsAllFiltersNull() {

    }

    @Test
    public void testGetInvitationsAllFiltersNotNull() {

    }

    @Test
    public void testGetInvitationsSomeFiltersNullSomeFiltersNotNull() {

    }

    private GoalInvitationDto createGoalInvitationDto(Long inviterId, Long invitedId, Long goalId) {
        return new GoalInvitationDto(null, inviterId, invitedId, goalId, null);
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setGoals(new ArrayList<>());
        return user;
    }

    private Goal createGoal(Long goalId) {
        Goal goal = new Goal();
        goal.setId(goalId);
        return goal;
    }
}
