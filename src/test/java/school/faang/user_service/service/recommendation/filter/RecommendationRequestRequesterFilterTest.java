package school.faang.user_service.service.recommendation.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestRequesterFilterTest {
    private RecommendationRequestRequesterFilter recommendationRequestRequesterFilter;
    private RecommendationRequestFilterDto requestFilterDto;
    private Stream<RecommendationRequest> recommendationRequestStream;

    @BeforeEach
    void setUp() {
        recommendationRequestRequesterFilter = new RecommendationRequestRequesterFilter();
        requestFilterDto = new RecommendationRequestFilterDto();

        recommendationRequestStream = Stream.of(
                RecommendationRequest.builder()
                        .requester(User.builder().id(1L).build())
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(11L).build())
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(11L).build())
                        .build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setRequestIdPattern(11L);
        assertTrue(recommendationRequestRequesterFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestRequesterFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setRequestIdPattern(11L);
        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestRequesterFilter
                .apply(recommendationRequestStream, requestFilterDto)
                .stream()
                .toList();

        assertEquals(2, resultRecommendationRequests.size());
        resultRecommendationRequests.forEach(recommendationRequest ->
                assertEquals(11L, recommendationRequest.getRequester().getId()));
    }
}
