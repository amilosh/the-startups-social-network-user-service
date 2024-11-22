package school.faang.user_service.dto.skill;

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
    @Schema(description = "Title of the skill", example = "Java")
    private String title;
}