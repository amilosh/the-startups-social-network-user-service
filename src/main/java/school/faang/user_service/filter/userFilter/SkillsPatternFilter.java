package school.faang.user_service.filter.userFilter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class SkillsPatternFilter implements UserFilter {
    private String pattern;

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        this.pattern = filterDto.skillPattern();
        return pattern != null && !pattern.isEmpty();
    }

    @Override
    public boolean apply(User user) {
        return user.getSkills() != null && user.getSkills().contains(pattern);
    }
}
