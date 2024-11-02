package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class AboutPatternFilter implements UserFilter {
    private final String pattern;

    @Override
    public boolean apply(User user) {
        return pattern != null && user.getAboutMe().contains(pattern);
    }
}
