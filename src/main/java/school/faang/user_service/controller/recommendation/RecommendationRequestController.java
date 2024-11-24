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
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/recommendation-requests")
@RequiredArgsConstructor
@Tag(name = "Recommendation Request Controller", description = "Controller for managing recommendation requests")
@ApiResponse(responseCode = "201", description = "Recommendation request created successfully")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Recommendation request not found")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @Operation(
            summary = "Request a recommendation",
            description = "Create a new recommendation request"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @Operation(
            summary = "Get recommendation requests",
            description = "Retrieve all recommendation requests filtered by specific criteria",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation requests retrieved successfully")
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecommendationRequestDto> getRecommendationRequests(
            @Valid @ModelAttribute RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @Operation(
            summary = "Get a recommendation request",
            description = "Retrieve a specific recommendation request by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation request retrieved successfully")
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationRequestDto getRecommendationRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @Operation(
            summary = "Reject a recommendation request",
            description = "Reject a recommendation request with a reason",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation request rejected successfully")
            }
    )
    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationRequestDto rejectRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id,
            @Valid @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    @Operation(
            summary = "Accept a recommendation request",
            description = "Accept a recommendation request and return the created recommendation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation request accepted successfully")
            }
    )
    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseRecommendationDto acceptRequest(
            @PathVariable @NotNull(message = "Recommendation request ID should not be null") Long id) {
        return recommendationRequestService.acceptRequest(id);
    }
}
