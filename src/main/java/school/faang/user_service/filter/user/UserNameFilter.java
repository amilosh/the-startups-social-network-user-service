package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getNamePattern() != null && !filter.getNamePattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String namePattern = filter.getNamePattern().toLowerCase();
        return users.filter(user -> user.getUsername() != null &&
                user.getUsername().toLowerCase().contains(namePattern));
    }
}
