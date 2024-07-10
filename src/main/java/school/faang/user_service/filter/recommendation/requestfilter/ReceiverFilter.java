package school.faang.user_service.filter.recommendation.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.filter.Filter;

@Component
public class ReceiverFilter implements Filter<RecommendationRequestFilterDto, RecommendationRequestDto> {
    @Override
    public boolean applyFilter(RecommendationRequestDto data, RecommendationRequestFilterDto filter) {
        return data.getReceiverId().equals(filter.getReceiverId());
    }

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }
}
