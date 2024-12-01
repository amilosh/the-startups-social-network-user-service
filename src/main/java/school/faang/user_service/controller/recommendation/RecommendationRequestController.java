package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendation-request")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    @Operation(summary = "Create a new recommendation request", description = "Submit a new recommendation request with the necessary details.")
    @ApiResponse(responseCode = "201", description = "Recommendation request successfully created")
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequestDto createdRequest = recommendationRequestService.create(recommendationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping
    @Operation(summary = "Get all recommendation requests", description = "Retrieve a list of recommendation requests with optional filtering and pagination.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of recommendation requests")
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(
            @Valid @ModelAttribute RequestFilterDto filter) {
        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recommendation request by ID", description = "Retrieve a specific recommendation request using its unique identifier.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the recommendation request")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(
            @Parameter(description = "ID of the recommendation request to retrieve", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        RecommendationRequestDto dto = recommendationRequestService.getRequest(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a recommendation request", description = "Reject a specific recommendation request by providing a reason.")
    @ApiResponse(responseCode = "200", description = "Recommendation request successfully rejected")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(
            @Parameter(description = "ID of the recommendation request to reject", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            @Valid @RequestBody RejectionDto rejection) {
        RecommendationRequestDto dto = recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok(dto);
    }
}
