package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.utilities.UrlUtils;

@RestController
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.RECOMMENDATION)
public class RecommendationRequestController {

    @Autowired
    private RecommendationRequestService recommendationRequestService;

    @PostMapping("/request")
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Recommendation request must contain a non-empty message.");
        }

        RecommendationRequestDto savedDto = recommendationRequestService.create(recommendationRequest);

        return ResponseEntity.ok().body(savedDto);
    }
}