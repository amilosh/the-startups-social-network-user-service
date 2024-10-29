package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.interfaces.RecommendationService;

@Validated
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/give")
    public ResponseEntity<Long> giveRecommendation(@RequestBody RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        Long recommendationId = recommendationService.create(recommendation);
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationId);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_CONTENT);
        }
        if (recommendation.getAuthorId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_AUTHOR);
        }
        if (recommendation.getReceiverId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_RECEIVER);
        }
    }
}
