package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestNotFoundException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillValidator skillValidator;

    public void validateUsersExistence(User requester, User receiver) {
        if (requester == null) {
            throw new IllegalArgumentException("Requester was not provided");
        }

        if (receiver == null) {
            throw new IllegalArgumentException("Receiver was not provided");
        }
    }

    public void validateSkillsExistence(List<Long> skillsIds) {
        if (skillsIds != null && !skillsIds.isEmpty()) {
            skillValidator.validateSkills(skillsIds);
        }
    }

    public RecommendationRequest validateAndGetRecommendationRequest(Long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new RecommendationRequestNotFoundException(
                        "Recommendation request with this Id was not found"));
    }

    public void validateRejectRequest(RecommendationRequest request) {
        if (request.getStatus() == RequestStatus.ACCEPTED || request.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException(
                    "Impossible to reject recommendation request since it already has status " + request.getStatus());
        }
    }

    public void validateRequestFrequency(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(
                requesterId, receiverId);

        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getCreatedAt();
            if (lastRequestDate.isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new IllegalArgumentException("Recommendation request must be sent once in 6 months");
            }
        }
    }
}
