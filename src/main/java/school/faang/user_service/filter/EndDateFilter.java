package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class EndDateFilter implements Filter<RecommendationRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter != null && filter.getEndDate() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter) {
        LocalDateTime endDate = filter.getEndDate();
        return stream.filter(request -> !request.getCreatedAt().isAfter(endDate));
    }
}
