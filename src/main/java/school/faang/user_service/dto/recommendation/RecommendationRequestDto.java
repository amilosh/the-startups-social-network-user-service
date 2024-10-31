package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private RequestStatus status;
    private List<Long> skillIds;
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
