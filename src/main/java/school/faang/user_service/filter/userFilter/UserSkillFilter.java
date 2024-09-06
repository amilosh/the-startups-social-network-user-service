package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto userFilter) {
        return users.stream().filter(user -> user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle().equals(userFilter.getSkillPattern())));
    }
}
