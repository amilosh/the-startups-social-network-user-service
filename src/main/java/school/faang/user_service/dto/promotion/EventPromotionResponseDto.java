package school.faang.user_service.dto.promotion;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventPromotionResponseDto {
    private long id;
    private long eventId;
    private int numberOfViews;
    private int audienceReach;
    private LocalDateTime creationDate;
}
