package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.RecommendationService;

@Validated
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/give")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PutMapping("/update/{id}")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updatedRecommendationDto) {
        validateRecommendation(updatedRecommendationDto);
        return recommendationService.update(updatedRecommendationDto);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_CONTENT);
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_AUTHOR);
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_RECEIVER);
        }
    }
}
