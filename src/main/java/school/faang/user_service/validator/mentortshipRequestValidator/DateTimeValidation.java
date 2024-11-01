package school.faang.user_service.validator.mentortshipRequestValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.ValidationContext;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DateTimeValidation implements MentorshipRequestValidator {

    @Override
    public void validate(MentorshipRequestDto mentorshipRequestDto, ValidationContext validationContext) {
        long requesterUserId = mentorshipRequestDto.getRequesterUserId();
        long receiverUserId = mentorshipRequestDto.getReceiverUserId();

        MentorshipRequest request = validationContext.mentorshipRequestService()
                .findLatestRequest(requesterUserId, receiverUserId);

        checkIfOlderThanThreeMonths(request.getCreatedAt());
    }

    private void checkIfOlderThanThreeMonths(LocalDateTime date) {
        if (!date.isBefore(LocalDateTime.now().minusMonths(3))) {
            throw new IllegalArgumentException("created time not older than three-months");
        }
    }
}
