package school.faang.user_service.dto.promotion;

import lombok.Builder;

@Builder
public record RequestPromotionDto(int numberOfViews) {
}
