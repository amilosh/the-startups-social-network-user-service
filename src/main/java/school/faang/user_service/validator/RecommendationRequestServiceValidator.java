package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RecommendationRequestServiceValidator {
    private final UserRepository userRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;

    public void validateExistsRequesterAndReceiverInDatabase(RecommendationRequestDto recommendationRequest) {
        Long requesterId = recommendationRequest.getRequesterId();
        Long receiverId = recommendationRequest.getReceiverId();

        log.info("Start validation: requester and receiver exist in database. Requester ID: {}, Receiver ID: {}",
                requesterId, receiverId);

        if (!userRepository.existsById(requesterId)) {
            throw new DataValidationException("requester with ID " + requesterId + " not found in database");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new DataValidationException("receiver with ID " + receiverId + " not found in database");
        }

        log.info("Validation passed: Requester and Receiver exist in database. Requester ID: {}, Receiver ID: {}",
                requesterId, receiverId);
    }

    public void validateSixMonthRequestLimit(RecommendationRequestDto recommendationRequestDto) {
        Long requesterId = recommendationRequestDto.getRequesterId();
        Long receiverId = recommendationRequestDto.getReceiverId();

        log.info("Start validation: six-month request limit. Requester ID: {}, Receiver ID: {}",
                requesterId, receiverId);

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

        log.info("Validation passed: six-month request limit. Requester ID: {}, Receiver ID: {}", requesterId, receiverId);
    }

    public void validateExistsSkillsInDatabase(RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillIds = recommendationRequestDto.getSkillIds();

        log.info("Start validation: all skills exist in database. Skill IDs: {}", skillIds);

        List<Long> skillsNotDatabase = skillIds.stream()
                .filter(id -> !(skillRepository.existsById(id)))
                .distinct()
                .toList();
        if (!skillsNotDatabase.isEmpty()) {
            throw new DataValidationException("The following skills were not found in the database: " + skillsNotDatabase);
        }

        log.info("Validation passed: all skills exist in database. Skill IDs: {}", skillIds);
    }
}
