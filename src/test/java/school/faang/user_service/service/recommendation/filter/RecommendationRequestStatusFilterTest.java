package school.faang.user_service.service.recommendation.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestStatusFilterTest {
    private RecommendationRequestStatusFilter recommendationRequestStatusFilter;
    private RecommendationRequestFilterDto requestFilterDto;
    private Stream<RecommendationRequest> recommendationRequestStream;

    @BeforeEach
    void setUp() {
        recommendationRequestStatusFilter = new RecommendationRequestStatusFilter();
        requestFilterDto = new RecommendationRequestFilterDto();

        recommendationRequestStream = Stream.of(
                RecommendationRequest.builder().status(RequestStatus.ACCEPTED).build(),
                RecommendationRequest.builder().status(RequestStatus.REJECTED).build(),
                RecommendationRequest.builder().status(RequestStatus.PENDING).build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setStatusPattern(RequestStatus.PENDING);
        assertTrue(recommendationRequestStatusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestStatusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setStatusPattern(RequestStatus.PENDING);
        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestStatusFilter
                .apply(recommendationRequestStream, requestFilterDto)
                .stream()
                .toList();

        assertAll(
                () -> assertEquals(1, resultRecommendationRequests.size()),
                () -> assertEquals(RequestStatus.PENDING, resultRecommendationRequests.get(0).getStatus())
        );
    }
}
