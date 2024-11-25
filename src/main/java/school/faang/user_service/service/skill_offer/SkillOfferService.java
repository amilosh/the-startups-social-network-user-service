package school.faang.user_service.service.skill_offer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public Long create(long skillId, long recommendationId) {
        return skillOfferRepository.create(skillId, recommendationId);
    }

    public void deleteAllByRecommendationId(long recommendationId){
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }
}
