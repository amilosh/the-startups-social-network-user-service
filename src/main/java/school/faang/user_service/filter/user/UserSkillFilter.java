package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserSkillFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getSkillPattern() != null && !filter.getSkillPattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String skillPattern = filter.getSkillPattern().toLowerCase();
        return users.filter(user -> user.getSkills() != null &&
                user.getSkills().stream()
                        .map(Skill::getTitle)
                        .filter(title -> title != null)
                        .anyMatch(title -> title.toLowerCase().contains(skillPattern)));
    }
}
