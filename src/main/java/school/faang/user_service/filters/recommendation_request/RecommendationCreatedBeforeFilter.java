package school.faang.user_service.filters.recommendation_request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
@Slf4j
public class RecommendationCreatedBeforeFilter implements RecommendationRequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        boolean applicable = requestFilterDto.getCreatedBefore() != null;
        log.info("Checking if filter 'RecommendationCreatedBeforeFilter' is applicable. CreatedBefore: {}, Result: {}",
                requestFilterDto.getCreatedBefore(), applicable);
        return applicable;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        boolean result = recommendationRequest.getCreatedAt().isBefore(requestFilterDto.getCreatedBefore());
        log.info("Applying filter 'RecommendationCreatedBeforeFilter'. Recommendation CreatedAt: {}, Filter CreatedBefore: {}, Result: {}",
                recommendationRequest.getCreatedAt(), requestFilterDto.getCreatedBefore(), result);
        return result;
    }
}
