package school.faang.user_service.repository.filter;

import school.faang.user_service.entity.User;

public interface UserFilter {
    boolean filter(User user);
}
