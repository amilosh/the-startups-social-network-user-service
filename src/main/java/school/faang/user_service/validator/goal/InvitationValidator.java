package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.goal.ValidateException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationValidator {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public void validate(final GoalInvitationRequestDto goalInvitationDto) {
        validateUserIsNotInvitingSelf(goalInvitationDto);
        validateUserExists(goalInvitationDto.getInviterId(), "Inviter");
        validateUserExists(goalInvitationDto.getInvitedUserId(), "Invited");
        validateGoalExists(goalInvitationDto.getGoalId());
    }

    private void validateUserIsNotInvitingSelf(GoalInvitationRequestDto goalRequestDto) {
        log.info("Check users for matching, inviter: {}, invited: {}", goalRequestDto.getInvitedUserId(), goalRequestDto.getInviterId());
        if (goalRequestDto.getInvitedUserId().equals(goalRequestDto.getInviterId())) {
            throw new DataValidationException("Inviter and invited user cannot be the same.");
        }
    }

    public void validateInvitationStatus(GoalInvitation goalInvitation, RequestStatus requestStatus, String massage) {
        if (goalInvitation.getStatus() == requestStatus){
            throw new ValidateException(massage);
        }

    }

    private void validateUserExists(Long userId, String userType) {
        log.info("Checking existence of {} user with id: {}", userType, userId);
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(userType + " user with id: " + userId + " does not exist.");
        }
    }

    private void validateGoalExists(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.error("Goal with id: {} does not exist.", goalId);
            throw new NoSuchElementException("Goal with id: " + goalId + " does not exist.");
        }
    }
}
