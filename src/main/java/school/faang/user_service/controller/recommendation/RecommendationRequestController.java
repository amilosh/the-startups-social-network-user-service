package school.faang.user_service.controller.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService) {
        this.recommendationRequestService = recommendationRequestService;
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (!recommendationRequestService.isMessageEmpty(recommendationRequest)) {
            recommendationRequestService.create(recommendationRequest);
        }
        return recommendationRequest;
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(RequestFilterDto filter) {
        return recommendationRequestService.getRequest(filter);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
