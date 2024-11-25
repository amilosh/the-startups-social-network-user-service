package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class CityPatternFilter implements SubscriptionRequestFilter {
    @Override
    public Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user) {
        String userCity = user.getCity().toLowerCase();
        String userCityPattern = userFilter.getCityPattern().toLowerCase();

        return userCity.matches(".*" + userCityPattern + ".*");
    }

    @Override
    public Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter) {
        return userFilter.getCityPattern() != null;
    }
}
