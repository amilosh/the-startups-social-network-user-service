package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 255, message = "The message pattern should be between 1 and 255 characters long")
    private String messagePattern;

    private RequestStatus statusPattern;
}
