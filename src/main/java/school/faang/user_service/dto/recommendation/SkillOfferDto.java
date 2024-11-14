package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private String skillTitle;
    private Long recommendationId;
}
