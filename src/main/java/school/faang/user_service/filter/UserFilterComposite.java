package school.faang.user_service.filter;

import school.faang.user_service.entity.User;
import school.faang.user_service.repository.filter.UserFilter;

import java.util.ArrayList;
import java.util.List;

public class UserFilterComposite implements UserFilter {
    private final List<UserFilter> filters;

    public UserFilterComposite() {
        this.filters = new ArrayList<>();
    }

    public void addFilter(UserFilter filter) {
        filters.add(filter);
    }

    @Override
    public boolean filter(User user) {
        return filters.stream().allMatch(filter -> filter.filter(user));
    }
}
