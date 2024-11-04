package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;
    private final GoalService goalService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        goalInvitation.setInviter(userService.findUserById(invitation.getInviterId()));
        goalInvitation.setInvited(userService.findUserById(invitation.getInvitedUserId()));
        goalInvitation.setGoal(goalService.findGoalById(invitation.getGoalId()));
        goalInvitation.setStatus(RequestStatus.PENDING);

        goalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public void acceptGoalInvitation(long id) {
        Optional<GoalInvitation> goalInvitation = goalInvitationRepository.getById(id);
        goalInvitation.ifPresent(invitation -> {
            goalInvitationValidator.validateGoalInvitationAcceptance(invitation);

            invitation.setStatus(RequestStatus.ACCEPTED);
            invitation.getInvited()
                    .getGoals()
                    .add(invitation.getGoal());

            goalInvitationRepository.save(invitation);
        });
    }

    public void rejectGoalInvitation(long id) {
        Optional<GoalInvitation> goalInvitation = goalInvitationRepository.getById(id);
        goalInvitation.ifPresent(invitation -> {
            goalInvitationValidator.validateGoalInvitationRejection(invitation);

            invitation.setStatus(RequestStatus.REJECTED);

            goalInvitationRepository.save(invitation);
        });
    }
}
