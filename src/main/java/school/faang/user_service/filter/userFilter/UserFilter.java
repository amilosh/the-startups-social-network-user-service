package school.faang.user_service.filter.userFilter;

import school.faang.user_service.entity.User;

public interface UserFilter {
    boolean apply(User user);
}
