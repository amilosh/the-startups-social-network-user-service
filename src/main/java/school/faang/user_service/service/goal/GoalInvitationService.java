package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        User inviter = userRepository.getById(invitation.getInviterId());
        goalInvitationValidator.validateUserExists(inviter);
        User invited = userRepository.getById(invitation.getInvitedUserId());
        goalInvitationValidator.validateUserExists(invited);
        Goal goal = goalRepository.getById(invitation.getGoalId());
        goalInvitationValidator.validateGoalExists(goal);

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        goalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }
}
