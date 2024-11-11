package school.faang.user_service.validator.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;

    public void validateSkillByMinSkillOffer(int skillOffers, long skillId, long userId) {
        if (skillOffers < MIN_SKILL_OFFERS) {
            throw new DataValidationException("User " + userId + " doesn't have enough skill offers to acquire skill with id: " + skillId);
        }
    }
}
