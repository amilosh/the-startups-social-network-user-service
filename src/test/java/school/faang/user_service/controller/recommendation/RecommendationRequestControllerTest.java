package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void requestRecommendationNullBody() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestController.requestRecommendation(null));
        assertEquals("Request body cannot be null", exception.getMessage());
    }

    @Test
    public void requestRecommendationSuccess() {
        recommendationRequestController.requestRecommendation(new RecommendationRequestDto());
        Mockito.verify(recommendationRequestService).create(Mockito.any(RecommendationRequestDto.class));
    }

    @Test
    public void getRecommendationRequestsSuccess() {
        recommendationRequestController.getRecommendationRequests(new RecommendationRequestFilterDto());
        Mockito.verify(recommendationRequestService).getRequests(Mockito.any(RecommendationRequestFilterDto.class));
    }

    @Test
    public void getRecommendationRequestSuccess() {
        recommendationRequestController.getRecommendationRequest(Mockito.anyLong());
        Mockito.verify(recommendationRequestService).getRequest(Mockito.anyLong());
    }

    @Test
    public void rejectRequestSuccess() {
        RecommendationRequestRejectionDto rejectionDto = new RecommendationRequestRejectionDto();
        recommendationRequestController.rejectRequest(1L, rejectionDto);
        Mockito.verify(recommendationRequestService).rejectRequest(1L, rejectionDto);
    }
}