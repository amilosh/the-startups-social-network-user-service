package school.faang.user_service.services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;

@Service
@Component
public interface RecommendationRequestService {

    RecommendationRequestDto create(RecommendationRequestDto recommendationRequest);
}
