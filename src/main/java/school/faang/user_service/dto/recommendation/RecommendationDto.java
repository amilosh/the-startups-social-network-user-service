package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationDto {
    public Long id;
    public Long authorId;
    public Long receiverId;
    public String content;
    public List<SkillOfferDto> skillOffers;
    public LocalDateTime createdAt;
}
