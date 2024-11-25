package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/recommendation")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        log.info("A request was received to create a recommendation request: {}", recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping("/requests")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        log.info("A request has been received to receive recommendation requests matching the filter: {}", filter);
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/request/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable @Positive(message = "ID must be positive") long id) {
        log.info("Received a request to receive a recommendation request by id: {}", id);
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/request/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable @Positive(message = "ID must be positive") long id, @Valid @RequestBody RejectionDto rejection) {
        log.info("A request was received to reject a recommendation request, by id: {}, with a rejection: {}", id, rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
