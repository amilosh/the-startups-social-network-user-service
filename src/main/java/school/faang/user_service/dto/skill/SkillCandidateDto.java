package school.faang.user_service.dto.skill;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillCandidateDto {
    @Valid
    private SkillDto skill;

    private Long offersAmount;
}
