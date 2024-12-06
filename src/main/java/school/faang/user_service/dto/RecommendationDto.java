package school.faang.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {

    @Positive(message = "Id must be a positive number")
    private Long id;

    @NotNull(message = "Author id must be a positive number")
    @Positive(message = "Author id must be a positive number")
    private Long authorId;

    @NotNull(message = "Receiver id must be a positive number")
    @Positive(message = "Receiver id must be a positive number")
    private Long receiverId;

    @NotBlank(message = "Recommendation cannot be empty")
    private String content;

    @NotNull(message = "Skill offers list cannot be null")
    @Valid
    private List<@Valid SkillOfferDto> skillOffers;

    private LocalDateTime createdAt;
}
