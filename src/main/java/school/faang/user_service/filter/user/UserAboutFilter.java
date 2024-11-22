package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserAboutFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getAboutPattern() != null && !filter.getAboutPattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String aboutPattern = filter.getAboutPattern().toLowerCase();
        return users.filter(user -> user.getAboutMe() != null &&
                user.getAboutMe().toLowerCase().contains(aboutPattern));
    }
}
