package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillCandidateDto {

    @NotNull(message = "Skill cannot be null")
    private SkillDto skill;

    @NotNull(message = "Offers cannot be null")
    @Positive(message = "Offers amount must be a positive number")
    private long offersAmount;
}
