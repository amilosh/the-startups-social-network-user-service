package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserService userService;
    private final GoalService goalService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        goalInvitation.setInviter(userService.findUserById(invitation.getInviterId()));
        goalInvitation.setInvited(userService.findUserById(invitation.getInvitedUserId()));
        goalInvitation.setGoal(goalService.findGoalById(invitation.getGoalId()));

        goalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }
}
