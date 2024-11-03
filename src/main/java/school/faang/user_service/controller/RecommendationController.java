package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    public static RecommendationService recommendationService;

    @GetMapping("/user/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/user/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    @PostMapping("/give")
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return new ResponseEntity<>(recommendationService.create(recommendation), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<RecommendationDto> updateRecommendation(@RequestBody RecommendationDto updated) {
        validateRecommendation(updated);
        return new ResponseEntity<>(recommendationService.update(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
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
