package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import org.springframework.validation.ObjectError;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public long giveRecommendation(@Valid RecommendationDto recommendation, BindingResult bindingResult) {
        validateRecommendation(bindingResult);
        return recommendationService.create(recommendation).getId();
    }

    public long updateRecommendation(@Valid RecommendationDto updated, BindingResult bindingResult) {
        validateRecommendation(bindingResult);
        return recommendationService.update(updated).getId();
    }

    public long deleteRecommendation(long id) {
        return recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int size) {
        return recommendationService.getAllUserRecommendations(receiverId, page, size);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size){
        return recommendationService.getAllGivenRecommendations(authorId, page, size);
    }

    private void validateRecommendation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            String errorMessage = String.join("\n", errorMessages);
            throw new DataValidationException(errorMessage);
        }
    }
}
