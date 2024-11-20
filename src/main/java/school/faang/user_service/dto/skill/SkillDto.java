package school.faang.user_service.dto.skill;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {

    @Positive(message = "Id must be a positive integer")
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 64, message = "Title length is too long, max 64 symbols")
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
