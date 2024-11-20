package school.faang.user_service.dto.recommendationRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class RecommendationRequestRejectionDto {
    private String rejectionReason;
}
