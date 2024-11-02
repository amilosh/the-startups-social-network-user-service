package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationServiceValidator recommendationServiceValidator;

    public void deleteAllByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public long create(long skillId, long recommendationId) {
        return skillOfferRepository.create(skillId, recommendationId);
    }

    public List<SkillOffer> findAllByUserId(long userId) {
        return skillOfferRepository.findAllByUserId(userId);
    }

    public void saveSkillOffers(Recommendation recommendation) {
        recommendationServiceValidator.validateRecommendationExistsById(recommendation.getId());
        recommendation.getSkillOffers().forEach(skillOffer ->
                create(skillOffer.getSkill().getId(), recommendation.getId())
        );
    }
}
