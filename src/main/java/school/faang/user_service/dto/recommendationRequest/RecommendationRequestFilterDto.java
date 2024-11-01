package school.faang.user_service.dto.recommendationRequest;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RecommendationRequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private LocalDateTime createdAfter;
    private String rejectionReason;
}
