package school.faang.user_service.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static school.faang.user_service.entity.RequestStatus.PENDING;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private static final int MONTHS_BEFORE_NEW_RECOMMENDATION = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserService userService;

    public void checkId(RecommendationDto recommendationDto) {
        if (recommendationDto.getId() == null) {
            throw new DataValidationException("Null recommendation id");
        }
        if (recommendationRepository.findById(recommendationDto.getId()).isEmpty()) {
            throw new DataValidationException("Recommendation not found");
        }
    }

    public void checkTimeInterval(RecommendationDto recommendationDto) {
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId());
        if (lastRecommendation.isPresent() && !checkMonthsBeforeNew(lastRecommendation.get())) {
            throw new DataValidationException("Must pass " + MONTHS_BEFORE_NEW_RECOMMENDATION
                    + " before new recommendation for the same user");
        }
    }

    public void checkSkillsExist(RecommendationDto recommendationDto) {
        List<Long> skillIds = recommendationDto
                .getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId).toList();
        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new DataValidationException("Skill does not exist");
        }
    }

    public void checkSkillsUnique(RecommendationDto recommendationDto) {
        List<Long> skillIds = recommendationDto
                .getSkillOffers()
                .stream()
                .mapToLong(SkillOfferDto::getSkillId)
                .boxed()
                .toList();
        Set<Long> uniqueSkillIds = new HashSet<>(skillIds);
        if (uniqueSkillIds.size() != skillIds.size()) {
            throw new DataValidationException("Skills must be unique");
        }
    }

    public void checkRequest(RecommendationDto recommendationDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository
                .findById(recommendationDto.getRequestId())
                .orElseThrow(() -> new DataValidationException("Request not found"));
        if (recommendationRequest.getStatus() != PENDING) {
            throw new DataValidationException("Request already processed");
        }
    }

    private boolean checkMonthsBeforeNew(Recommendation lastRecommendation) {
        return lastRecommendation.getCreatedAt()
                .isBefore(LocalDateTime.now().minusMonths(MONTHS_BEFORE_NEW_RECOMMENDATION));
    }

    public void checkMainValidation(RecommendationDto recommendationDto) {
        checkTimeInterval(recommendationDto);
        checkSkillsExist(recommendationDto);
        checkSkillsUnique(recommendationDto);
    }
}
