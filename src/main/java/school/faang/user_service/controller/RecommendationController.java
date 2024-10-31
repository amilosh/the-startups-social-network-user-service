package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.RecommendationValidator;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendationValidator.validateDto(recommendationDto);
        return recommendationService.create(recommendationDto);
    }
}
