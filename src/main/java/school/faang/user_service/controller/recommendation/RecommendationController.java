package school.faang.user_service.controller.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.recommendation.ControllerRecommendationValidator;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ControllerRecommendationValidator recommendationValidator;

    public void updateRecommendation(RecommendationDto upRecommendation) {
        log.info("A request has been received to update the recommendation {}", upRecommendation);
        recommendationValidator.validateContentRecommendation(upRecommendation.getContent());
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        log.info("A request has been received for a recommendation {}", recommendation);
        recommendationValidator.validateContentRecommendation(recommendation.getContent());
        return recommendationService.giveRecommendation(recommendation);
    }

    public void deleteRecommendation(RecommendationDto delRecommendation) {
        log.info("A request was received to delete the recommendation {}", delRecommendation);
    }

    public List<SkillOfferDto> getAllUserRecommendations(long receiverId) {

        return null;
    }
}
