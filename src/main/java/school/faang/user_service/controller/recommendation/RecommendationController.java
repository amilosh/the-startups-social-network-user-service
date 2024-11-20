package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping()
    public ResponseRecommendationDto giveRecommendation(
            @Valid @RequestBody RequestRecommendationDto requestRecommendationDto) {
        return recommendationService.create(requestRecommendationDto);
    }

    @PutMapping()
    public ResponseRecommendationDto updateRecommendation(
            @PathVariable @NotNull(message = "Recommendation ID should not be null") Long id,
            @Valid @RequestBody RequestRecommendationDto updatedRequestRecommendationDto) {
        return recommendationService.update(id, updatedRequestRecommendationDto);
    }

    @DeleteMapping()
    public void deleteRecommendation(
            @PathVariable @NotNull(message = "Recommendation ID should not be null") Long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/received/{receiverId}")
    public List<ResponseRecommendationDto> getAllUserRecommendations(
            @PathVariable @NotNull(message = "Receiver ID should not be null") Long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/given/{authorId}")
    public List<ResponseRecommendationDto> getAllGivenRecommendations(
            @PathVariable @NotNull(message = "Author ID should not be null") Long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
