package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserNameFilter implements UserFilter {
    @Override
    public boolean apply(User user, UserFilterDto filter) {
            return filter.getNamePattern() == null || (user.getUsername() != null && user.getUsername().matches(filter.getNamePattern()));
    }

}
