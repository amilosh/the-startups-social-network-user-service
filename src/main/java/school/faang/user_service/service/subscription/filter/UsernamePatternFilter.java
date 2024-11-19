package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UsernamePatternFilter implements SubscriptionRequestFilter {


    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        String username = user.getUsername().toLowerCase().trim();
        String namePattern = userFilter.getNamePattern().toLowerCase().trim();

        return username.matches(".*" + namePattern + ".*");
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getNamePattern() != null;
    }
}
