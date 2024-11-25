package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation Controller", description = "Controller for managing recommendations")
@ApiResponse(responseCode = "201", description = "Recommendation successfully created")
@ApiResponse(responseCode = "204", description = "Recommendation successfully deleted")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(
            summary = "Create a new recommendation",
            description = "Create and save a new recommendation in the system."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseRecommendationDto giveRecommendation(
            @Valid @RequestBody RequestRecommendationDto requestRecommendationDto) {
        return recommendationService.create(requestRecommendationDto);
    }

    @Operation(
            summary = "Update an existing recommendation",
            description = "Update details of an existing recommendation by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation successfully updated")
            }
    )
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseRecommendationDto updateRecommendation(
            @PathVariable @NotNull(message = "Recommendation ID should not be null") Long id,
            @Valid @RequestBody RequestRecommendationDto updatedRequestRecommendationDto) {
        return recommendationService.update(id, updatedRequestRecommendationDto);
    }

    @Operation(
            summary = "Delete a recommendation",
            description = "Delete a recommendation by its ID.",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Recommendation not found")
            }
    )
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecommendation(
            @PathVariable @NotNull(message = "Recommendation ID should not be null") Long id) {
        recommendationService.delete(id);
    }

    @Operation(
            summary = "Get all received recommendations",
            description = "Retrieve all recommendations received by a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/received/{receiverId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseRecommendationDto> getAllUserRecommendations(
            @PathVariable @NotNull(message = "Receiver ID should not be null") Long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @Operation(
            summary = "Get all given recommendations",
            description = "Retrieve all recommendations created by a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/given/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseRecommendationDto> getAllGivenRecommendations(
            @PathVariable @NotNull(message = "Author ID should not be null") Long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}

//TODO добавить фильтрДто с двумя полями, объединить два метода запроса в один
