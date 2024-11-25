package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;

import java.time.LocalDateTime;

@Component
public class MentorshipRequestValidator {

    public void validate(MentorshipRequestDto dto, boolean requesterUserExists, boolean receiverUserExists, LocalDateTime LatestRequestCreatedAt) {
        long requesterUserId = dto.getRequesterUserId();
        long receiverUserId = dto.getReceiverUserId();

        if (requesterUserId == receiverUserId) {
            throw new IllegalArgumentException("Requester and receiver IDs must be different");
        }

        if (!requesterUserExists) {
            throw new IllegalArgumentException("Requester user ID does not exist");
        }

        if (!receiverUserExists) {
            throw new IllegalArgumentException("Requester user ID does not exist");
        }

        if (LatestRequestCreatedAt.isAfter(LocalDateTime.now().minusMonths(3))) {
            throw new IllegalArgumentException("request for mentoring can be sent once every 3 months");
        }
    }
}
