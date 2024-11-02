package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    private Long id;
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
    @NotNull
    private String content;
    private List<SkillOfferDto> skillOffers;
    @NotNull
    private LocalDateTime createdAt;
}
