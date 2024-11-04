package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.UserValidator;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserValidator userValidator;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto invitationDto) {
        long inviter = invitationDto.getInviterId();
        long invited = invitationDto.getInvitedUserId();

        if (inviter == invited) {
            throw new DataValidationException("Id of inviter and invited user can't be the same");
        }
        userValidator.userAlreadyExists(inviter);
        userValidator.userAlreadyExists(invited);

        goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
    }
}
