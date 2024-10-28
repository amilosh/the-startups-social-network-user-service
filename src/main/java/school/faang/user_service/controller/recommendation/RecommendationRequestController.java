package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Slf4j
@Component
@RestController
@RequestMapping("/api/v1/recommendRequest")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto requestRecommendation(@RequestBody
                                                          @Valid RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(@Valid RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @Valid RecommendationRejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
