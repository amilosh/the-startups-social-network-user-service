package school.faang.user_service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilter {
    boolean apply(User user, UserFilterDto filter);

    static boolean applyAllFilters(List<UserFilter> filters, User user, UserFilterDto userFilterDto) {
        for (var filter : filters) {
            if (!filter.apply(user, userFilterDto)) {
                return false;
            }
        }
        return true;
    }

}
