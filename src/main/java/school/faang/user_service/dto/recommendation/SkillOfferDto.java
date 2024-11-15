package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillOfferDto {
    private long id;
    private long skillId;
    private long recommendationId;
    private String skillTitle;
}
