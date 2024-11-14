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
public class UserPromotionResponseDto {
    private long id;
    private long userId;
    private int numberOfViews;
    private int audienceReach;
    private LocalDateTime creationDate;
}
