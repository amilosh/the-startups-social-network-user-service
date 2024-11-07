package school.faang.user_service.service.user.user_filters;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);
    List<User> apply(List<User> users, UserFilterDto filters);
}
