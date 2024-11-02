package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class CityPatternFilter implements UserFilter {
    private final String pattern;

    @Override
    public boolean apply(User user) {
        return user.getCity() != null && user.getCity().contains(pattern);
    }
}
