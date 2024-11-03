package school.faang.user_service.validation.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidation;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private static final int RECOMMENDATION_COOLDOWN_MONTHS = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillValidation skillValidation;

    public void validateDto(RecommendationDto recommendationDto) {

        if (recommendationDto.getAuthorId() == null || recommendationDto.getAuthorId() == 0) {
            throw new DataValidationException("Author id cannot be null");
        }

        if (recommendationDto.getAuthorId() < 0) {
            throw new DataValidationException("Author id cannot be negative");
        }

        if (recommendationDto.getReceiverId() == null || recommendationDto.getReceiverId() == 0) {
            throw new DataValidationException("Receiver id cannot be null");
        }

        if (recommendationDto.getReceiverId() < 0) {
            throw new DataValidationException("Receiver id cannot be negative");
        }

        if (recommendationDto.getReceiverId().equals(recommendationDto.getAuthorId())) {
            throw new DataValidationException("You cannot recommend yourself");
        }

        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation cannot be empty");
        }

        if (recommendationDto.getSkillOffers() == null) {
            throw new DataValidationException("Skill offers list cannot be null");
        }
    }

    public void validateId(Long id) {
        if (id == null || id == 0) {
            throw new DataValidationException("Id cannot be null");
        }

        if (id <= 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }

    public void validateSkillAndTimeRequirementsForGuarantee(RecommendationDto recommendationDto) {
        validateTimeAfterLastRecommendation(recommendationDto);
        validateSkillExists(recommendationDto);
    }

    public void validateRecommendationExistsById(Long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new DataValidationException("Recommendation with id #" + id + " doesn't exist in the system");
        }
    }

    public void validateTimeAfterLastRecommendation(RecommendationDto dto) {
        Optional<Recommendation> lastRecommendation = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(dto.getAuthorId(), dto.getReceiverId());

        if (lastRecommendation.isPresent()) {
            LocalDate lastRecommendationDate = lastRecommendation.get().getCreatedAt().toLocalDate();
            if (lastRecommendationDate != null &&
                    lastRecommendationDate.isAfter(LocalDate.now().minusMonths(RECOMMENDATION_COOLDOWN_MONTHS))) {
                throw new DataValidationException("Less " + RECOMMENDATION_COOLDOWN_MONTHS +
                        " months passed since last recommendation " + lastRecommendationDate);
            }
        }
    }

    public void validateSkillExists(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOffer -> {
            if (!skillValidation.validateSkillExists(skillOffer.getSkillId())) {
                throw new DataValidationException("Skill doesn't exist " + skillOffer.getSkillId());
            }
        });
    }
}
