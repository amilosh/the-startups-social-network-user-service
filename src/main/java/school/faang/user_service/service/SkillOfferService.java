package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private SkillOfferRepository skillOfferRepository;

    public int getCountSkillOffersForUser(Long skillId, Long userId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId, userId) ;
    }

    public Long getCountSkillOffersBySkill(Long skillId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId) ;
    }
}
