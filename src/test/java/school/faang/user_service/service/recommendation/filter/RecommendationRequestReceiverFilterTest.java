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

class RecommendationRequestReceiverFilterTest {
    private RecommendationRequestReceiverFilter recommendationRequestReceiverFilter;
    private RecommendationRequestFilterDto requestFilterDto;
    private Stream<RecommendationRequest> recommendationRequestStream;

    @BeforeEach
    void setUp() {
        recommendationRequestReceiverFilter = new RecommendationRequestReceiverFilter();
        requestFilterDto = new RecommendationRequestFilterDto();

        recommendationRequestStream = Stream.of(
                RecommendationRequest.builder()
                        .receiver(User.builder().id(1L).build())
                        .build(),
                RecommendationRequest.builder()
                        .receiver(User.builder().id(11L).build())
                        .build(),
                RecommendationRequest.builder()
                        .receiver(User.builder().id(11L).build())
                        .build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setReceiverIdPattern(11L);
        assertTrue(recommendationRequestReceiverFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestReceiverFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setReceiverIdPattern(11L);
        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestReceiverFilter
                .apply(recommendationRequestStream, requestFilterDto)
                .stream()
                .toList();

        assertEquals(2, resultRecommendationRequests.size());
        resultRecommendationRequests.forEach(recommendationRequest ->
                assertEquals(11L, recommendationRequest.getReceiver().getId()));
    }
}
