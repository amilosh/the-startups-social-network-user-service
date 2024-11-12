package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/recommendationRequest")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping("/search")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/get/{id}")
    public RecommendationRequestDto getRecommendationRequest(@Parameter(description = "requestId")
                                                             @PathVariable @Positive long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/reject")
    public RecommendationRequestDto rejectRequest(@RequestBody RecommendationRejectionDto rejection) {
        return recommendationRequestService.rejectRequest(rejection);
    }
}
