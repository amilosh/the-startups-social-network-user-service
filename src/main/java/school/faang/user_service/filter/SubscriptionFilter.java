package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface SubscriptionFilter {

    boolean isApplicable(UserFilterDto filter);

    boolean apply(User user);
}
