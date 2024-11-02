package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class PhonePatternFilter implements UserFilter {
    private final String pattern;

    @Override
    public boolean apply(User user) {
        return user.getPhone() != null && user.getPhone().contains(pattern);
    }
}
