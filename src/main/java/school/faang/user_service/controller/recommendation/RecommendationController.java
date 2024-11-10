package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import org.springframework.validation.ObjectError;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public long giveRecommendation(@Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation).getId();
    }

    public long updateRecommendation(@Valid RecommendationDto updated) {
        return recommendationService.update(updated).getId();
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int size) {
        return recommendationService.getAllUserRecommendations(receiverId, page, size);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size) {
        return recommendationService.getAllGivenRecommendations(authorId, page, size);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private void validateRecommendation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        throw new DataValidationException(errorMessage);
    }
}
