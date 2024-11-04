package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class GoalInvitationServiceImpl implements GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final int userGoalsLimit;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        log.info("Create invitation, inviterId: {}, invitedUserId: {}, goalId: {}", invitation.inviterId(), invitation.invitedUserId(), invitation.goalId());
        checkUsersFromMatching(invitation);
        invitationSetEntities(invitation);
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(savedGoalInvitation);
    }

    private void checkUsersFromMatching(GoalInvitationDto invitation) {
        log.info("Check users for matching, inviterId: {}, invitedUserId: {}", invitation.inviterId(), invitation.invitedUserId());
        if (invitation.inviterId().equals(invitation.invitedUserId())) {
            throw new InvitationCheckException(String.format("ID приглашающего пользователя: %s и ID приглашенного пользователя: %s не должны совпадать", invitation.inviterId(), invitation.invitedUserId()));
        }
    }

    private void invitationSetEntities(GoalInvitationDto invitation) {
        log.info("Установить для объектов новое приглашение");
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        User inviter = findUserById(invitation.inviterId(), "ID приглашающего: %s не найден");
        goalInvitation.setInviter(inviter);
        User invitedUser = findUserById(invitation.invitedUserId(), "ID приглашенного пользователя: %s не найден");
        goalInvitation.setInvited(invitedUser);
        Goal goal = goalRepository.findById(invitation.goalId()).orElseThrow(() ->
                new InvitationEntityNotFoundException(String.format("Goal с id: %s не найдена", invitation.goalId())));
        goalInvitation.setGoal(goal);
    }

    private User findUserById(Long userId, String errorMessage) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format(errorMessage, userId)));
    }

    public void acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with idL {}", id);
        GoalInvitation invitation = findGoalInvitationById(id);
        User invitedUser = invitation.getInvited();
        Goal goal = invitation.getGoal();
        checkUserGoals(invitedUser, goal, invitation);
        goal.getUsers().add(invitedUser);
        invitation.setStatus(RequestStatus.ACCEPTED);
        goalRepository.save(goal);
        goalInvitationRepository.save(invitation);
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
                new InvitationEntityNotFoundException("приглашение на цель с id: %s не найдено"));
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
