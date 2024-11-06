package school.faang.user_service.dto.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillCandidateDto {
    @Valid
    private SkillDto skill;

    @NotNull(message = "Offers cannot be null")
    @Positive(message = "Offers amount must be a positive number")
    private Long offersAmount;
}
