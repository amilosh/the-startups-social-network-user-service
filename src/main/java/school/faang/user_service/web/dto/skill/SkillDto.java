package school.faang.user_service.web.dto.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {

    private Long id;

    @Min(0)
    @Max(300)
    @NotBlank
    private String title;
}
