package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/give")
    public ResponseRecommendationDto giveRecommendation(@RequestBody RequestRecommendationDto requestRecommendationDto) {
        validateRecommendation(requestRecommendationDto);
        return recommendationService.create(requestRecommendationDto);
    }

    @PutMapping("/update/{id}")
    public ResponseRecommendationDto updateRecommendation(@PathVariable long id, @RequestBody RequestRecommendationDto updatedRequestRecommendationDto) {
        validateRecommendation(updatedRequestRecommendationDto);
        return recommendationService.update(id, updatedRequestRecommendationDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/user/{receiverId}")
    public List<ResponseRecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/user/{authorId}")
    public List<ResponseRecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private void validateRecommendation(RequestRecommendationDto requestRecommendationDto) {
        if (requestRecommendationDto.getContent() == null || requestRecommendationDto.getContent().isBlank()) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_CONTENT);
        }
        if (requestRecommendationDto.getAuthorId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_AUTHOR);
        }
        if (requestRecommendationDto.getReceiverId() == null) {
            throw new DataValidationException(ErrorMessage.RECOMMENDATION_RECEIVER);
        }
    }
}
