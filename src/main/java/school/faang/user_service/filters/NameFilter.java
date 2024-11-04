package school.faang.user_service.filters;

import school.faang.user_service.entity.User;
import school.faang.user_service.repository.filter.UserFilter;

public class NameFilter implements UserFilter {
    private final String namePattern;

    public NameFilter(String namePattern) {
        this.namePattern = namePattern;
    }

    @Override
    public boolean filter(User user) {
        return user.getUsername().toLowerCase().contains(namePattern.toLowerCase());
    }
}
