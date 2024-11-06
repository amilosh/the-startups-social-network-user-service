package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillAcquireDto {
    @Positive(message = "Value must be positive")
    private Long skillId;

    @Positive(message = "Value must be positive")
    private Long userId;
}
