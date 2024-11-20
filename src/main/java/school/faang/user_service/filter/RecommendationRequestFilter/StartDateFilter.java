package school.faang.user_service.filter.RecommendationRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class StartDateFilter implements Filter<RecommendationRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStartDate() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter) {
        LocalDateTime startDate = filter.getStartDate();
        return stream.filter(request -> !request.getCreatedAt().isBefore(startDate));
    }
}
