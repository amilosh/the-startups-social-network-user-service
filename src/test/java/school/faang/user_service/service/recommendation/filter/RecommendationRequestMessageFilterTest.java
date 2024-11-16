package school.faang.user_service.service.recommendation.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestMessageFilterTest {
    private RecommendationRequestMessageFilter recommendationRequestMessageFilter;
    private RecommendationRequestFilterDto requestFilterDto;
    private Stream<RecommendationRequest> recommendationRequestStream;

    @BeforeEach
    void setUp() {
        recommendationRequestMessageFilter = new RecommendationRequestMessageFilter();
        requestFilterDto = new RecommendationRequestFilterDto();

        recommendationRequestStream = Stream.of(
                RecommendationRequest.builder().message("test Message").build(),
                RecommendationRequest.builder().message("some text").build(),
                RecommendationRequest.builder().message("some Message").build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setMessagePattern("testMessage");
        assertTrue(recommendationRequestMessageFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestMessageFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setMessagePattern("Mess");
        List<RecommendationRequest> recommendationRequests = recommendationRequestMessageFilter
                .apply(recommendationRequestStream, requestFilterDto)
                .stream()
                .toList();

        assertEquals(2, recommendationRequests.size());
        recommendationRequests.forEach(recommendationRequest ->
                assertTrue(recommendationRequest.getMessage().contains(requestFilterDto.getMessagePattern())));
    }
}
