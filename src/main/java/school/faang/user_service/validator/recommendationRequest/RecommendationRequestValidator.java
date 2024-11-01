package school.faang.user_service.validator.recommendationRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RecommendationRequestValidator {
    private static final int SIX_MONTHS_AGO = 6;

    public void validateCreate(RecommendationRequestDto recommendationRequestDto,
                               RecommendationRequestRepository recommendationRequestRepository,
                               UserRepository userRepository,
                               SkillRepository skillRepository) {
        validateUserExists(recommendationRequestDto.getRequesterId(), userRepository);
        validateUserExists(recommendationRequestDto.getReceiverId(), userRepository);
        validateNoRecentRequest(recommendationRequestDto, recommendationRequestRepository);
        validateSkills(recommendationRequestDto.getSkillList(), skillRepository);
    }

    public void validateNoRecentRequest(RecommendationRequestDto recommendationRequestDto,
                                        RecommendationRequestRepository recommendationRequestRepository) {
        RecommendationRequest latestPendingRequest = recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("No existing request found"));

        if (ChronoUnit.MONTHS.between(latestPendingRequest.getCreatedAt(), recommendationRequestDto.getCreatedAt()) < SIX_MONTHS_AGO) {
            log.error("Recent recommendation request exists for Requester ID: {} and Recipient ID: {}",
                    recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
            throw new DataValidationException("Request for recommendation can only be submitted once every 6 months");
        }

        log.info("No recent recommendation request found for Requester ID: {} and Recipient ID: {}",
                recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
    }

    public void validateSkills(List<Long> skillIds, SkillRepository skillRepository) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new DataValidationException("Skill IDs cannot be null or empty");
        }

        Set<Long> existingSkillIds = skillRepository.findAllById(skillIds).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        for (Long skillId : skillIds) {
            if (!existingSkillIds.contains(skillId)) {
                throw new DataValidationException("Skill with ID " + skillId + " does not exist in the database");
            }
        }
    }

    public void validateRequesterAndReceiverNotNull(Long id) {
        if (id == null) {
            log.error("id should not be equal null: {}", id);
            throw new DataValidationException("id should not be equal null");
        }
    }

    public void validateUserExists(Long id, UserRepository userRepository) {
        validateRequesterAndReceiverNotNull(id);
        userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(id + " does not exist"));
    }

    public void validateOfFilterAvailability(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        if (recommendationRequestFilterDto == null) {
            log.error("The recommendation request filter cannot be null");
            throw new DataValidationException("The recommendation request filter cannot be null");
        }
    }

    public RecommendationRequest validateRequestExists(long id, RecommendationRequestRepository recommendationRequestRepository) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Recommendation request with ID " + id + " not found"));
        validateRequestStatus(recommendationRequest);
        return recommendationRequest;
    }

    public void validateRequestStatus(RecommendationRequest recommendationRequest) {
        if (recommendationRequest.getStatus() == RequestStatus.REJECTED) {
            log.warn("Recommendation request with ID: {} has already been rejected", recommendationRequest.getId());
            throw new DataValidationException("Request has already been rejected");
        }

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            log.warn("Recommendation request with ID: {} has already been processed", recommendationRequest.getId());
            throw new DataValidationException("The request has already been processed");
        }
    }
}
