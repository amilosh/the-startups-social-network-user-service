package school.faang.user_service.filters.recommendation_request;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationCreatedAfterFilterTest {
    private final RecommendationCreatedAfterFilter createdAfterFilter = new RecommendationCreatedAfterFilter();

    @Test
    void testIsFilterApplicableFieldIsPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedAfter(LocalDateTime.now());
        boolean filterApplicable = createdAfterFilter.isFilterApplicable(requestFilterDto);
        assertTrue(filterApplicable);
    }

    @Test
    void testIsFilterApplicableFieldIsNotPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        boolean filterApplicable = createdAfterFilter.isFilterApplicable(requestFilterDto);
        assertFalse(filterApplicable);
    }

    @Test
    void testPassedFilter(){
        RecommendationRequest recommendationRequestDto = new RecommendationRequest()
                .setCreatedAt(LocalDateTime.of(2024, 11, 15, 23, 35, 58));
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedAfter(LocalDateTime.of(2024, 11, 14, 23, 35, 58));
        boolean apply = createdAfterFilter.apply(recommendationRequestDto, requestFilterDto);
        assertTrue(apply);
    }

    @Test
    void testFailedFilter(){
        RecommendationRequest recommendationRequestDto = new RecommendationRequest()
                .setCreatedAt(LocalDateTime.of(2024, 11, 14, 23, 35, 58));
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedAfter(LocalDateTime.of(2024, 11, 15, 23, 35, 58));
        boolean apply = createdAfterFilter.apply(recommendationRequestDto, requestFilterDto);
        assertFalse(apply);
    }
}