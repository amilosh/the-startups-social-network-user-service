package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class MinExperiencePatternFilter implements UserFilter {
    private final int pattern;

    @Override
    public boolean apply(User user) {
        return user.getExperience() >= pattern;
    }
}
