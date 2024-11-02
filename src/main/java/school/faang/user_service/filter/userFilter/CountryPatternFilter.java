package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class CountryPatternFilter implements UserFilter {
    private final String pattern;

    @Override
    public boolean apply(User user) {
        return user.getCountry() != null && user.getCountry().getResidents().contains(pattern);
    }
}
