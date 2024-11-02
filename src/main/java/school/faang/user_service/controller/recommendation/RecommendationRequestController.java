package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(
            @Valid @ModelAttribute RequestFilterDto filter) {
        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }
}
