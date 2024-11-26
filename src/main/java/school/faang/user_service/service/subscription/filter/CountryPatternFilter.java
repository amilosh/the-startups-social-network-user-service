package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class CountryPatternFilter implements SubscriptionRequestFilter {
    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        String userCountry = user.getCountry().getTitle().toLowerCase();
        String userCountryPattern = userFilter.getCountryPattern().toLowerCase();

        return userCountry.matches(".*" + userCountryPattern + ".*");
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getCountryPattern() != null;
    }
}
