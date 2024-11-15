package school.faang.user_service.service.subscription.filter;


import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

public interface SubscriptionRequestFilter {
    /**
     *
     * @param userFilter
     * @param user
     * @return True if user passed the filter, else False
     */
    Boolean applyFilter(SubscriptionUserFilterDto userFilter, User user);

    /**
     *
     * @param userFilter
     * @return True if user specified current filter in UserFilterDto, else False
     */
    Boolean isSpecifiedIn(SubscriptionUserFilterDto userFilter);
}
