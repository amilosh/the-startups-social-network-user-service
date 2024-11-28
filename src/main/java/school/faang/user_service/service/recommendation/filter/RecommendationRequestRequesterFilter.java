package school.faang.user_service.service.recommendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestRequesterFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filters) {
        return filters.getRequesterIds() != null && !filters.getRequesterIds().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendations, RecommendationRequestFilterDto filters) {
        return recommendations.filter(recommendation ->
                filters.getRequesterIds().contains(recommendation.getRequester().getId()));
    }
}
