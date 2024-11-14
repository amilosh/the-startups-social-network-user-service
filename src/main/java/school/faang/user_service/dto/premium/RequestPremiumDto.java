package school.faang.user_service.dto.premium;

import lombok.Builder;

@Builder
public record RequestPremiumDto(int days) {
}
