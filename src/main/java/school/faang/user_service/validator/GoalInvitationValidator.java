package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exceptions.DataValidationException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationValidator {

    public void validateInvitation(GoalInvitationDto goalInvitationDto) {
        if (Objects.equals(goalInvitationDto.inviterId(), goalInvitationDto.invitedUserId())) {
            throw new DataValidationException("The same user cannot be an invited and inviter");
        }
    }
}
