package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    public final RecommendationService recommendationService;

    @GetMapping("/user/receiver/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(@PathVariable long receiverId) {
        return new ResponseEntity<>(recommendationService.getAllUserRecommendations(receiverId), HttpStatus.OK);
    }

    @GetMapping("/user/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable long authorId) {
        return new ResponseEntity<>(recommendationService.getAllGivenRecommendations(authorId), HttpStatus.OK);
    }

    @PostMapping("/give")
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return new ResponseEntity<>(recommendationService.create(recommendation), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<RecommendationDto> updateRecommendation(@RequestBody @Valid RecommendationDto updated) {
        return new ResponseEntity<>(recommendationService.update(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
