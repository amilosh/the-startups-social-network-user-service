package school.faang.user_service.dto.mentorship_request;

import lombok.Data;
import school.faang.user_service.entity.enumeration.RequestStatus;

@Data
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
