package school.faang.user_service.service.recommendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestReceiverFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filters) {
        return filters.getReceiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RecommendationRequestFilterDto filters) {
        return requests.filter(request -> request.getReceiver().getId().equals(filters.getReceiverId()));
    }
}
