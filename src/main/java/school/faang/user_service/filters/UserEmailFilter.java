package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserEmailFilter implements UserFilter {
    @Override
    public boolean apply(User user, UserFilterDto filter) {
            return filter.getEmailPattern() == null || (user.getEmail() != null && user.getEmail().matches(filter.getEmailPattern()));
    }

}
