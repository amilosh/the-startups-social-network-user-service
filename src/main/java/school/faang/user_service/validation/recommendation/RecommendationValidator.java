package school.faang.user_service.validation.recommendation;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.skill.SkillValidation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@Data
public class RecommendationValidator {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillService skillService;
    private final SkillValidation skillValidation;


    public void validateDto(RecommendationDto recommendationDto) {

        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("Author id cannot be null");
        }

        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("Receiver id cannot be null");
        }

        if (recommendationDto.getReceiverId().equals(recommendationDto.getAuthorId())) {
            throw new DataValidationException("You cannot recommend yourself");
        }

        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation cannot be empty");
        }
    }

    public void validateTimeAfterLastRecommendation(RecommendationDto recommendationDto) {
        List<SkillOffer> userSKillOffers = skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId());

        LocalDate lastRecommendationDate = userSKillOffers.stream()
                .map(SkillOffer::getRecommendation)
                .filter(recommendation -> recommendation.getAuthor().getId().equals(recommendationDto.getAuthorId()))
                .map(Recommendation::getCreatedAt)
                .map(LocalDateTime::toLocalDate)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (lastRecommendationDate != null &&
                lastRecommendationDate.isAfter(LocalDate.now().minusMonths(6))) {
            throw new DataValidationException("Less 6 months passed since last recommendation " + lastRecommendationDate);
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
