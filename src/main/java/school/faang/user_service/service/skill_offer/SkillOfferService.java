package school.faang.user_service.service.skill_offer;

import school.faang.user_service.repository.recommendation.SkillOfferRepository;

public class SkillOfferService {
    private SkillOfferRepository skillOfferRepository;

    public Long create(long skillId, long recommendationId) {
        return skillOfferRepository.create(skillId, recommendationId);
    }

    public void deleteAllByRecommendationId(long recommendationId){
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }
}
