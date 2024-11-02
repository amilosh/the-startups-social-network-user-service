package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;


@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody @Validated RecommendationDto recommendationDto) {
        RecommendationDto createdRecommendation = recommendationService.create(recommendationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecommendation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @PathVariable long id,
            @RequestBody @Validated RecommendationDto recommendationDto) {

        RecommendationDto updatedRecommendation = recommendationService.update(id, recommendationDto);
        return ResponseEntity.ok(updatedRecommendation);
    }
}
