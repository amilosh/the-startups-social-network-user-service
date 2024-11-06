package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestNotFoundException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {

    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validateUsersExistence(User requester, User receiver) {
        if (requester == null) {
            throw new IllegalArgumentException("Пользователя, запрашивающего рекомендацию не существует");
        }

        if (receiver == null) {
            throw new IllegalArgumentException("Пользователя, получающего рекомендацию не существует");
        }
    }

    public RecommendationRequest getAndValidateRecommendationRequest(Long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new RecommendationRequestNotFoundException(
                        "Запрос на рекомендацию с таким id не найден"));
    }

    public void validateRejectRequest(RecommendationRequest request) {
        if (request.getStatus() == RequestStatus.ACCEPTED || request.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException(
                    "Невозможно отклонить запрос на рекомендацию, поскольку он уже имеет статус " + request.getStatus());
        }
    }

    public void validateRequestFrequency(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(
                requesterId, receiverId);

        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getCreatedAt();
            if (lastRequestDate.isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new IllegalArgumentException("Запрос этому пользователю можно отправлять только раз в полгода");
            }
        }
    }
}
