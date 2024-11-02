package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class SkillsPatternFilter implements UserFilter {
    private final Skill pattern;

    @Override
    public boolean apply(User user) {
        return user.getSkills() != null && user.getSkills().contains(pattern);
    }
}
