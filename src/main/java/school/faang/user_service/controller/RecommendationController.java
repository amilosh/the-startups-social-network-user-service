package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    public static RecommendationService recommendationService;

    @PostMapping("/give")
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return new ResponseEntity<>(recommendationService.create(recommendation), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RecommendationDto> updateRecommendation(@PathVariable long id, @RequestBody RecommendationDto updatedRequestRecommendationDto) {
        validateRecommendation(updatedRequestRecommendationDto);
        return new ResponseEntity<>(recommendationService.update(id, updatedRequestRecommendationDto), HttpStatus.OK);
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
