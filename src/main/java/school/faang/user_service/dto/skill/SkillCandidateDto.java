package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SkillCandidateDto {
    private SkillDto skill;
    private Long offersAmount;

    public SkillCandidateDto() {}
}
