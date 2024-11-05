package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.InvitationCheckException;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.exception.goal.UserNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final int userGoalsLimit;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        log.info("Create invitation, inviterId: {}, invitedUserId: {}", invitation.inviterId(), invitation.invitedUserId());
        checkUsersFromMatching(invitation);
        invitationSetEntities(invitation);
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(savedGoalInvitation);
    }

    private void checkUsersFromMatching(GoalInvitationDto invitation) {
        log.info("Check users for matching, inviterId: {}, invitedUserId: {}", invitation.inviterId(), invitation.invitedUserId());
        if (invitation.inviterId().equals(invitation.invitedUserId())) {
            throw new InvitationCheckException(String.format("The inviting user ID: %s and the invited user ID: %s must not match", invitation.inviterId(), invitation.invitedUserId()));
        }
    }

    private void invitationSetEntities(GoalInvitationDto invitation) {
        log.info("Set a new prompt for objects");
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        User inviter = findUserById(invitation.inviterId(), "Inviter ID: %s not found");
        goalInvitation.setInviter(inviter);
        User invitedUser = findUserById(invitation.invitedUserId(), "Invited user ID: %s not found");
        goalInvitation.setInvited(invitedUser);
        Goal goal = goalRepository.findById(invitation.inviterId()).orElseThrow(() ->
                new InvitationEntityNotFoundException("Goal with id: %s not found"));
        goalInvitation.setGoal(goal);
    }

    private User findUserById(Long userId, String errorMessage) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format(errorMessage, userId)));
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with idL {}", id);
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        User invitedUser = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();
        checkUserGoals(invitedUser, goal, goalInvitation);
        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        invitedUser.getGoals().add(goal);
        return goalInvitationMapper.toDto(savedGoalInvitation);
    }

    public void rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}", id);
        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException("invitation to a goal with id: %s not found"));
    }

    private void checkUserGoals(User invitedUser, Goal goal, GoalInvitation invitation) {
        log.info("Check for user ability with id: {} get goal with id: {}", invitedUser.getId(), goal.getId());
        List<Goal> activeGoals = getActiveGoals(invitedUser);
        if (activeGoals.contains(goal)) {
            invitation.setStatus(RequestStatus.REJECTED);
            throw new InvitationCheckException("A user with id: %s already has a goal with id: %s", invitedUser.getId(), goal.getId());
        }
        if (activeGoals.size() >= userGoalsLimit) {
            invitation.setStatus(RequestStatus.REJECTED);
            throw new InvitationCheckException("The number of goals for a user with id: %s exceeds the allowed limit in: %s", invitedUser.getId(), userGoalsLimit);
        }
    }

    private List<Goal> getActiveGoals(User invitedUser) {
        log.info("Get active goal of user with id; {}", invitedUser.getId());
        return invitedUser.getGoals()
                .stream()
                .filter(g -> g.getStatus().equals(GoalStatus.ACTIVE))
                .toList();
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterIDto filterDto) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }
        filters.stream()
                .filter(f -> f.isApplicable((InvitationFilterIDto) filters))
                .forEach(f -> f.apply(invitations.stream(), (InvitationFilterIDto) filters));
        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }
}
