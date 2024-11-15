package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestServiceValidator {
    private final UserRepository userRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;

    public void validateExistsRequesterAndReceiverInDatabase(RecommendationRequestDto recommendationRequest) {
        Long requesterId = recommendationRequest.getRequesterId();
        Long receiverId = recommendationRequest.getReceiverId();
        if (!userRepository.existsById(requesterId)) {
            throw new DataValidationException("requester with ID " + requesterId + " not found in database");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new DataValidationException("receiver with ID " + receiverId + " not found in database");
        }
    }

    public void validateSixMonthRequestLimit(RecommendationRequestDto recommendationRequestDto) {
        Long requesterId = recommendationRequestDto.getRequesterId();
        Long receiverId = recommendationRequestDto.getReceiverId();
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        if (latestPendingRequest.isPresent()) {
            RecommendationRequest recommendationRequest = latestPendingRequest.get();

            LocalDateTime createdAt = recommendationRequest.getCreatedAt();
            LocalDateTime now = LocalDateTime.now();

            long betweenMonths = ChronoUnit.MONTHS.between(createdAt, now);
            int minLimitMonths = 6;
            if (betweenMonths < minLimitMonths) {
                throw new DataValidationException("no more than 6 months have passed since the last request with status 1, " +
                        "between months: " + betweenMonths + ", ID RecommendationRequest: " + recommendationRequest.getId());
            }
        }
    }

    public void validateExistsSkillsInDatabase(RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillIds = recommendationRequestDto.getSkillIds();

        List<Long> skillsNotDatabase = skillIds.stream()
                .filter(id -> !(skillRepository.existsById(id)))
                .distinct()
                .toList();
        if (!skillsNotDatabase.isEmpty()) {
            throw new DataValidationException("The following skills were not found in the database: " + skillsNotDatabase);
        }
    }
}
