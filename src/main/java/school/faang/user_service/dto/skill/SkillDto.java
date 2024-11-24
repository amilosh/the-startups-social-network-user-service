package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    @Schema(description = "Unique identifier of the skill", example = "1")
    private Long id;

    @NotBlank(message = "The skill title should not be blank")
    @NotEmpty(message = "The skill title should not be empty")
    @Size(min = 1, max = 64, message = "The skill title should be between 1 and 64 characters long")
    @Schema(description = "Title of the skill", example = "Java")
    private String title;
}