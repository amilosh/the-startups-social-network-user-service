package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;

@RestController
@RequestMapping("api/v1/recommendation-requests")
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        recommendationRequestValidator.validateRecommendationRequestDto(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping
    public List<RecommendationRequestDto> getRecommendationRequests(@ModelAttribute RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/{id}/reject")
    public RecommendationRequestDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
