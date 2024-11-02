package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseSkillOfferDto {
    private Long id;
    private Long skillId;
    private Long recommendationId;
}
