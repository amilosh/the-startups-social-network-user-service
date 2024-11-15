package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class EmailPatternFilter implements SubscriptionRequestFilter {
    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        String userEmail = user.getEmail().toLowerCase();
        String emailPattern = userFilter.getEmailPattern().toLowerCase();

        return userEmail.matches(".*" + emailPattern + ".*");
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getEmailPattern() != null;
    }
}
