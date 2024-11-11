package school.faang.user_service.validator.abstracts;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

public interface MentorshipRequestValidator {
    void validateRequesterAndReceiver(Long requesterId, Long receiverId);

    void validateRequestInterval(Long requesterId, Long receiverId);

    MentorshipRequest getRequestByIdOrThrowException(Long requestId);

    void validateRequestStatus(MentorshipRequest mentorshipRequest, RequestStatus requestedStatus);
}
