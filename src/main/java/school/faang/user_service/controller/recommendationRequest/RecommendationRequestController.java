package school.faang.user_service.controller.recommendationRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestRejectionDto;
import school.faang.user_service.service.recommendationRequest.RecommendationRequestService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(@Valid RecommendationRequestDto recommendationRequest) {
        log.info("Requesting recommendation: {}", recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        log.info("Retrieving recommendation requests with filter: {}", filter);
        return recommendationRequestService.getRequest(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        log.info("Retrieving recommendation request with id: {}", id);
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, @Valid RecommendationRequestRejectionDto recommendationRequestRejectionDto) {
        log.info("Rejecting recommendation request with id: {} and reason: {}", id, recommendationRequestRejectionDto);
        return recommendationRequestService.rejectRequest(id, recommendationRequestRejectionDto);
    }
}
