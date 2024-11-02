package school.faang.user_service.filter.userFilter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilter {

    boolean isApplicable(UserFilterDto filter);

    boolean apply(User user);
}
