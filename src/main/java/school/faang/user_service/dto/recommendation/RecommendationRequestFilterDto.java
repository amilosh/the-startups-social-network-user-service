package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestFilterDto {
    private Long requestIdPattern;
    private Long receiverIdPattern;
    private String messagePattern;
    private RequestStatus statusPattern;
}
