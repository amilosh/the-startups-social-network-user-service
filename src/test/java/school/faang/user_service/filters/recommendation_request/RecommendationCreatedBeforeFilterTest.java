package school.faang.user_service.filters.recommendation_request;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationCreatedBeforeFilterTest {
    private final RecommendationCreatedBeforeFilter createdBeforeFilter = new RecommendationCreatedBeforeFilter();

    @Test
    void testIsFilterApplicableFieldIsPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedBefore(LocalDateTime.now());
        boolean filterApplicable = createdBeforeFilter.isFilterApplicable(requestFilterDto);
        assertTrue(filterApplicable);
    }

    @Test
    void testIsFilterApplicableFieldIsNotPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        boolean filterApplicable = createdBeforeFilter.isFilterApplicable(requestFilterDto);
        assertFalse(filterApplicable);
    }

    @Test
    void testPassedFilter() {
        RecommendationRequest recommendationRequestDto = new RecommendationRequest()
                .setCreatedAt(LocalDateTime.of(2024, 11, 14, 23, 35, 58));
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedBefore(LocalDateTime.of(2024, 11, 15, 23, 35, 58));
        boolean apply = createdBeforeFilter.apply(recommendationRequestDto, requestFilterDto);
        assertTrue(apply);
    }

    @Test
    void testFailedFilter() {
        RecommendationRequest recommendationRequestDto = new RecommendationRequest()
                .setCreatedAt(LocalDateTime.of(2024, 11, 15, 23, 35, 58));
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedBefore(LocalDateTime.of(2024, 11, 14, 23, 35, 58));
        boolean apply = createdBeforeFilter.apply(recommendationRequestDto, requestFilterDto);
        assertFalse(apply);
    }
}