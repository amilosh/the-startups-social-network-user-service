package school.faang.user_service.service.recommendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RecommendationRequestMessageFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getMessagePattern() != null;
    }

    @Override
    public List<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                             RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequests
                .filter(recommendationRequest -> recommendationRequest.getMessage().contains(requestFilterDto.getMessagePattern()))
                .toList();
    }
}
