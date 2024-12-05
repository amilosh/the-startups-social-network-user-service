package school.faang.user_service.controller.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public long giveRecommendation(@RequestBody @Valid RecommendationDto recommendation)
            throws JsonProcessingException {
        return recommendationService.create(recommendation).getId();
    }

    @PutMapping
    public long updateRecommendation(@RequestBody @Valid RecommendationDto updated) {
        return recommendationService.update(updated).getId();
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/all_by_receiver")
    public List<RecommendationDto> getAllUserRecommendations(
            @RequestParam("receiverId") long receiverId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return recommendationService.getAllUserRecommendations(receiverId, page, size);
    }

    @GetMapping("/all_by_author")
    public List<RecommendationDto> getAllGivenRecommendations(
            @RequestParam("authorId") long authorId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return recommendationService.getAllGivenRecommendations(authorId, page, size);
    }
}
