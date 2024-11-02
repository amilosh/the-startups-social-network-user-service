package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
public class PagePatternFilter implements UserFilter {
    @Override
    public boolean apply(User user) {
        return true;
    }
}
