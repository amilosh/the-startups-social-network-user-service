package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    public Long create(RecommendationRequestDto recRequest) {
        long createdRequestId = 0;
        if (eligibleForRecommendation(recRequest)) {
            createdRequestId = recommendationRepository.create(
                    recRequest.getRequesterId(),
                    recRequest.getReceiverId(),
                    recRequest.getMessage()
            );
            recRequest.getSkills().forEach(skill ->
                    skillRequestRepository.create(recRequest.getId(), skill.getId()));
        }
        return createdRequestId;
    }

    private boolean usersExistInDb(RecommendationRequestDto recommendationRequest) {
        boolean usersExist = recommendationRequestRepository.checkTheUsersExistInDb(
                recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId());
        if (!usersExist) {
            System.err.println("The users don't exist in database");
        }
        return usersExist;
    }

    private boolean sixMonthHavePassed(RecommendationRequestDto recommendationRequest) {
        Optional<RecommendationRequest> latestRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId());

        if (latestRequest.isEmpty()) {
            return true;
        }

        LocalDateTime lastRequestDate = latestRequest.get().getCreatedAt();
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        return lastRequestDate.isBefore(sixMonthsAgo);
    }

    private boolean skillsExistInDb(RecommendationRequestDto recommendationRequest) {
        return recommendationRequest.getSkills().stream()
                .allMatch(skill -> skillRepository.existsByTitle(skill.getTitle()));
    }

    private boolean eligibleForRecommendation(RecommendationRequestDto recRequest) {
        return usersExistInDb(recRequest)
                && sixMonthHavePassed(recRequest)
                && skillsExistInDb(recRequest);
    }
}
