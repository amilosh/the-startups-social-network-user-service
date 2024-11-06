package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterManager {

    private final List<RequestFilter> filters;

    public RecommendationRequestFilterManager(List<RequestFilter> filters) {
        this.filters = filters;
    }

    public Stream<RecommendationRequest> applyFilters(
            Stream<RecommendationRequest> stream, RequestFilterDto filterDto) {
        for (RequestFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                stream = filter.apply(stream, filterDto);
            }
        }
        return stream;
    }
}
