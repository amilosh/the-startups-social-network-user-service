package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.utilities.RestUrlsUtil;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;

@RestController
@RequestMapping(RestUrlsUtil.MAIN_URL + RestUrlsUtil.V1)
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    @PostMapping(RestUrlsUtil.CREATE + RestUrlsUtil.RECOMMENDATION)
    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateContent(recommendation.getContent());
        return recommendationService.create(recommendation);
    }

    @PutMapping(RestUrlsUtil.UPDATE + RestUrlsUtil.RECOMMENDATION)
    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateContent(recommendation.getContent());
        return recommendationService.updateRecommendation(recommendation);
    }

    @DeleteMapping(RestUrlsUtil.DELETE + RestUrlsUtil.RECOMMENDATION)
    public void deleteRecommendation(long id) {
        recommendationService.deleteRecommendation(id);
    }

    @GetMapping(RestUrlsUtil.GET + RestUrlsUtil.RECEIVER_SKILL_OFFERS)
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping(RestUrlsUtil.GET + RestUrlsUtil.AUTHOR_SKILL_OFFERS)
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
