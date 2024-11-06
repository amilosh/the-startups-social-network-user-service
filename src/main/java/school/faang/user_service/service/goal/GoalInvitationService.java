package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.UserValidator;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserValidator userValidator;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto invitationDto) {
        long inviter = invitationDto.getInviterId();
        long invited = invitationDto.getInvitedUserId();

        userValidator.userIdsAreEqual(inviter, invited);
        userValidator.userAlreadyExists(inviter);
        userValidator.userAlreadyExists(invited);

        goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
    }
}
