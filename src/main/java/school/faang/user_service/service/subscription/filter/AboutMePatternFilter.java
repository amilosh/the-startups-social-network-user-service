package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class AboutMePatternFilter implements SubscriptionRequestFilter {
    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        String aboutUser = user.getAboutMe().toLowerCase();
        String aboutMePattern = userFilter.getAboutMePattern().toLowerCase();

        return aboutUser.matches(".*" + aboutMePattern + ".*");
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getAboutMePattern() != null;
    }
}
