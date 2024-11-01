package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.recommendation.RecommendationControllerValidator;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationControllerValidator recommendationControllerValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendationControllerValidator.validateDto(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        recommendationControllerValidator.validateDto(recommendationDto);
        return recommendationService.update(recommendationDto);
    }

    public boolean deleteRecommendation(Long recommendationId) {
        recommendationControllerValidator.validateRecommendationId(recommendationId);
        return recommendationService.delete(recommendationId);
    }
}
