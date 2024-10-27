package school.faang.user_service.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class RecommendationRequestedEventMessage implements Serializable {
    private Long id;
    private Long requesterId;
    private Long receiverId;
}
