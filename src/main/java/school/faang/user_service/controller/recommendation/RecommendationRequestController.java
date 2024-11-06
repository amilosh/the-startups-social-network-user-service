package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Validated
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @Validated
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @Validated
    public RecommendationRequestDto getRecommendationRequest(@Positive long id) {
        return recommendationRequestService.getRequest(id);
    }

    @Validated
    public RecommendationRequestDto rejectRequest(@Positive long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
