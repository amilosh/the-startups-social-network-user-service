package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPromotionResponseDto {
    private long id;
    private long eventId;
    private int numberOfViews;
    private int audienceReach;
    private LocalDateTime creationDate;
}
