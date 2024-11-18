package school.faang.user_service.filter.RecommendationRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class RecommendationRequesterIdFilter implements Filter<RecommendationRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter) {
        Long requesterId = filter.getRequesterId();
        return stream.filter(request -> request.getRequester().getId().equals(requesterId));
    }
}
