package school.faang.user_service.dto.promotion;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserPromotionResponseDto {
    private long id;
    private long userId;
    private int numberOfViews;
    private int audienceReach;
    private LocalDateTime creationDate;
}
