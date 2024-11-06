package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    @Positive(message = "Id must be a positive integer")
    private Long id;

    @NotBlank(message = "title name cannot be blank")
    @Size(min = 1, max = 64, message = "title must be between 1 and 64 characters")

    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
