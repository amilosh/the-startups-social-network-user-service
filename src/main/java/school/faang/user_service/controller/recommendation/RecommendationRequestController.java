package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.RECOMMENDATION)
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(UrlUtils.REQUEST)
    public RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping(UrlUtils.REQUEST)
    public List<RecommendationRequestDto> getRecommendationRequests(@Valid @RequestBody RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping(UrlUtils.REQUEST + UrlUtils.ID)
    public RecommendationRequestDto getRecommendationRequest(@PathVariable("id") @Min(1) Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping(UrlUtils.REQUEST + UrlUtils.ID + UrlUtils.REJECT)
    public RecommendationRequestDto rejectRecommendationRequest(@PathVariable("id") @Min(1) Long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}