package school.faang.user_service.controller.recommendation;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;

@Component
@RestController
public class RecommendationRequestController {

    @GetMapping
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return new RecommendationRequestDto();
    }

}
