package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class ExperienceBordersFilter implements SubscriptionRequestFilter {
    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        if (userFilter.getExperienceMax() != null && userFilter.getExperienceMin() == null) {
            if (user.getExperience() <= userFilter.getExperienceMax()) {
                return true;
            }
        } else if (userFilter.getExperienceMax() == null && userFilter.getExperienceMin() != null) {
            if (user.getExperience() >= userFilter.getExperienceMin()) {
                return true;
            }
        } else if (userFilter.getExperienceMax() != null && userFilter.getExperienceMin() != null) {
            if (user.getExperience() >= userFilter.getExperienceMin() &&
                    user.getExperience() <= userFilter.getExperienceMax()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getExperienceMax() != null || userFilter.getExperienceMin() != null;
    }
}
