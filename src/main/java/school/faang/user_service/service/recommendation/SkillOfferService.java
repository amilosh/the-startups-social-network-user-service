package school.faang.user_service.service.recommendation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.validation.SkillOfferValidation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferValidation skillOfferValidation;

    public void saveSkillOffers(List<SkillOfferDto> skillOffers, @NonNull Long recommendationId) {
        if (!recommendationRepository.findById(recommendationId).isPresent()) {
            throw new DataValidationException("Recommendation with id " + recommendationId + " not found");
        }
        skillOffers.forEach(skillOffer -> {
            skillOfferValidation.validate(skillOffer);
            Skill skill = skillRepository.findById(skillOffer.getSkillId())
                    .orElseThrow(() -> new DataValidationException("Skill not found"));
            long id = skillOfferRepository.create(skill.getId(), recommendationId);
            skillOffer.setId(id);
        });
    }

    public void deleteAllByRecommendationId(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
    }
}
