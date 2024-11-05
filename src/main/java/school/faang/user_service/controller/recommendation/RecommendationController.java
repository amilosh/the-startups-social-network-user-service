package school.faang.user_service.controller.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.recommendation.ControllerRecommendationValidator;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ControllerRecommendationValidator recommendationValidator;

    public RecommendationDto updateRecommendation(RecommendationDto updateRecommendationDto) {
        log.info("A request has been received to update the recommendation {}", updateRecommendationDto);
        recommendationValidator.validateContentRecommendation(updateRecommendationDto.getContent());
        return recommendationService.updateRecommendation(updateRecommendationDto);
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        log.info("A request has been received for a recommendation {}", recommendationDto);
        recommendationValidator.validateContentRecommendation(recommendationDto.getContent());
        return recommendationService.giveRecommendation(recommendationDto);
    }

    public void deleteRecommendation(RecommendationDto delRecommendationDto) {
        log.info("A request was received to delete the recommendation {}", delRecommendationDto);
        recommendationService.deleteRecommendation(delRecommendationDto);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        log.info("Request to receive all user {} recommendations", receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId){
        log.info("Request to receive all recommendations created by the user {}", authorId);
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
