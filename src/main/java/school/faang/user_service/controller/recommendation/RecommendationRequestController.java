package school.faang.user_service.controller.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Component
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(
            RecommendationRequestDto recRequest) {
        if (validateRequest(recRequest)) {
            long createdRequestId = recommendationRequestService.create(recRequest);
            recRequest.setId(createdRequestId);
        }
        return recRequest;
    }

    private boolean validateRequest(RecommendationRequestDto recommendationRequest) {
        String message = recommendationRequest.getMessage();
        if (message == null || message.isEmpty()) {
            return false;
        }
        return true;
    }

    @Autowired
    public RecommendationRequestController(
            RecommendationRequestService recommendationRequestService) {
        this.recommendationRequestService = recommendationRequestService;
    }
}
