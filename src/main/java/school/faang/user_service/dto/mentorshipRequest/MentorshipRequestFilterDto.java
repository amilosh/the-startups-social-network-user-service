package school.faang.user_service.dto.mentorshipRequest;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class MentorshipRequestFilterDto {
    private Long requesterUserId;
    private Long receiverUserId;
    private RequestStatus status;
    private Long taskId;
}
