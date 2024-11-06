package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public int getCountSkillOffersForUser(Long skillId, Long userId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId, userId) ;
    }

    public Long getCountSkillOffersBySkill(Long skillId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId) ;
    }

    public void deleteAllByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public long create(long skillId, long recommendationId) {
        return skillOfferRepository.create(skillId, recommendationId);
    }

    public List<SkillOffer> findAllByUserId(long userId) {
        return skillOfferRepository.findAllByUserId(userId);
    }
}
