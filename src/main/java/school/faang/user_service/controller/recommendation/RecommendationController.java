package school.faang.user_service.controller.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.recommendation.ControllerRecommendationValidator;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ControllerRecommendationValidator recommendationValidator;

    public void updateRecommendation(RecommendationDto updated) {
    }

    public Long giveRecommendation(RecommendationDto recommendation) {
        log.info("The request {} for a recommendation has been received", recommendation);
        recommendationValidator.validateContentRecommendation(recommendation.getContent());
        return recommendationService.giveRecommendation(recommendation);
    }
}
