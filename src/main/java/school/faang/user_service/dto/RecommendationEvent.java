package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEvent {
    private Long recommendationId;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime eventTime;
}
