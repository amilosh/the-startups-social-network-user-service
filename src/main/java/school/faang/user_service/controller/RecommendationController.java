package school.faang.user_service.controller;

import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Controller
public class RecommendationController {
    public static RecommendationService recommendationService;

    public int giveRecommendation(RecommendationDto recommendation) {

        //        recommendationService.giveRecommendation(recommendation);
        return 1;
    }
}
