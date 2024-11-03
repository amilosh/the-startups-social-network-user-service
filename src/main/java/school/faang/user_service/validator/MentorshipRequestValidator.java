package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator implements Validator {
    private final UserService userService;
    private final MentorshipRequestService mentorshipRequestService;

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return MentorshipRequestDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        MentorshipRequestDto dto = (MentorshipRequestDto) target;

        long requesterUserId = dto.getRequesterUserId();
        long receiverUserId = dto.getReceiverUserId();

        if (requesterUserId == receiverUserId) {
            String defaultMessage = "Requester and receiver IDs must be different";

            errors.rejectValue(
                    "receiverUserId and requesterUserId",
                    "different.users",
                    defaultMessage
            );
            throw new IllegalArgumentException(defaultMessage);
        }

        if (!userService.existsById(requesterUserId)) {
            String defaultMessage = "Requester user ID does not exist";

            errors.rejectValue(
                    "requesterUserId",
                    "user.not.found",
                    defaultMessage
            );
            throw new IllegalArgumentException(defaultMessage);
        }

        if (!userService.existsById(receiverUserId)) {
            String defaultMessage = "Requester user ID does not exist";

            errors.rejectValue(
                    "receiverUserId",
                    "user.not.found",
                    "Receiver user ID does not exist"
            );
            throw new IllegalArgumentException(defaultMessage);
        }

        MentorshipRequest request = mentorshipRequestService.findLatestRequest(requesterUserId, receiverUserId);
        if (request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
            errors.rejectValue(
                    "createAt",
                    "older.than.three-months",
                    "created time not older than three-months"
            );
            throw new IllegalArgumentException("request for mentoring can be sent once every 3 months");
        }
    }
}
