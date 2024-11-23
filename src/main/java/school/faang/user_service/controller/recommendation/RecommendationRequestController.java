package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecommendationRequestDto> getRecommendationRequests(
            @Valid @ModelAttribute RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationRequestDto getRecommendationRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationRequestDto rejectRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id,
            @Valid @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseRecommendationDto acceptRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id) {
        return recommendationRequestService.acceptRequest(id);
    }
}
