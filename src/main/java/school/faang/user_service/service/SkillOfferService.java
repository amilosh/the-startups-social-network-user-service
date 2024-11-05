package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Component
@RequiredArgsConstructor
public class SkillOfferService {
    private SkillOfferRepository skillOfferRepository;

    public int getCountSkillOffersForUser(Long skillId, Long userId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId, userId) ;
    }

    public int getCountSkillOffersBySkill(Long skillId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId) ;
    }
}
