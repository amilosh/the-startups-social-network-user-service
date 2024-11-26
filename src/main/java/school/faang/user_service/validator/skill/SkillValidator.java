package school.faang.user_service.validator.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@Component
public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;

    public void validateSkillByMinSkillOffer(int skillOffers, long skillId, long userId) {
        if (skillOffers < MIN_SKILL_OFFERS) {
            log.warn("User {} doesn't have enough skill offers to acquire skill with id: {}", userId, skillId);
            throw new DataValidationException("User " + userId + " doesn't have enough skill offers to acquire skill with id: " + skillId);
        }
    }
}
