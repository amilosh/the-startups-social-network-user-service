package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestRecommendationDto {
    private Long id;

    @NotNull(message = "The author's ID should not be null")
    private Long authorId;

    @NotNull(message = "The receiver's ID should not be null")
    private Long receiverId;

    @NotEmpty(message = "The recommendation content should not be empty")
    @NotBlank(message = "The recommendation content should not be blank")
    @Size(min = 1, max = 4096, message = "The recommendation content should be between 1 and 4096 characters long")
    private String content;

    @NotNull(message = "The list of skill offers should not be null")
    @Size(min = 1, message = "The list of skill offers should contain at least one skill")
    private List<SkillOfferDto> skillOffers;
}
