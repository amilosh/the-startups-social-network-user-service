package school.faang.user_service.filter.recommendationRequest;

import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

public class RecommendationRequestReceiverFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return filterDto.getReceiverId() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto filterDto) {
        recommendationRequests.filter(request -> Objects.equals(request.getReceiver().getId(), filterDto.getReceiverId()));
    }
}