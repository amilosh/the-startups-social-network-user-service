package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestFilterDto {
    private RequestStatus status;
    private Long requesterId;
    private Long receiverId;
}
