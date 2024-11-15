package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest {

    private final StatusFilter statusFilter = new StatusFilter();

    @Test
    void isFilterApplicableFieldIsPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setStatus(RequestStatus.REJECTED);
        boolean filterApplicable = statusFilter.isFilterApplicable(requestFilterDto);
        assertTrue(filterApplicable);
    }

    @Test
    void isFilterApplicableFieldIsNotPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        boolean filterApplicable = statusFilter.isFilterApplicable(requestFilterDto);
        assertFalse(filterApplicable);
    }

    @Test
    void passedFilter() {
        RecommendationRequest recommendationRequest = new RecommendationRequest()
                .setStatus(RequestStatus.REJECTED);
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setStatus(RequestStatus.REJECTED);
        boolean apply = statusFilter.apply(recommendationRequest, requestFilterDto);
        assertTrue(apply);
    }

    @Test
    void failedFilter() {
        RecommendationRequest recommendationRequest = new RecommendationRequest()
                .setStatus(RequestStatus.PENDING);
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setStatus(RequestStatus.REJECTED);
        boolean apply = statusFilter.apply(recommendationRequest, requestFilterDto);
        assertFalse(apply);
    }
}