package school.faang.user_service.validation.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private static final int RECOMMENDATION_COOLDOWN_MONTHS = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillValidator skillValidator;

    public void validateAuthorAndReceiverId(RecommendationDto recommendationDto) {
        if (recommendationDto.getReceiverId().equals(recommendationDto.getAuthorId())) {
            throw new DataValidationException("You cannot recommend yourself");
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
            if (lastRecommendationDate.isAfter(LocalDate.now().minusMonths(RECOMMENDATION_COOLDOWN_MONTHS))) {
                throw new DataValidationException("Less " + RECOMMENDATION_COOLDOWN_MONTHS +
                        " months passed since last recommendation " + lastRecommendationDate);
            }
        }
    }

    public void validateSkillExists(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOffer -> {
            if (!skillValidator.validateSkillExists(skillOffer.getSkillId())) {
                throw new DataValidationException("Skill doesn't exist " + skillOffer.getSkillId());
            }
        });
    }
}
