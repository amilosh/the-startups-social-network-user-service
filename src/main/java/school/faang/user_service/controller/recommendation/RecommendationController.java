package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.utilities.RestUrlsUtil;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.List;

@RestController
@RequestMapping(RestUrlsUtil.MAIN_URL + RestUrlsUtil.V1 + RestUrlsUtil.SKILLS)
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    @PostMapping(RestUrlsUtil.RECOMMENDATION)
    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateContent(recommendation.getContent());
        return recommendationService.createRecommendation(recommendation);
    }

    @PutMapping(RestUrlsUtil.RECOMMENDATION)
    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateContent(recommendation.getContent());
        return recommendationService.updateRecommendation(recommendation);
    }

    @DeleteMapping(RestUrlsUtil.ID + RestUrlsUtil.RECOMMENDATION)
    public void deleteRecommendation(@PathVariable("id") @Min(1) Long id) {
        recommendationService.deleteRecommendation(id);
    }

    @GetMapping(RestUrlsUtil.ID + RestUrlsUtil.RECEIVER_SKILL_OFFERS)
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable("id") @Min(1) Long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping(RestUrlsUtil.ID + RestUrlsUtil.AUTHOR_SKILL_OFFERS)
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable("id") @Min(1) Long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
