package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(@Valid RecommendationRequestDto recommendationRequest) {
        recommendationRequestService.create(recommendationRequest);
        return recommendationRequest;
    }

    public List<RecommendationRequestDto> getRecommendationRequests(@Valid RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, @Valid RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
