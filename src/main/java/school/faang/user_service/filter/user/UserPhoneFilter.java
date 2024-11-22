package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserPhoneFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getPhonePattern() != null && !filter.getPhonePattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String phonePattern = filter.getPhonePattern();
        return users.filter(user -> user.getPhone() != null &&
                user.getPhone().contains(phonePattern));
    }
}
