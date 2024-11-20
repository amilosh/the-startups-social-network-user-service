package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "The skill ID should not be null")
    private Long skillId;

    @NotBlank(message = "The skill title should not be blank")
    @NotEmpty(message = "The skill title should not be empty")
    @Size(min = 1, max = 64, message = "The skill title should be between 1 and 64 characters long")
    private String skillTitle;

    private Long recommendationId;
}
