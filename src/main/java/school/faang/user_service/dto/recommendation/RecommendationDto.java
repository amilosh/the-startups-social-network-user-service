package school.faang.user_service.dto.recommendation;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record RecommendationDto(
        @Nullable Long id,
        @NotNull Long authorId,
        @NotNull Long receiverId,
        @NotNull String content,
        @Nullable List<SkillOfferDto> skillOffers,
        @Nullable LocalDateTime createdAt
) {

    public RecommendationDto(@Nullable Long id, @NotNull Long authorId, @NotNull Long receiverId, @NotNull String content,
            @Nullable List<SkillOfferDto> skillOffers, @Nullable LocalDateTime createdAt) {
        this.id = id;
        this.authorId = Objects.requireNonNull(authorId, "authorId is null");
        this.receiverId = Objects.requireNonNull(receiverId, "receiverId is null");
        this.content = Objects.requireNonNull(content, "content is null");
        this.skillOffers = skillOffers;
        this.createdAt = createdAt;
    }
}
