package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilterStrategy {
    boolean applyFilter(UserFilterDto filterDto);
    List<User> filter(List<User> users, UserFilterDto filterDto);
}
