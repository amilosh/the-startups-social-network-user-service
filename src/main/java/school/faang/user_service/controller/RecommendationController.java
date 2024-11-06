package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("api/recommendations")
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping()
    public ResponseEntity<RecommendationDto> createRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationService.create(recommendationDto));
    }

    @PutMapping()
    public ResponseEntity<RecommendationDto> updateRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        return ResponseEntity.ok(recommendationService.update(recommendationDto));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable @NotNull @Positive long recommendationId) {
        recommendationService.delete(recommendationId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/receivers/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(@PathVariable @Positive long receiverId) {
        return ResponseEntity.ok().body(recommendationService.getAllUserRecommendations(receiverId));
    }
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable @Positive long authorId) {
        return ResponseEntity.ok().body(recommendationService.getAllGivenRecommendations(authorId));
    }
}
