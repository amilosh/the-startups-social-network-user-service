package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationControllerValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendationControllerValidator.validateDto(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        recommendationControllerValidator.validateDto(recommendationDto);
        return recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(Long recommendationId) {
        recommendationControllerValidator.validateId(recommendationId);
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        recommendationControllerValidator.validateId(receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        recommendationControllerValidator.validateId(authorId);
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
