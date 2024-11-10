package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Collectors;

@Component
public class UserFilterSkill implements UserFilter {
    @Override
    public boolean apply(User user, UserFilterDto userFilterDto) {
        return userFilterDto.getSkillPattern() == null
                || user.getSkills().parallelStream()
                .anyMatch(skill -> skill.getTitle().contains(userFilterDto.getSkillPattern()));
    }
}