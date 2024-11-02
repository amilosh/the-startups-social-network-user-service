package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    public static RecommendationService recommendationService;

    public void giveRecommendation(RecommendationDto recommendation) throws DataValidationException {
        validateRecommendation(recommendation);
        recommendationService.create(recommendation);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) throws DataValidationException {
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_EMPTY_AUTHOR);
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_EMPTY_RECEIVER);
        }
        if (StringUtils.isEmpty(recommendationDto.getContent())) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_EMPTY_CONTENT);
        }
    }
}
