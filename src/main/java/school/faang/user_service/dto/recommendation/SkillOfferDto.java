package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class SkillOfferDto {
    private long id;
    private long skillId;
    private long recommendationId;
    private String skillTitle;
}
