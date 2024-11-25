package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

@Component
public class SkillPatternFilter implements SubscriptionRequestFilter {
    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        String skillPattern = userFilter.getSkillPattern().toLowerCase();

        for (Skill userSkill : user.getSkills()) {
            String userSkillTitle = userSkill.getTitle().toLowerCase();

            if (userSkillTitle.matches(".*" + skillPattern + ".*")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getSkillPattern() != null;
    }
}
