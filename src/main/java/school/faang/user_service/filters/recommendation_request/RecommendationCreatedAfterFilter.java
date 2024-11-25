package school.faang.user_service.filters.recommendation_request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
@Component
@Slf4j
public class RecommendationCreatedAfterFilter implements RecommendationRequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        boolean applicable = requestFilterDto.getCreatedAfter() != null;
        log.info("Checking if filter 'RecommendationCreatedAfterFilter' is applicable. CreatedAfter: {}, Result: {}",
                requestFilterDto.getCreatedAfter(), applicable);
        return applicable;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        boolean result = recommendationRequest.getCreatedAt().isAfter(requestFilterDto.getCreatedAfter());
        log.info("Applying filter 'RecommendationCreatedAfterFilter'. Recommendation request CreatedAt: {}, Filter CreatedAfter: {}, Result: {}",
                recommendationRequest.getCreatedAt(), requestFilterDto.getCreatedAfter(), result);
        return result;
    }
}
