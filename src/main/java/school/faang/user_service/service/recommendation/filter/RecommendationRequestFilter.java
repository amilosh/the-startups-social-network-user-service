package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto filters);
    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendations, RecommendationRequestFilterDto filters);
}
