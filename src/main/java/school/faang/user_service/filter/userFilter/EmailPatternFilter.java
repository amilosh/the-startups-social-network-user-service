package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class EmailPatternFilter implements UserFilter {
    private final String pattern;

    @Override
    public boolean apply(User user) {
        return user.getEmail() != null && user.getEmail().contains(pattern);
    }
}
