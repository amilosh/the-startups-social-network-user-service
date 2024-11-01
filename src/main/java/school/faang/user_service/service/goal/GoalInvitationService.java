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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private Integer userGoalsLimit = 3;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final List<InvitationFilter> filters;

    public GoalInvitation createInvitation(GoalInvitation invitation, long inviterId, long invitedUserId, long goalId) {
        log.info("Create invitation, inviterId: {}, invitedUserId: {}, goalId: {}", inviterId, invitedUserId, goalId);
        checkUsersFromMatching(inviterId, invitedUserId);
        invitationSetEntities(invitation, inviterId, invitedUserId, goalId);
        return goalInvitationRepository.save(invitation);
    }

    public void acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with idL {}", id);
        GoalInvitation invitation = findGoalInvitationById(id);
        User invitedUser = invitation.getInvited();
        Goal goal = invitation.getGoal();
        checkUserGoals(invitedUser, goal, invitation);
        goal.getUsers().add(invitedUser);
        invitation.setStatus(RequestStatus.ACCEPTED);
    }

    public void rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}", id);
        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    public List<GoalInvitation> getInvitations(InvitationFilterIDto filterDto) {
        log.info("Get invitations by filter: {}", filterDto);
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        return filters
                .stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(invitations.stream(), (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();
    }

    private void checkUsersFromMatching(long inviterId, long invitedUserId) {
        log.info("Check users for matching, inviterId: {}, invitedUserId: {}", inviterId, invitedUserId);
        if (inviterId == invitedUserId) {
            throw new InvitationCheckException("ID приглашающего пользователя: %s и ID приглашенного пользователя: % s не должны совпадать", inviterId, invitedUserId);
        }
    }

    private void invitationSetEntities(GoalInvitation invitation, long inviterId, long invitedUserId, long goalId) {
        log.info("Установить для объектов новое приглашение");
        User inviter = findUserById(inviterId, "ID приглашающего: %s не найден");
        invitation.setInviter(inviter);
        User invitedUser = findUserById(invitedUserId, "ID приглашенного пользователя: %s не найден");
        invitation.setInvited(invitedUser);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new InvitationEntityNotFoundException("Goal с id: %s не найдена", goalId));
        invitation.setGoal(goal);
    }

    private User findUserById(long id, String notFoundMessage) {
        return userRepository.findById(id).orElseThrow(() -> new InvitationEntityNotFoundException(notFoundMessage, id));
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException("приглашение на цель с id: %s не найдено", id));
    }

    private void checkUserGoals(User invitedUser, Goal goal, GoalInvitation invitation) {
        log.info("Check for user ability with id: {} get goal with id: {}", invitedUser.getId(), goal.getId());
        List<Goal> activeGoals = getActiveGoals(invitedUser);
        if (activeGoals.contains(goal)) {
            invitation.setStatus(RequestStatus.REJECTED);
            throw new InvitationCheckException("Пользователь с id: %s уже имеет цель с id: %s", invitedUser.getId(), goal.getId());
        }
        if (activeGoals.size() >= userGoalsLimit) {
            invitation.setStatus(RequestStatus.REJECTED);
            throw new InvitationCheckException("Количество целей у пользователя с id: %s превышает допустимый предел в: %s", invitedUser.getId(), userGoalsLimit);
        }
    }

    private List<Goal> getActiveGoals(User invitedUser) {
        log.info("Get active goal of user with id; {}", invitedUser.getId());
        return invitedUser.getGoals()
                .stream()
                .filter(g -> g.getStatus().equals(GoalStatus.ACTIVE))
                .toList();
    }
}
