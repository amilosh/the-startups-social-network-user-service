package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);
    boolean apply(User user);
}
