package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreatedBeforeFilterTest {
    private final CreatedBeforeFilter createdBeforeFilter = new CreatedBeforeFilter();

    @Test
    void isFilterApplicableFieldIsPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedBefore(LocalDateTime.now());
        boolean filterApplicable = createdBeforeFilter.isFilterApplicable(requestFilterDto);
        assertTrue(filterApplicable);
    }

    @Test
    void isFilterApplicableFieldIsNotPresent() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        boolean filterApplicable = createdBeforeFilter.isFilterApplicable(requestFilterDto);
        assertFalse(filterApplicable);
    }

    @Test
    void passedFilter() {
        RecommendationRequest recommendationRequestDto = new RecommendationRequest()
                .setCreatedAt(LocalDateTime.of(2024, 11, 14, 23, 35, 58));
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedBefore(LocalDateTime.of(2024, 11, 15, 23, 35, 58));
        boolean apply = createdBeforeFilter.apply(recommendationRequestDto, requestFilterDto);
        assertTrue(apply);
    }

    @Test
    void failedFilter() {
        RecommendationRequest recommendationRequestDto = new RecommendationRequest()
                .setCreatedAt(LocalDateTime.of(2024, 11, 15, 23, 35, 58));
        RequestFilterDto requestFilterDto = new RequestFilterDto()
                .setCreatedBefore(LocalDateTime.of(2024, 11, 14, 23, 35, 58));
        boolean apply = createdBeforeFilter.apply(recommendationRequestDto, requestFilterDto);
        assertFalse(apply);
    }
}