package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationReceivedEvent {

    private long authorId;
    private long receiverId;
    private long recommendationId;

}
