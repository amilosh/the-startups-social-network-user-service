package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    private Long id;

    @NotBlank(message = "title name cannot be blank")
    @Size(min = 1, max = 64, message = "title must be between 1 and 64 characters")
    private String title;
}
