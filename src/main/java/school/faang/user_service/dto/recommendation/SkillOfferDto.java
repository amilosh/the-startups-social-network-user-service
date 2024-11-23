package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SkillOfferDto {
    private long id;
    private List<Long> skillsId;
}
