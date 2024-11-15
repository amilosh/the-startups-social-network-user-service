package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    public final RecommendationService recommendationService;

    @Operation(summary = "Get all recommendations for a user by receiver ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations found successfully")
    })
    @GetMapping("/receivers/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(@PathVariable long receiverId) {
        return new ResponseEntity<>(recommendationService.getAllUserRecommendations(receiverId), HttpStatus.OK);
    }

    @Operation(summary = "Get all recommendations given by a user by author ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations found successfully")
    })
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable long authorId) {
        return new ResponseEntity<>(recommendationService.getAllGivenRecommendations(authorId), HttpStatus.OK);
    }

    @Operation(summary = "Give a recommendation to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recommendation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid recommendation data provided")
    })
    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return new ResponseEntity<>(recommendationService.create(recommendation), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a recommendation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid recommendation data provided"),
            @ApiResponse(responseCode = "404", description = "Recommendation not found")
    })
    @PutMapping
    public ResponseEntity<RecommendationDto> updateRecommendation(@RequestBody @Valid RecommendationDto updated) {
        return new ResponseEntity<>(recommendationService.update(updated), HttpStatus.OK);
    }

    @Operation(summary = "Delete a recommendation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recommendation deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
