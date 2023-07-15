package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

@Data
@AllArgsConstructor
public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;
}
