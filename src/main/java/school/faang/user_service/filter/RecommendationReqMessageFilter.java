package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationReqMessageFilter implements RecommendationRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getMessagePattern() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> requests, RequestFilterDto filter) {
        requests.filter(recRequest -> recRequest.getMessage().contains(filter.getMessagePattern()));
    }
}
