package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class RequestSkillOfferDto {
    private Long id;
    private Long skillId;
    private String skillTitle;
}
