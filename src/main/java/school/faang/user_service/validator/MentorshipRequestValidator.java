package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestValidator {
    private final UserService userService;
    private final MentorshipRequestService mentorshipRequestService;

    public void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        long requesterUserUd = mentorshipRequestDto.getRequesterUserId();
        long receiverUserId = mentorshipRequestDto.getReceiverUserId();

        if (mentorshipRequestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("mentorshipRequest description is blank");
        }

        if (!userService.existsById(requesterUserUd)) {
            throw new IllegalArgumentException("requester user not found");
        }

        if (!userService.existsById(receiverUserId)) {
            throw new IllegalArgumentException("receiver user not found");
        }

        if (requesterUserUd == receiverUserId) {
            throw new IllegalArgumentException("requester user is the same as receiver user");
        }

        mentorshipRequestService.findLatestRequest(requesterUserUd, receiverUserId)
                .ifPresent(mentorshipRequest -> checkIfOlderThanThreeMonths(mentorshipRequest.getCreatedAt()));
    }

    private void checkIfOlderThanThreeMonths(LocalDateTime date) {
        if (!date.isBefore(LocalDateTime.now().minusMonths(3))) {
            throw new IllegalArgumentException("created time not older than three-months");
        }
    }
}
