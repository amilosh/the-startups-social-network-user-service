package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class EmailPatternFilter implements SubscriptionFilter {
    private String pattern;

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        this.pattern = filterDto.emailPattern();
        return pattern != null && !pattern.isEmpty();
    }

    @Override
    public boolean apply(User user) {
        return user.getEmail() != null && user.getEmail().contains(pattern);
    }
}
