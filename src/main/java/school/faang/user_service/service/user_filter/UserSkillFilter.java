package school.faang.user_service.service.user_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserSkillFilter implements UserFilter {

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> user.getSkills().stream().
                anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern())));
    }
}
