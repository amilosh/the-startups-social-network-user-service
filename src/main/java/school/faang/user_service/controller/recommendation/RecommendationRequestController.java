package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/recommendation-requests")
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping
    public List<RecommendationRequestDto> getRecommendationRequests(
            @Valid @ModelAttribute RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/{id}/reject")
    public RecommendationRequestDto rejectRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id,
            @Valid @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    @PutMapping("/{id}/accept")
    public ResponseRecommendationDto acceptRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id) {
        return recommendationRequestService.acceptRequest(id);
    }
}
