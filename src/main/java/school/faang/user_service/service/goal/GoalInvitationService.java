package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserValidator userValidator;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalService goalService;

    public void createInvitation(GoalInvitationDto invitationDto) {
        long inviter = invitationDto.getInviterId();
        long invited = invitationDto.getInvitedUserId();

        validateIdEquality(inviter, invited);
        validateUsersExistence(inviter, invited);

        goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Invitation to join the goal with id: " + id + " not found in DB"));

        validateUserGoalsAmount(invitation.getInvited().getId());
        validateGoalExistence(invitation.getId());
        validateGoalAlreadyPicked(invitation.getInvited().getId());

        invitation.getInvited().setGoals(List.of(invitation.getGoal())); //Maybe update in DB??
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Invitation to join the goal with id: " + id + " not found in DB"));

        validateGoalExistence(invitation.getId());
        goalInvitationRepository.deleteById(invitation.getId());
    }

    private void validateIdEquality(long userId1, long userId2) {
        if (userId1 == userId2) {
            throw new DataValidationException("Id of inviter and invited user can't be the same");
        }
    }

    private void validateUsersExistence(long userId1, long userId2) {
        userValidator.areUsersExist(userId1, userId2);
    }

    private void validateUserGoalsAmount(long id) {
        goalService.validateUserGoalsAmount(id);
    }

    private void validateGoalExistence(long id) {
        goalService.getGoalById(id);
    }

    private void validateGoalAlreadyPicked(long id) {
        List<Goal> userGoals = goalService.getGoalsByUserId(id).toList();
        if (userGoals.contains(goalService.getGoalById(id))) {
            throw new IllegalStateException("User already has this goal");
        }
    }
}
