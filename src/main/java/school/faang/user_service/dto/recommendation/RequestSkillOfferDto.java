package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestSkillOfferDto {
    private Long id;
    private Long skillId;
    private String skillTitle;
    private Long recommendationId;
}
