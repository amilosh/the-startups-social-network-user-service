package school.faang.user_service.dto.recommendationRequest;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecommendationRequestDto {
    @Positive
    private Long id;
    private String message;
    private RequestStatus status;
    private List<Long> skillsId;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}