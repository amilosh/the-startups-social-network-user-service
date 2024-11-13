package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/recommendationrequest")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequestDto createdRequest = recommendationRequestService.create(recommendationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(
            @Valid RequestFilterDto filter) {
        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable @NotNull @Min(1) Long id) {
        RecommendationRequestDto dto = recommendationRequestService.getRequest(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(
            @PathVariable @NotNull @Min(1) Long id, @Valid @RequestBody RejectionDto rejection) {
        RecommendationRequestDto dto = recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok(dto);
    }
}
