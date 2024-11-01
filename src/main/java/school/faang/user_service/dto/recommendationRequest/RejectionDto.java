package school.faang.user_service.dto.recommendationRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionDto {
    private String rejectionReason;
}
