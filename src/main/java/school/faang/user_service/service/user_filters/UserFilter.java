package school.faang.user_service.service.user_filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {

    public boolean isApplicable(UserFilterDto filter);

    public Stream<User> apply(Stream<User> users, UserFilterDto filter);
}
