package school.faang.user_service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RecommendationReceivedEvent {
    private final Long recommendationId;
    private final Long receiverId;
    private final Long authorId;
}
